package de.leycm.vault.adapter.file;

import de.leycm.vault.adapter.ConfigFileAdapter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YamlConfigAdapter implements ConfigFileAdapter {

    private final Yaml yaml;
    private static final Pattern KEY_PATTERN = Pattern.compile("^(\\s*)([a-zA-Z0-9_-]+):\\s*(.*)$");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("^(\\s*)#(.*)$");

    public YamlConfigAdapter() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        options.setWidth(120); // Verhindert unnötiges Line-Wrapping
        this.yaml = new Yaml(options);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> read(String content) throws IOException {
        if (content == null || content.trim().isEmpty()) {
            return new LinkedHashMap<>();
        }

        try {
            Object loaded = yaml.load(content);
            if (loaded instanceof Map) {
                return (Map<String, Object>) loaded;
            }
            return new LinkedHashMap<>();
        } catch (Exception e) {
            throw new IOException("Invalid YAML", e);
        }
    }

    @Override
    public String write(String current, Map<String, Object> data) {
        if (current == null || current.trim().isEmpty()) {
            return yaml.dump(data);
        }

        CommentMap commentMap = parseComments(current);

        String newYaml = yaml.dump(data);

        return mergeCommentsWithYaml(newYaml, commentMap);
    }

    @Override
    public String updateValue(String current, String key, Object value) throws IOException {
        Map<String, Object> data = read(current);
        setNestedValue(data, key, value);
        return write(current, data);
    }

    // ==================== Comment Preservation Logic ====================

    private @NotNull CommentMap parseComments(@NotNull String content) {
        CommentMap map = new CommentMap();
        String[] lines = content.split("\n");

        List<String> pendingComments = new ArrayList<>();
        String lastKey = null;
        int lastIndent = -1;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
            Matcher keyMatcher = KEY_PATTERN.matcher(line);

            if (commentMatcher.matches()) {
                pendingComments.add(line);
            } else if (keyMatcher.matches()) {
                // Key gefunden
                int indent = keyMatcher.group(1).length();
                String key = keyMatcher.group(2);

                // Bestimme vollständigen Pfad basierend auf Indent
                String fullKey = buildKeyPath(key, indent, lastKey, lastIndent);

                // Füge pending Kommentare diesem Key hinzu
                if (!pendingComments.isEmpty()) {
                    map.addComments(fullKey, pendingComments);
                    pendingComments = new ArrayList<>();
                }

                lastKey = fullKey;
                lastIndent = indent;
            }
        }

        // Header-Kommentare (ganz oben, vor erstem Key)
        if (!pendingComments.isEmpty()) {
            map.setHeaderComments(pendingComments);
        }

        return map;
    }

    /**
     * Baut den vollständigen Key-Pfad basierend auf Indentation
     */
    private String buildKeyPath(String currentKey, int currentIndent, String lastKey, int lastIndent) {
        if (lastKey == null || currentIndent == 0) {
            return currentKey;
        }

        if (currentIndent > lastIndent) {
            // Tiefer verschachtelt
            return lastKey + "." + currentKey;
        } else if (currentIndent == lastIndent) {
            // Gleiche Ebene
            int lastDot = lastKey.lastIndexOf('.');
            if (lastDot == -1) {
                return currentKey;
            }
            return lastKey.substring(0, lastDot + 1) + currentKey;
        } else {
            // Weniger verschachtelt - zurück zur höheren Ebene
            String[] parts = lastKey.split("\\.");
            int levels = (lastIndent - currentIndent) / 2;
            int newDepth = Math.max(0, parts.length - levels - 1);

            if (newDepth == 0) {
                return currentKey;
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < newDepth; i++) {
                if (i > 0) result.append(".");
                result.append(parts[i]);
            }
            result.append(".").append(currentKey);
            return result.toString();
        }
    }

    private @NonNull String mergeCommentsWithYaml(@NonNull String yamlContent, @NonNull CommentMap commentMap) {
        String[] lines = yamlContent.split("\n");
        StringBuilder result = new StringBuilder();

        List<String> headerComments = commentMap.getHeaderComments();
        if (headerComments != null && !headerComments.isEmpty()) {
            for (String comment : headerComments) {
                result.append(comment).append("\n");
            }
        }

        String currentPath = "";
        int lastIndent = -1;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                result.append(line).append("\n");
                continue;
            }

            Matcher keyMatcher = KEY_PATTERN.matcher(line);

            if (keyMatcher.matches()) {
                int indent = keyMatcher.group(1).length();
                String key = keyMatcher.group(2);

                currentPath = buildKeyPath(key, indent, currentPath, lastIndent);
                lastIndent = indent;

                List<String> comments = commentMap.getComments(currentPath);
                if (comments != null && !comments.isEmpty()) {
                    for (String comment : comments) {
                        result.append(comment).append("\n");
                    }
                }
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

    // ==================== Helper Classes ====================

    private static class CommentMap {
        private final Map<String, List<String>> commentsByKey = new LinkedHashMap<>();
        private List<String> headerComments = new ArrayList<>();

        void addComments(String key, List<String> comments) {
            commentsByKey.put(key, new ArrayList<>(comments));
        }

        List<String> getComments(String key) {
            return commentsByKey.get(key);
        }

        void setHeaderComments(List<String> comments) {
            this.headerComments = new ArrayList<>(comments);
        }

        List<String> getHeaderComments() {
            return headerComments;
        }
    }
}