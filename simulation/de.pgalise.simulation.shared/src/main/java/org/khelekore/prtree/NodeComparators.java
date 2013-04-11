package org.khelekore.prtree;

import java.io.Serializable;
import java.util.Comparator;

interface NodeComparators<T> extends Serializable {
    /** Get a comparator for the given axis
     * @param axis the axis that the comparator will use
     * @return the comparator
     */
    Comparator<T> getMinComparator (int axis);

    /** Get a comparator for the given axis
     * @param axis the axis that the comparator will use
     * @return the comparator
     */
    Comparator<T> getMaxComparator (int axis);
}
