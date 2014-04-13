/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared;

import com.vividsolutions.jts.geom.Coordinate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An extension of {@link Coordinate} which is necessary to reach JaxRS 
 * compliance (e.g. getter and setter for properties x and y).
 * @author richter
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxRSCoordinate extends Coordinate {
  

	/**
	 * Constructs a <code>Coordinate</code> at (x,y,z).
	 *
   * @param id
	 * @param x the x-value
	 * @param y the y-value
	 * @param z the z-value
	 */
	public JaxRSCoordinate(double x,
		double y,
		double z) {
		super(x,
			y,
			z);
	}

	/**
	 * Constructs a <code>Coordinate</code> at (0,0,NaN).
	 */
	protected JaxRSCoordinate() {
		super();
	}

	/**
	 * Constructs a <code>Coordinate</code> having the same (x,y,z) values as
	 * <code>other</code>.
	 *
   * @param id
	 * @param c the <code>Coordinate</code> to copy.
	 */
	public JaxRSCoordinate(Coordinate c) {
		super(c);
	}

	/**
	 * Constructs a <code>Coordinate</code> at (x,y,NaN).
	 *
   * @param id
	 * @param x the x-value
	 * @param y the y-value
	 */
	public JaxRSCoordinate(double x,
		double y) {
		super(x,
			y);
	}

	public double getX() {
		return super.getOrdinate(X);
	}

	public void setX(double x) {
		super.setOrdinate(X,
			x);
	}

	public double getY() {
		return super.getOrdinate(Y);
	}

	public void setY(double y) {
		super.setOrdinate(Y,
			y);
	}
}
