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
package de.leycm.vault.field;

import de.leycm.vault.Config;
import org.jetbrains.annotations.Nullable;

import lombok.NonNull;
import java.io.File;
import java.util.Map;
import java.util.Optional;

public class VaultFieldSection extends VaultField<Map<String, Object>> implements FieldSection {

    private static final String PATH_SEPARATOR = ".";

    public VaultFieldSection(@NonNull Config config, @NonNull String path) {
        //noinspection unchecked
        super(config, path, (Class<Map<String, Object>>) (Class<?>) Map.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull Optional<Map<String, Object>> getOptional() {
        Optional<Object> raw = getConfig().getOptional(getPath(), Object.class);

        return raw.filter(value -> value instanceof Map)
                .map(value -> (Map<String, Object>) value);
    }

    @Override
    public void set(@Nullable Map<String, Object> value) {
        getConfig().set(getPath(), value);
    }

    @Override
    public @NonNull <T> Optional<T> getOptional(@NonNull String path, @NonNull Class<T> type) {
        return getConfig().getOptional(combinePath(path), type);
    }

    @Override
    public @NonNull <T> Field<T> getField(@NonNull String path, @NonNull Class<T> type) {
        return new VaultField<>(getConfig(), combinePath(path), type);
    }

    @Override
    public @NonNull <T> FieldList<T> getFieldList(@NonNull String path, @NonNull Class<T> type) {
        return new VaultFieldList<>(getConfig(), combinePath(path), type);
    }

    @Override
    public @NonNull FieldSection getFieldSection(@NonNull String path) {
        return new VaultFieldSection(getConfig(), combinePath(path));
    }

    @Override
    public void set(@NonNull String path, @Nullable Object value) {
        getConfig().set(combinePath(path), value);
    }

    @Override
    public boolean contains(@NonNull String path) {
        return getConfig().contains(combinePath(path));
    }

    @Override
    public @NonNull File file() {
        return getConfig().file();
    }

    private String combinePath(String subPath) {
        if (getPath().isEmpty()) return subPath;
        if (subPath.isEmpty()) return getPath();
        return getPath() + PATH_SEPARATOR + subPath;
    }
}