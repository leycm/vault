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

/**
 * Registry for managing initializable service instances.
 * Provides static methods for service registration, retrieval, and lifecycle management.
 *
 * @author LeyCM
 * @since 1.0.2
 * @see Initializable
 */
public interface Initializable {

    /**
     * Retrieves the registered instance for the specified class.
     *
     * @param <T> the type of the initializable service
     * @param clazz the class of the service to retrieve
     * @return the registered service instance
     * @throws NullPointerException if no instance is registered for the class
     * @throws ClassCastException if the registered instance is not of the expected type
     * @author LeyCM
     * @since 1.0.2
     */
    @SuppressWarnings("unchecked")
    static <T extends Initializable> @NonNull T getInstance(Class<T> clazz) {
        Initializable instance = InitializableRegistry.INIT.get(clazz);

        if (instance == null)
            throw new NullPointerException("No instance registered for " + clazz.getSimpleName());

        if (!clazz.isInstance(instance))
            throw new ClassCastException("Registered instance is not of type " + clazz.getSimpleName());

        return (T) instance;
    }

    /**
     * Registers a new service instance.
     *
     * @param <T> the type of the initializable service
     * @param instance the service instance to register
     * @param clazz the class under which to register the instance
     * @throws RuntimeException if an instance is already registered for the class
     * @throws NullPointerException if instance or clazz is null
     * @author LeyCM
     * @since 1.0.2
     */
    static <T extends Initializable> void register(@NonNull T instance, Class<T> clazz) {
        if (InitializableRegistry.INIT.containsKey(clazz))
            throw new RuntimeException("An Instance of " + clazz.getSimpleName() + " is Already registered");


        instance.onInstall();
        InitializableRegistry.INIT.put(clazz, instance);
    }

    /**
     * Unregisters a service instance.
     *
     * @param <T> the type of the initializable service
     * @param clazz the class to unregister
     * @throws RuntimeException if no instance is registered for the class
     * @throws NullPointerException if clazz is null
     * @author LeyCM
     * @since 1.0.2
     */
    static <T extends Initializable> void unregister(@NonNull Class<T> clazz) {
        if (!InitializableRegistry.INIT.containsKey(clazz))
            throw new RuntimeException("There is no Instance of " + clazz.getSimpleName());

        InitializableRegistry.INIT.get(clazz).onUninstall();
        InitializableRegistry.INIT.remove(clazz);
    }

    /**
     * Called when the service is installed/registered.
     * Default implementation does nothing.
     *
     * @author LeyCM
     * @since 1.0.2
     */
    default void onInstall() {}

    /**
     * Called when the service is uninstalled/unregistered.
     * Default implementation does nothing.
     *
     * @author LeyCM
     * @since 1.0.2
     */
    default void onUninstall() {}
}