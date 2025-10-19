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
package de.leycm.vault.adapter;

import de.leycm.vault.Config;
import de.leycm.vault.ConfigFactory;

/**
 * Adapter interface for custom type conversion in configuration.
 * Allows custom serialization and deserialization of complex types.
 *
 * @param <T> the type this adapter handles
 * @author LeyCM
 * @since 1.0.2
 * @see ConfigFactory#registerTypeAdapter(TypeAdapter, Class)
 */
public interface TypeAdapter<T> {

    /**
     * Converts a raw object to the target type.
     *
     * @param root the root configuration instance
     * @param path the configuration path where the value is located
     * @param raw the raw object from the configuration
     * @return the converted value of type T
     * @author LeyCM
     * @since 1.0.2
     */
    T fromObject(Config root, String path, Object raw);

    /**
     * Converts a typed value back to a serializable object.
     *
     * @param root the root configuration instance
     * @param path the configuration path where the value is located
     * @param value the typed value to convert
     * @return a serializable object representation
     * @author LeyCM
     * @since 1.0.2
     */
    Object toObject(Config root, String path, T value);

}