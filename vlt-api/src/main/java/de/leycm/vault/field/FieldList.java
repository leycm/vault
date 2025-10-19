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

/**
 * Represents a list field in the configuration with element-level operations.
 * Extends {@link Field} to provide list-specific functionality.
 *
 * @param <T> the type of elements in the list
 * @author LeyCM
 * @since 1.0.2
 * @see Config#getFieldList(String, Class)
 * @see Field
 */
public interface FieldList<T> extends Field<List<T>> {

    /**
     * Retrieves the element at the specified position, or null if the list doesn't exist or index is out of bounds.
     *
     * @param i the index of the element to retrieve
     * @return the element at the specified position, or null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional(int)
     */
    @Nullable
    default T get(int i) {
        return getOptional(i).orElse(null);
    }

    /**
     * Retrieves the element at the specified position, or returns the default value if not present.
     *
     * @param i the index of the element to retrieve
     * @param def the default value to return
     * @return the element or the default value
     * @throws NullPointerException if def is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional(int)
     */
    default T getOr(int i, final @NonNull T def) {
        return getOptional(i).orElse(def);
    }

    /**
     * Retrieves the element at the specified position, or supplies a default value if not present.
     *
     * @param i the index of the element to retrieve
     * @param supplier the supplier that provides the default value
     * @return the element or the supplied default value
     * @throws NullPointerException if supplier is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional(int)
     */
    default T getOr(int i,
                    final @NonNull Supplier<T> supplier) {
        return getOptional(i).orElseGet(supplier);
    }

    /**
     * Retrieves the element at the specified position, or throws the provided exception if not present.
     *
     * @param <X> the type of the exception to throw
     * @param i the index of the element to retrieve
     * @param throwable the exception to throw if the element is not present
     * @return the element at the specified position
     * @throws X if the element is not present
     * @throws NullPointerException if throwable is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional(int)
     */
    default <X extends Throwable> T getOr(int i, final @NonNull X throwable) throws X {
        return getOptional(i).orElseThrow(() -> throwable);
    }

    /**
     * Retrieves the element at the specified position as an Optional.
     *
     * @param i the index of the element to retrieve
     * @return an {@link Optional} containing the element if present, empty otherwise
     * @author LeyCM
     * @since 1.0.2
     * @see Optional
     */
    default Optional<T> getOptional(int i) {
        List<T> it = get();
        if (it == null) return Optional.empty();
        return Optional.of(it.get(i));
    }

    /**
     * Sets the element at the specified position.
     * If the value is null, the element is removed from the list.
     *
     * @param i the index at which to set the element
     * @param value the value to set, or null to remove the element
     * @author LeyCM
     * @since 1.0.2
     */
    default void set(int i, final T value) {
        List<T> it = getOr(new ArrayList<>()); set(it);
        if (value != null) {
            it.set(i, value);
            return;
        } it.remove(i);
    }

    /**
     * Adds an element to the end of the list.
     *
     * @param value the value to add
     * @throws NullPointerException if value is null
     * @author LeyCM
     * @since 1.0.2
     */
    default void add(final @NonNull T value) {
        List<T> it = getOr(new ArrayList<>()); set(it);
        it.add(value);
    }

    /**
     * Inserts an element at the specified position in the list.
     *
     * @param i the index at which to insert the element
     * @param value the value to insert
     * @throws NullPointerException if value is null
     * @author LeyCM
     * @since 1.0.2
     */
    default void add(int i, final @NonNull T value) {
        List<T> it = getOr(new ArrayList<>()); set(it);
        it.add(i, value);
    }

    /**
     * Removes the element at the specified position.
     *
     * @param i the index of the element to remove
     * @author LeyCM
     * @since 1.0.2
     * @see #set(int, Object)
     */
    default void remove(int i) {
        set(i, null);
    }

}