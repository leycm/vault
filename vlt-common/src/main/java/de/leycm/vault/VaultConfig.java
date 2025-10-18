/**
 * LECP-LICENSE NOTICE
 * <br><br>
 * This Sourcecode is under the LECP-LICENSE. <br>
 * License at: <a href="https://github.com/leycm/leycm/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) LeyCM <leycm@proton.me> <br>
 * Copyright (c) maintainers <br>
 * Copyright (c) contributors
 */
package de.leycm.vault;

import de.leycm.vault.adapter.TypeAdapter;
import de.leycm.vault.field.Field;
import de.leycm.vault.field.FieldList;
import de.leycm.vault.field.FieldSection;
import de.leycm.vault.field.VaultField;
import de.leycm.vault.field.VaultFieldList;
import de.leycm.vault.field.VaultFieldSection;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public record VaultConfig(@NonNull File file, @NonNull Map<String, Object> data,
                          @NonNull ConfigFactory factory) implements Config {

    private static final String PATH_SEPARATOR = ".";

    @Override
    public @NonNull <T> Optional<T> getOptional(@NonNull String path, @NonNull Class<T> type) {
        Object value = navigateToValue(path);

        if (value == null) {
            return Optional.empty();
        }

        return convertValue(value, type, path);
    }

    @Override
    public @NonNull <T> Field<T> getField(@NonNull String path, @NonNull Class<T> type) {
        return new VaultField<>(this, path, type);
    }

    @Override
    public @NonNull <T> FieldList<T> getFieldList(@NonNull String path, @NonNull Class<T> type) {
        return new VaultFieldList<>(this, path, type);
    }

    @Override
    public @NonNull FieldSection getFieldSection(@NonNull String path) {
        return new VaultFieldSection(this, path);
    }

    @Override
    public <T> void set(@NonNull String path, @Nullable T value) {
        String[] parts = splitPath(path);
        Map<String, Object> target = navigateToParent(parts);
        String finalKey = parts[parts.length - 1];

        if (value == null) {
            target.remove(finalKey);
            return;
        }

        Object objectToStore = convertToStorable(value, path);
        target.put(finalKey, objectToStore);
    }

    @Override
    public boolean contains(@NonNull String path) {
        try {
            Object value = navigateToValue(path);
            return value != null;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Private Helper Methods ====================

    @Nullable
    private Object navigateToValue(@NonNull String path) {
        String[] parts = splitPath(path);
        Object current = data;

        for (String key : parts) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }

            if (!map.containsKey(key)) {
                return null;
            }

            current = map.get(key);
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> navigateToParent(String @NonNull [] parts) {
        Map<String, Object> current = data;

        for (int i = 0; i < parts.length - 1; i++) {
            String key = parts[i];
            Object next = current.get(key);

            if (!(next instanceof Map)) {
                Map<String, Object> newMap = new LinkedHashMap<>();
                current.put(key, newMap);
                current = newMap;
            } else {
                current = (Map<String, Object>) next;
            }
        }

        return current;
    }

    private <T> Optional<T> convertValue(Object value, Class<T> type, String path) {
        if (factory instanceof VaultFactory vaultFactory) {
            TypeAdapter<T> adapter = vaultFactory.getTypeAdapter(type);

            if (adapter != null) {
                try {
                    T converted = adapter.fromObject(this, path, value);
                    return Optional.ofNullable(converted);
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        }

        if (type.isInstance(value))
            return Optional.of(type.cast(value));

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <T> Object convertToStorable(T value, String path) {
        if (factory instanceof VaultFactory vaultFactory) {
            TypeAdapter<T> adapter = (TypeAdapter<T>) vaultFactory.getTypeAdapter(value.getClass());

            if (adapter != null) {
                try {
                    return adapter.toObject(this, path, value);
                } catch (Exception ignored) {}
            }
        }

        return value;
    }

    @Contract(pure = true)
    private String @NotNull [] splitPath(@NonNull String path) {
        return path.split("\\" + PATH_SEPARATOR);
    }
}