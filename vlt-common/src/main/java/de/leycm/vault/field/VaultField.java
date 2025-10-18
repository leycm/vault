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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class VaultField<T> implements Field<T> {

    private final @NonNull Config config;
    private final @NonNull String path;
    private final @NonNull Class<T> type;

    @Override
    public @NonNull Optional<T> getOptional() {
        return config.getOptional(path, type);
    }

    @Override
    public void set(@Nullable T value) {
        config.set(path, value);
    }

}