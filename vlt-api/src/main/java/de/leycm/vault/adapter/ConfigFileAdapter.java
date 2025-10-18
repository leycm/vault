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

import java.io.IOException;
import java.util.Map;

public interface ConfigFileAdapter {

    Map<String, Object> read(String content) throws IOException;

    String write(String current, Map<String, Object> data) throws IOException;

    String updateValue(String current, String key, Object value) throws IOException;

}
