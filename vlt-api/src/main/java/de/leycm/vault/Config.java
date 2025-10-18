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

import de.leycm.vault.field.*;
import org.jetbrains.annotations.Nullable;

import lombok.NonNull;
import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

public interface Config {

    static Config of(final @NonNull String filename) {
        return ConfigFactory.getInstance().create(filename);
    }

    @Nullable
    default <T> T get(final @NonNull String path,
                       final @NonNull Class<T> type) {
        return getOptional(path, type).orElse(null);
    }

    @NonNull
    default <T> T getOr(final @NonNull String path,
                         final @NonNull Class<T> type,
                         final @NonNull T def) {
        return getOptional(path, type).orElse(def);
    }

    @NonNull
    default <T> T getOr(final @NonNull String path,
                         final @NonNull Class<T> type,
                         final @NonNull Supplier<T> supplier) {
        return getOptional(path, type).orElseGet(supplier);
    }

    @NonNull
    default <T, X extends Throwable> T getOr(final @NonNull String path,
                        final @NonNull Class<T> type,
                        final @NonNull X throwable) throws X {
        return getOptional(path, type).orElseThrow(() -> throwable);
    }

    @NonNull
    <T> Optional<T> getOptional(final @NonNull String path,
                                final @NonNull Class<T> type);


    @NonNull
    <T> Field<T> getField(final @NonNull String path,
                          final @NonNull Class<T> type);


    @NonNull
    <T> FieldList<T> getFieldList(final @NonNull String path,
                                  final @NonNull Class<T> type);
    @NonNull
    FieldSection getFieldSection(final @NonNull String path);

    default void remove(final @NonNull String path) {
        set(path, null);
    }

    <T> void set(final @NonNull String path,
             final @Nullable T value);

    boolean contains(final @NonNull String path);

    default void reload() {
        ConfigFactory.getInstance().reload(this);
    }

    default void save() {
        ConfigFactory.getInstance().save(this);
    }

    @NonNull
    File file();

}