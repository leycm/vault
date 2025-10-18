package de.leycm.vault.adapter.file;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import de.leycm.vault.adapter.ConfigFileAdapter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TOML adapter with intelligent comment preservation
 * Preserves comments by position and key/section association
 */
public class TomlConfigAdapter implements ConfigFileAdapter {

    private final TomlWriter writer;

     
    private static final Pattern SECTION_PATTERN = Pattern.compile("^\\[(.*?)\\]\\s*$");
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^([a-zA-Z0-9_-]+)\\s*=\\s*(.*)$");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("^\\s*#(.*)$");
    private static final Pattern INLINE_COMMENT_PATTERN = Pattern.compile("^(.*?)\\s*#(.*)$");

    public TomlConfigAdapter() {
        this.writer = new TomlWriter.Builder()
                .indentValuesBy(2)
                .indentTablesBy(0)
                .padArrayDelimitersBy(1)
                .build();
    }

    @Override
    public Map<String, Object> read(String content) throws IOException {
        if (content == null || content.trim().isEmpty()) {
            return new LinkedHashMap<>();
        }

        try {
            Toml toml = new Toml().read(content);
            return toml.toMap();
        } catch (Exception e) {
            throw new IOException("Invalid TOML: " + e.getMessage(), e);
        }
    }

    @Override
    public String write(String current, Map<String, Object> data) {
         
        if (current == null || current.trim().isEmpty()) {
            return writer.write(data);
        }

         
        TomlCommentMap commentMap = parseComments(current);

         
        String newToml = writer.write(data);

         
        return mergeCommentsWithToml(newToml, commentMap);
    }

    @Override
    public String updateValue(String current, String key, Object value) throws IOException {
        Map<String, Object> data = read(current);
        setNestedValue(data, key, value);
        return write(current, data);
    }

    private @NonNull TomlCommentMap parseComments(@NonNull String content) {
        TomlCommentMap map = new TomlCommentMap();
        String[] lines = content.split("\n");

        List<String> pendingComments = new ArrayList<>();
        String currentSection = "";  
        Map<String, String> inlineComments = new LinkedHashMap<>();

        for (String line : lines) {
            String trimmed = line.trim();

             
            if (trimmed.isEmpty()) continue;
             
            if (trimmed.startsWith("#")) {
                pendingComments.add(line);
                continue;
            }

             
            Matcher sectionMatcher = SECTION_PATTERN.matcher(trimmed);
            if (sectionMatcher.matches()) {
                String sectionName = sectionMatcher.group(1);

                 
                if (!pendingComments.isEmpty()) {
                    map.addSectionComments(sectionName, pendingComments);
                    pendingComments = new ArrayList<>();
                }

                currentSection = sectionName;
                continue;
            }

             
            Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(trimmed);
            if (keyValueMatcher.matches()) {
                String key = keyValueMatcher.group(1);
                String fullKey = currentSection.isEmpty() ? key : currentSection + "." + key;

                 
                Matcher inlineMatcher = INLINE_COMMENT_PATTERN.matcher(line);
                if (inlineMatcher.matches() && inlineMatcher.group(2) != null) {
                    String inlineComment = "#" + inlineMatcher.group(2).trim();
                    inlineComments.put(fullKey, inlineComment);
                }

                 
                if (!pendingComments.isEmpty()) {
                    map.addKeyComments(fullKey, pendingComments);
                    pendingComments = new ArrayList<>();
                }
            }
        }

         
        if (!pendingComments.isEmpty()) {
            map.setHeaderComments(pendingComments);
        }

        map.setInlineComments(inlineComments);
        return map;
    }

    private @NonNull String mergeCommentsWithToml(@NonNull String tomlContent, @NonNull TomlCommentMap commentMap) {
        String[] lines = tomlContent.split("\n");
        StringBuilder result = new StringBuilder();

         
        List<String> headerComments = commentMap.getHeaderComments();
        if (headerComments != null && !headerComments.isEmpty()) {
            for (String comment : headerComments) {
                result.append(comment).append("\n");
            }
            result.append("\n");
        }

        String currentSection = "";
        boolean firstSection = true;

        for (String line : lines) {
            String trimmed = line.trim();

             
            if (trimmed.isEmpty()) {
                result.append(line).append("\n");
                continue;
            }

             
            Matcher sectionMatcher = SECTION_PATTERN.matcher(trimmed);
            if (sectionMatcher.matches()) {
                String sectionName = sectionMatcher.group(1);
                currentSection = sectionName;

                 
                if (!firstSection) {
                    result.append("\n");
                }
                firstSection = false;

                 
                List<String> sectionComments = commentMap.getSectionComments(sectionName);
                if (sectionComments != null && !sectionComments.isEmpty()) {
                    for (String comment : sectionComments) {
                        result.append(comment).append("\n");
                    }
                }

                result.append(line).append("\n");
                continue;
            }

             
            Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(trimmed);
            if (keyValueMatcher.matches()) {
                String key = keyValueMatcher.group(1);
                String fullKey = currentSection.isEmpty() ? key : currentSection + "." + key;

                 
                List<String> keyComments = commentMap.getKeyComments(fullKey);
                if (keyComments != null && !keyComments.isEmpty()) {
                    for (String comment : keyComments) {
                        result.append(comment).append("\n");
                    }
                }

                 
                String inlineComment = commentMap.getInlineComment(fullKey);
                if (inlineComment != null) {
                     
                    String cleanLine = line;
                    Matcher inlineMatcher = INLINE_COMMENT_PATTERN.matcher(line);
                    if (inlineMatcher.matches()) {
                        cleanLine = inlineMatcher.group(1).trim();
                    }
                    result.append(cleanLine).append("  ").append(inlineComment).append("\n");
                } else {
                    result.append(line).append("\n");
                }
                continue;
            }

            result.append(line).append("\n");
        }

        return result.toString();
    }

    @SuppressWarnings("unchecked")
    private void setNestedValue(Map<String, Object> data, @NotNull String key, Object value) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = data;

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                Map<String, Object> newMap = new LinkedHashMap<>();
                current.put(parts[i], newMap);
                current = newMap;
            } else {
                current = (Map<String, Object>) next;
            }
        }

        current.put(parts[parts.length - 1], value);
    }


    private static class TomlCommentMap {
        private final Map<String, List<String>> sectionComments = new LinkedHashMap<>();
        private final Map<String, List<String>> keyComments = new LinkedHashMap<>();
        private final Map<String, String> inlineComments = new LinkedHashMap<>();
        private List<String> headerComments = new ArrayList<>();

        void addSectionComments(String section, List<String> comments) {
            sectionComments.put(section, new ArrayList<>(comments));
        }

        void addKeyComments(String key, List<String> comments) {
            keyComments.put(key, new ArrayList<>(comments));
        }

        void setInlineComments(Map<String, String> comments) {
            this.inlineComments.putAll(comments);
        }

        List<String> getSectionComments(String section) {
            return sectionComments.get(section);
        }

        List<String> getKeyComments(String key) {
            return keyComments.get(key);
        }

        String getInlineComment(String key) {
            return inlineComments.get(key);
        }

        void setHeaderComments(List<String> comments) {
            this.headerComments = new ArrayList<>(comments);
        }

        List<String> getHeaderComments() {
            return headerComments;
        }
    }
}