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

import java.util.HashMap;
import java.util.Map;

/**
 * Registry storage for initializable services.
 * This class maintains a static map of registered service instances.
 *
 * @author LeyCM
 * @since 1.0.2
 * @see Initializable
 */
public class InitializableRegistry {

    /**
     * Static map storing registered initializable instances.
     * Key: Service class, Value: Service instance
     */
    protected final static Map<Class<?>, Initializable> INIT = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static members.
     *
     * @throws UnsupportedOperationException if constructor is called
     * @author LeyCM
     * @since 1.0.2
     */
    private InitializableRegistry() {
        throw new UnsupportedOperationException("Class can not be initialized");
    }


}