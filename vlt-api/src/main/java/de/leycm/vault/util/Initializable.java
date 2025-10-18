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
package de.leycm.vault.util;

import lombok.NonNull;
import java.util.*;

public interface Initializable {

    @SuppressWarnings("unchecked")
    static <T extends Initializable> @NonNull T getInstance(Class<T> clazz) {
        Initializable instance = InitializableRegistry.INIT.get(clazz);

        if (instance == null)
            throw new NullPointerException("No instance registered for " + clazz.getSimpleName());

        if (!clazz.isInstance(instance))
            throw new ClassCastException("Registered instance is not of type " + clazz.getSimpleName());

        return (T) instance;
    }

    static <T extends Initializable> void register(@NonNull T instance, Class<T> clazz) {
        if (InitializableRegistry.INIT.containsKey(clazz))
            throw new RuntimeException("An Instance of " + clazz.getSimpleName() + " is Already registered");


        instance.onInstall();
        InitializableRegistry.INIT.put(clazz, instance);
    }

    static <T extends Initializable> void unregister(@NonNull Class<T> clazz) {
        if (!InitializableRegistry.INIT.containsKey(clazz))
            throw new RuntimeException("There is no Instance of " + clazz.getSimpleName());

        InitializableRegistry.INIT.get(clazz).onUninstall();
        InitializableRegistry.INIT.remove(clazz);
    }

    default void onInstall() {}
    default void onUninstall() {}
}
