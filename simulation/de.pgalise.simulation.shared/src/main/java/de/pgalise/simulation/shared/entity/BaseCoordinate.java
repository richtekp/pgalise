/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.entity;

import com.vividsolutions.jts.geom.Coordinate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An extension of {@link Coordinate} which is necessary to reach JaxRS 
 * compliance (e.g. getter and setter for properties x and y). The values 
 * of properties x and y are forming the {@link Id} of the entity and are 
 * therefore immutable.
 * @author richter
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@IdClass(BaseCoordinatePK.class)
/*
BaseCoordinate doesn't have a business id because it is not allowed that two 
Coordinates with the same x and y property exist (therefore defining a 
composite key over x and y makes sense) AND changing the values is not 
necessary
*/
public class BaseCoordinate extends Coordinate {
  private static final long serialVersionUID = 1L;

  /*
  internal implementation notes:
  - only serves to hide the public field of the super class, operations are
  performed on it though (passed in constructor)
  */
  @SuppressWarnings("FieldNameHidesFieldInSuperclass")
  private double x;
  /*
  internal implementation notes:
  - only serves to hide the public field of the super class, operations are
  performed on it though (passed in constructor)
  */
  @SuppressWarnings("FieldNameHidesFieldInSuperclass")
  private double y;
  /*
  internal implementation notes:
  - only serves to hide the public field of the super class, operations are
  performed on it though (passed in constructor)
  */
  @SuppressWarnings("FieldNameHidesFieldInSuperclass")
  private double z;
  
	/**
	 * Constructs a <code>Coordinate</code> at (x,y,z).
	 *
	 * @param x the x-value
	 * @param y the y-value
	 */
	public BaseCoordinate(double x,
		double y) {
		super(x,
			y);
	}

	/**
	 * Constructs a <code>Coordinate</code> at (0,0,NaN).
	 */
	protected BaseCoordinate() {
		super();
	}

	/**
	 * Constructs a <code>Coordinate</code> having the same (x,y,z) values as
	 * <code>other</code>.
	 *
	 * @param c the <code>Coordinate</code> to copy.
	 */
	public BaseCoordinate(Coordinate c) {
		super(c);
	}

  @Id
	public double getX() {
		return super.getOrdinate(X);
	}

	protected void setX(double x) {
		super.setOrdinate(X,
			x);
	}

  @Id
	public double getY() {
		return super.getOrdinate(Y);
	}

	protected void setY(double y) {
		super.setOrdinate(Y,
			y);
	}
}
