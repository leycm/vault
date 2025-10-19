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
package de.leycm.vault.field;

import de.leycm.vault.Config;
import java.util.*;

/**
 * Represents a configuration section that can contain nested configuration values.
 * Combines functionality of both {@link Config} and {@link Field} for map-based sections.
 *
 * @author LeyCM
 * @since 1.0.2
 * @see Config
 * @see Field
 */
public interface FieldSection extends
        Config, Field<Map<String, Object>> {

}