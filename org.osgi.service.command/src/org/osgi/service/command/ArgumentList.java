package org.osgi.service.command;

import java.util.*;

/**
 * A Parameter List must be expanded to its constituents when used in a command
 * line. It allows the following pattern:
 * 
 * <pre>
 *   $ g = { grep -i (args $*) }
 *   $ g pattern file -> grep -i pattern file
 * </pre>
 * 
 * A ParameterList extends List<Object>, meaning that it can be manipulated as a
 * list as well.
 */
public interface ArgumentList extends List<Object> {
}