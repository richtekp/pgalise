package org.khelekore.prtree;

import java.io.Serializable;

/** A description of an N-dimensional point
 */
public interface PointND extends Serializable {
    /**
     * @return the number of dimensions this point has
     */
    int getDimensions ();
    
    /**
     * @param axis the axis to get the value for
     * @return the ordinate value for the given axis
     */
    double getOrd (int axis);
}
