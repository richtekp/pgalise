package org.khelekore.prtree;

import java.io.Serializable;

/** A node object filterer.
 * @param <T> the node type
 */
public interface NodeFilter<T> extends Serializable {
    /** Check if the given node object is accepted
     * @param t the node user data
     * @return True if the node is accepted, false otherwise
     */
    boolean accept (T t);
}
