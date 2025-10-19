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

import de.leycm.vault.adapter.ConfigFileAdapter;
import de.leycm.vault.adapter.TypeAdapter;
import de.leycm.vault.util.Initializable;
import org.jetbrains.annotations.*;

import lombok.NonNull;
import java.io.File;

/**
 * Factory interface for creating and managing configuration instances.
 * Provides methods for configuration creation, persistence, and adapter registration.
 *
 * @author LeyCM
 * @since 1.0.2
 * @see Config
 * @see Initializable
 * @see ConfigFileAdapter
 * @see TypeAdapter
 */
public interface ConfigFactory extends Initializable {

    /**
     * Returns the singleton instance of the ConfigFactory.
     *
     * @return the ConfigFactory instance
     * @throws NullPointerException if no instance is registered
     * @author LeyCM
     * @since 1.0.2
     * @see Initializable#getInstance(Class)
     */
    @NonNull
    @Contract(pure = true)
    static ConfigFactory getInstance() {
        return Initializable.getInstance(ConfigFactory.class);
    }

    /**
     * Creates a new configuration instance for the specified filename.
     * The file will be created in the default configuration directory.
     *
     * @param filename the name of the configuration file
     * @return a new {@link Config} instance
     * @throws NullPointerException if filename is null
     * @author LeyCM
     * @since 1.0.2
     * @see #create(File)
     */
    default Config create(final @NonNull String filename) {
        return create(new File(defDir(), filename));
    }

    /**
     * Creates a new configuration instance for the specified file.
     *
     * @param file the configuration file
     * @return a new {@link Config} instance
     * @throws NullPointerException if file is null
     * @author LeyCM
     * @since 1.0.2
     */
    Config create(final @NonNull File file);

    /**
     * Reloads the configuration from its source file.
     *
     * @param config the configuration to reload
     * @throws NullPointerException if config is null
     * @author LeyCM
     * @since 1.0.2
     * @see #reload(File)
     */
    default void reload(final @NonNull Config config) {
        reload(config.file());
    }

    /**
     * Reloads the configuration from the specified file.
     *
     * @param file the configuration file to reload
     * @throws NullPointerException if file is null
     * @author LeyCM
     * @since 1.0.2
     */
    void reload(final @NonNull File file);

    /**
     * Saves the configuration to its source file.
     *
     * @param config the configuration to save
     * @throws NullPointerException if config is null
     * @author LeyCM
     * @since 1.0.2
     * @see #save(File)
     */
    default void save(final @NonNull Config config) {
        save(config.file());
    }

    /**
     * Saves the configuration to the specified file.
     *
     * @param file the configuration file to save to
     * @throws NullPointerException if file is null
     * @author LeyCM
     * @since 1.0.2
     */
    void save(final @NonNull File file);

    /**
     * Returns the default configuration directory.
     *
     * @return the default directory for configuration files
     * @author LeyCM
     * @since 1.0.2
     */
    File defDir();

    /**
     * Registers a file adapter for specific file extensions.
     *
     * @param adapter the file adapter to register
     * @param endings the file extensions this adapter supports (e.g., "yml", "json")
     * @throws NullPointerException if adapter or endings is null
     * @author LeyCM
     * @since 1.0.2
     * @see ConfigFileAdapter
     */
    void registerFileAdapter(ConfigFileAdapter adapter, String... endings);

    /**
     * Registers a type adapter for a specific class.
     *
     * @param <T> the type the adapter handles
     * @param adapter the type adapter to register
     * @param clazz the class this adapter supports
     * @throws NullPointerException if adapter or clazz is null
     * @author LeyCM
     * @since 1.0.2
     * @see TypeAdapter
     */
    <T> void registerTypeAdapter(TypeAdapter<T> adapter, Class<T> clazz);

}