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

/**
 * Main configuration interface providing methods to access and manipulate configuration values.
 * Supports type-safe retrieval of configuration values with various fallback strategies.
 *
 * @author LeyCM
 * @since 1.0.2
 * @see ConfigFactory
 * @see Field
 * @see FieldList
 * @see FieldSection
 */
public interface Config {

    /**
     * Creates a new configuration instance for the specified filename.
     * The file will be located in the default configuration directory.
     *
     * @param filename the name of the configuration file
     * @return a new {@link Config} instance
     * @throws NullPointerException if filename is null
     * @author LeyCM
     * @since 1.0.2
     * @see ConfigFactory#create(String)
     */
    static Config of(final @NonNull String filename) {
        return ConfigFactory.getInstance().create(filename);
    }

    /**
     * Retrieves a configuration value at the specified path, or null if not found.
     *
     * @param <T> the type of the configuration value
     * @param path the configuration path (e.g., "database.host")
     * @param type the class of the expected value type
     * @return the configuration value, or null if not found
     * @throws NullPointerException if path or type is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional(String, Class)
     */
    @Nullable
    default <T> T get(final @NonNull String path,
                      final @NonNull Class<T> type) {
        return getOptional(path, type).orElse(null);
    }

    /**
     * Retrieves a configuration value at the specified path, or returns the default value if not found.
     *
     * @param <T> the type of the configuration value
     * @param path the configuration path
     * @param type the class of the expected value type
     * @param def the default value to return if the path doesn't exist
     * @return the configuration value or the default value
     * @throws NullPointerException if path, type, or def is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional(String, Class)
     */
    @NonNull
    default <T> T getOr(final @NonNull String path,
                        final @NonNull Class<T> type,
                        final @NonNull T def) {
        return getOptional(path, type).orElse(def);
    }

    /**
     * Retrieves a configuration value at the specified path, or supplies a default value if not found.
     *
     * @param <T> the type of the configuration value
     * @param path the configuration path
     * @param type the class of the expected value type
     * @param supplier the supplier that provides the default value
     * @return the configuration value or the supplied default value
     * @throws NullPointerException if path, type, or supplier is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional(String, Class)
     */
    @NonNull
    default <T> T getOr(final @NonNull String path,
                        final @NonNull Class<T> type,
                        final @NonNull Supplier<T> supplier) {
        return getOptional(path, type).orElseGet(supplier);
    }

    /**
     * Retrieves a configuration value at the specified path, or throws the provided exception if not found.
     *
     * @param <T> the type of the configuration value
     * @param <X> the type of the exception to throw
     * @param path the configuration path
     * @param type the class of the expected value type
     * @param throwable the exception to throw if the value is not found
     * @return the configuration value
     * @throws X if the configuration value is not found
     * @throws NullPointerException if path, type, or throwable is null
     * @author LeyCM
     * @since 1.0.2
     * @see #getOptional(String, Class)
     */
    @NonNull
    default <T, X extends Throwable> T getOr(final @NonNull String path,
                                             final @NonNull Class<T> type,
                                             final @NonNull X throwable) throws X {
        return getOptional(path, type).orElseThrow(() -> throwable);
    }

    /**
     * Retrieves an optional configuration value at the specified path.
     *
     * @param <T> the type of the configuration value
     * @param path the configuration path
     * @param type the class of the expected value type
     * @return an {@link Optional} containing the value if present, empty otherwise
     * @throws NullPointerException if path or type is null
     * @author LeyCM
     * @since 1.0.2
     * @see Optional
     */
    @NonNull
    <T> Optional<T> getOptional(final @NonNull String path,
                                final @NonNull Class<T> type);

    /**
     * Retrieves a configuration field at the specified path.
     * A field provides additional operations on the configuration value.
     *
     * @param <T> the type of the configuration value
     * @param path the configuration path
     * @param type the class of the expected value type
     * @return a {@link Field} instance for the specified path
     * @throws NullPointerException if path or type is null
     * @author LeyCM
     * @since 1.0.2
     * @see Field
     */
    @NonNull
    <T> Field<T> getField(final @NonNull String path,
                          final @NonNull Class<T> type);

    /**
     * Retrieves a list field at the specified path.
     * A list field provides operations for working with lists of configuration values.
     *
     * @param <T> the type of elements in the list
     * @param path the configuration path
     * @param type the class of the list element type
     * @return a {@link FieldList} instance for the specified path
     * @throws NullPointerException if path or type is null
     * @author LeyCM
     * @since 1.0.2
     * @see FieldList
     */
    @NonNull
    <T> FieldList<T> getFieldList(final @NonNull String path,
                                  final @NonNull Class<T> type);

    /**
     * Retrieves a section field at the specified path.
     * A section represents a nested configuration structure.
     *
     * @param path the configuration path pointing to a section
     * @return a {@link FieldSection} instance for the specified path
     * @throws NullPointerException if path is null
     * @author LeyCM
     * @since 1.0.2
     * @see FieldSection
     */
    @NonNull
    FieldSection getFieldSection(final @NonNull String path);

    /**
     * Removes the configuration value at the specified path.
     * This is equivalent to setting the value to null.
     *
     * @param path the configuration path to remove
     * @throws NullPointerException if path is null
     * @author LeyCM
     * @since 1.0.2
     * @see #set(String, Object)
     */
    default void remove(final @NonNull String path) {
        set(path, null);
    }

    /**
     * Sets a configuration value at the specified path.
     *
     * @param <T> the type of the value
     * @param path the configuration path
     * @param value the value to set, or null to remove the value
     * @throws NullPointerException if path is null
     * @author LeyCM
     * @since 1.0.2
     */
    <T> void set(final @NonNull String path,
                 final @Nullable T value);

    /**
     * Checks if the configuration contains a value at the specified path.
     *
     * @param path the configuration path to check
     * @return true if a value exists at the specified path, false otherwise
     * @throws NullPointerException if path is null
     * @author LeyCM
     * @since 1.0.2
     */
    boolean contains(final @NonNull String path);

    /**
     * Reloads the configuration from its source file.
     * Any unsaved changes will be lost.
     *
     * @author LeyCM
     * @since 1.0.2
     * @see ConfigFactory#reload(Config)
     */
    default void reload() {
        ConfigFactory.getInstance().reload(this);
    }

    /**
     * Saves the current configuration state to its source file.
     *
     * @author LeyCM
     * @since 1.0.2
     * @see ConfigFactory#save(Config)
     */
    default void save() {
        ConfigFactory.getInstance().save(this);
    }

    /**
     * Returns the file associated with this configuration.
     *
     * @return the configuration file
     * @author LeyCM
     * @since 1.0.2
     */
    @NonNull
    File file();

}