package de.leycm.vault.adapter.file;

import com.google.gson.*;
import de.leycm.vault.adapter.ConfigFileAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonConfigAdapter implements ConfigFileAdapter {
    private final Gson prettyGson;

    public JsonConfigAdapter() {
        this.prettyGson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> read(String content) throws IOException {
        if (content == null || content.trim().isEmpty()) {
            return new LinkedHashMap<>();
        }

        try {
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            return (Map<String, Object>) convertJsonElement(json);
        } catch (JsonSyntaxException e) {
            throw new IOException("Invalid JSON", e);
        }
    }

    @Override
    public String write(String current, Map<String, Object> data) throws IOException {
        return prettyGson.toJson(data);
    }

    @Override
    public String updateValue(String current, String key, Object value) throws IOException {
        Map<String, Object> data = read(current);
        setNestedValue(data, key, value);
        return write(current, data);
    }

    private @Nullable Object convertJsonElement(@NotNull JsonElement element) {
        if (element.isJsonNull()) {
            return null;
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isNumber()) {
                Number number = primitive.getAsNumber();
                if (number.doubleValue() == number.longValue()) return number.longValue();
                return number.doubleValue();
            } else {
                return primitive.getAsString();
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            java.util.List<Object> list = new java.util.ArrayList<>();
            for (JsonElement item : array) {
                list.add(convertJsonElement(item));
            }
            return list;
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            Map<String, Object> map = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                map.put(entry.getKey(), convertJsonElement(entry.getValue()));
            }
            return map;
        }
        return null;
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
}
