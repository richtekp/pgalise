package org.khelekore.prtree;

import java.io.Serializable;

/** A factory that creates the nodes (either leaf or internal).
 * @param <T> the data stored in the node
 * @param <N> the type of the node
 */
interface NodeFactory<N> extends Serializable {
    /** Create a new node 
     * @param data the data entries for the node, fully filled.
     * @return the new node
     */
    N create (Object[] data);
}
