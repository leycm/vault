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

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public interface Field<T> {

    @Nullable
    default T get() {
        return getOptional().orElse(null);
    }

    @NonNull
    default T getOr(final @NonNull T def) {
        return getOptional().orElse(def);
    }

    @NonNull
    default T getOr(Supplier<T> supplier) {
        return getOptional().orElseGet(supplier);
    }

    @NonNull
    default <X extends Throwable> T getOr(final @NonNull X throwable) throws X {
        return getOptional().orElseThrow(() -> throwable);
    }

    @NonNull
    Optional<T> getOptional();

    default void remove() {
        set(null);
    }

    void set(final @Nullable T value);

    default boolean exists() {
        return getOptional().isPresent();
    }

}
