/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.city;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author richter
 */
@XmlRootElement
public class JaxRSCoordinate extends com.vividsolutions.jts.geom.Coordinate {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	@XmlTransient //will be discovered via getter
	private double x,y,z;

  /**
   *  Constructs a <code>Coordinate</code> at (x,y,z).
   *
   *@param  x  the x-value
   *@param  y  the y-value
   *@param  z  the z-value
   */
  public JaxRSCoordinate(double x, double y, double z) {
		super(x,
			y,
			z);
  }

  /**
   *  Constructs a <code>Coordinate</code> at (0,0,NaN).
   */
  public JaxRSCoordinate() {
		super();
  }

  /**
   *  Constructs a <code>Coordinate</code> having the same (x,y,z) values as
   *  <code>other</code>.
   *
   *@param  c  the <code>Coordinate</code> to copy.
   */
  public JaxRSCoordinate(com.vividsolutions.jts.geom.Coordinate c) {
		super(c);
  }

  /**
   *  Constructs a <code>Coordinate</code> at (x,y,NaN).
   *
   *@param  x  the x-value
   *@param  y  the y-value
   */
  public JaxRSCoordinate(double x, double y) {
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
