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

import de.leycm.vault.*;
import de.leycm.vault.adapter.TypeAdapter;
import org.jetbrains.annotations.*;

import lombok.Getter;
import lombok.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class VaultFieldList<T> extends VaultField<List<T>> implements FieldList<T> {

    private final Class<T> elementType;

    @SuppressWarnings("unchecked")
    public VaultFieldList(@NonNull Config config, @NonNull String path, @NonNull Class<T> elementType) {
        super(config, path, (Class<List<T>>) (Class<?>) List.class);
        this.elementType = elementType;
    }

    @Override
    public @NonNull Optional<List<T>> getOptional() {
        Optional<Object> raw = getConfig().getOptional(getPath(), Object.class);

        if (raw.isEmpty()) return Optional.empty();


        Object value = raw.get();

        if (!(value instanceof List<?> rawList))
            return Optional.empty();

        return convertList(rawList);
    }

    @Override
    public void set(@Nullable List<T> value) {
        getConfig().set(getPath(), value);
    }

    // ==================== Private Helper Methods ====================

    private Optional<List<T>> convertList(@NotNull List<?> rawList) {
        List<T> result = new ArrayList<>(rawList.size());

        for (Object item : rawList) {
            Optional<T> converted = convertItem(item);

            if (converted.isEmpty() && item != null)
                return Optional.empty();

            result.add(converted.orElse(null));
        }

        return Optional.of(result);
    }

    private Optional<T> convertItem(@Nullable Object item) {
        if (item == null)
            return Optional.empty();

        if (elementType.isInstance(item))
            return Optional.of(elementType.cast(item));

        return tryAdapterConversion(item);
    }

    private Optional<T> tryAdapterConversion(Object item) {
        Config config = getConfig();

        if (!(config instanceof VaultConfig vaultConfig))
            return Optional.empty();

        if (!(vaultConfig.factory() instanceof VaultFactory factory))
            return Optional.empty();

        TypeAdapter<T> adapter = factory.getTypeAdapter(elementType);

        if (adapter == null) return Optional.empty();

        try {
            T converted = adapter.fromObject(config, getPath(), item);
            return Optional.ofNullable(converted);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}