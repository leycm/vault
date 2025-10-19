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

import de.leycm.vault.ConfigFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Adapter interface for reading and writing configuration files in specific formats.
 * Implementations handle format-specific parsing and serialization.
 *
 * @author LeyCM
 * @since 1.0.2
 * @see ConfigFactory#registerFileAdapter(ConfigFileAdapter, String...)
 */
public interface ConfigFileAdapter {

    /**
     * Reads configuration data from a string content.
     *
     * @param content the configuration file content as string
     * @return a map representing the configuration data
     * @throws IOException if reading or parsing fails
     * @author LeyCM
     * @since 1.0.2
     */
    Map<String, Object> read(String content) throws IOException;

    /**
     * Writes configuration data to string format.
     *
     * @param current the current file content (for preserving format/comments)
     * @param data the configuration data to write
     * @return the serialized configuration as string
     * @throws IOException if writing or serialization fails
     * @author LeyCM
     * @since 1.0.2
     */
    String write(String current, Map<String, Object> data) throws IOException;

    /**
     * Updates a specific value in the configuration content.
     *
     * @param current the current file content
     * @param key the configuration key to update
     * @param value the new value to set
     * @return the updated configuration content
     * @throws IOException if updating fails
     * @author LeyCM
     * @since 1.0.2
     */
    String updateValue(String current, String key, Object value) throws IOException;

}