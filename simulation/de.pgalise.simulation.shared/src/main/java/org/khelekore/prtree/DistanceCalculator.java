package org.khelekore.prtree;

import java.io.Serializable;

/** A class that can calculate the distance to a given object
 *  stored in the PRTree
 * @param <T> the data type to calculate distances to
 */
public interface DistanceCalculator<T> extends Serializable {
    /** Calculate the distance between the given object and the point
     * @param t the object to calculate the distance to
     * @param p the point
     * @return The calculated distance
     */
    double distanceTo (T t, PointND p);
}
