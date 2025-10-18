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

public interface ConfigFactory extends Initializable {

    @NonNull
    @Contract(pure = true)
    static ConfigFactory getInstance() {
        return Initializable.getInstance(ConfigFactory.class);
    }

    default Config create(final @NonNull String filename) {
        return create(new File(defDir(), filename));
    }

    Config create(final @NonNull File file);

    default void reload(final @NonNull Config config) {
        reload(config.file());
    }

    void reload(final @NonNull File file);


    default void save(final @NonNull Config config) {
        save(config.file());
    }

    void save(final @NonNull File file);

    File defDir();

    void registerFileAdapter(ConfigFileAdapter adapter, String... endings);

    <T> void registerTypeAdapter(TypeAdapter<T> adapter, Class<T> clazz);

}
