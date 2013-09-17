package org.khelekore.prtree;

/** One implementatoin of a point
 */
public class SimplePointND implements PointND {
	private static final long serialVersionUID = 164119919737436081L;
	private final double[] ords;

    /** Create a new SimplePointND using the given ordinates.
     * @param ords the ordinates
     */
    public SimplePointND (double... ords) {
	this.ords = ords;
    }

	@Override
    public int getDimensions () {
	return ords.length;
    }

	@Override
    public double getOrd (int axis) {
	return ords[axis];
    }
}
