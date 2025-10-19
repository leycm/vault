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

/**
 * Represents a configuration field with type-safe access and manipulation methods.
 * Provides various strategies for retrieving values with fallbacks.
 *
 * @param <T> the type of the field value
 * @author LeyCM
 * @since 1.0.2
 * @see Config#getField(String, Class)
 */
public interface Field<T> {

    /**
     * Retrieves the field value, or null if the field doesn't exist.
     *
     * @return the field value, or null if not present
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional()
     */
    @Nullable
    default T get() {
        return getOptional().orElse(null);
    }

    /**
     * Retrieves the field value, or returns the default value if not present.
     *
     * @param def the default value to return
     * @return the field value or the default value
     * @throws NullPointerException if def is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional()
     */
    @NonNull
    default T getOr(final @NonNull T def) {
        return getOptional().orElse(def);
    }

    /**
     * Retrieves the field value, or supplies a default value if not present.
     *
     * @param supplier the supplier that provides the default value
     * @return the field value or the supplied default value
     * @throws NullPointerException if supplier is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional()
     */
    @NonNull
    default T getOr(Supplier<T> supplier) {
        return getOptional().orElseGet(supplier);
    }

    /**
     * Retrieves the field value, or throws the provided exception if not present.
     *
     * @param <X> the type of the exception to throw
     * @param throwable the exception to throw if the value is not present
     * @return the field value
     * @throws X if the field value is not present
     * @throws NullPointerException if throwable is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional()
     */
    @NonNull
    default <X extends Throwable> T getOr(final @NonNull X throwable) throws X {
        return getOptional().orElseThrow(() -> throwable);
    }

    /**
     * Retrieves the field value as an Optional.
     *
     * @return an {@link Optional} containing the value if present, empty otherwise
     * @author LeyCM
     * @since 1.0.2
     * @see Optional
     */
    @NonNull
    Optional<T> getOptional();

    /**
     * Removes the field value by setting it to null.
     *
     * @author LeyCM
     * @since 1.0.2
     * @see #set(Object)
     */
    default void remove() {
        set(null);
    }

    /**
     * Sets the field value.
     *
     * @param value the value to set, or null to remove the value
     * @author LeyCM
     * @since 1.0.2
     */
    void set(final @Nullable T value);

    /**
     * Checks if the field exists (has a non-null value).
     *
     * @return true if the field exists, false otherwise
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional()
     */
    default boolean exists() {
        return getOptional().isPresent();
    }

}