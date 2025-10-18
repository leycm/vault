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

import java.util.*;
import java.util.function.Supplier;

public interface FieldList<T> extends Field<List<T>> {

    @Nullable
    default T get(int i) {
        return getOptional(i).orElse(null);
    }

    default T getOr(int i, final @NonNull T def) {
        return getOptional(i).orElse(def);
    }

    default T getOr(int i,
                    final @NonNull Supplier<T> supplier) {
        return getOptional(i).orElseGet(supplier);
    }

    default <X extends Throwable> T getOr(int i, final @NonNull X throwable) throws X {
        return getOptional(i).orElseThrow(() -> throwable);
    }

    default Optional<T> getOptional(int i) {
        List<T> it = get();
        if (it == null) return Optional.empty();
        return Optional.of(it.get(i));
    }

    default void set(int i, final T value) {
        List<T> it = getOr(new ArrayList<>()); set(it);
        if (value != null) {
            it.set(i, value);
            return;
        } it.remove(i);
    }

    default void add(final @NonNull T value) {
        List<T> it = getOr(new ArrayList<>()); set(it);
        it.add(value);
    }

    default void add(int i, final @NonNull T value) {
        List<T> it = getOr(new ArrayList<>()); set(it);
        it.add(i, value);
    }

    default void remove(int i) {
        set(i, null);
    }

}
