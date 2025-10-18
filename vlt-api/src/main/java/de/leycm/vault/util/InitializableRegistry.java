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

public class InitializableRegistry {

    protected final static Map<Class<?>, Initializable> INIT = new HashMap<>();

    private InitializableRegistry() {
        throw new UnsupportedOperationException("Class can not be initialized");
    }


}
