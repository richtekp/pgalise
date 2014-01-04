/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A subclass fo {@link javax.vecmath.Vector2d} which hides x and y properties
 * in order to avoid complaints by JAX-RS API implementors
 *
 * @author richter
 */
public class JaxbVector2d extends javax.vecmath.Vector2d {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	@XmlTransient //will be discovered via getter
	private double x;
	@XmlTransient //will be discovered via getter
	private double y;

	/**
	 * Constructs and initializes a Vector2d from the specified xy coordinates.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public JaxbVector2d(double x,
		double y) {
		super(x,
			y);
	}

	/**
	 * Constructs and initializes a Vector2d from the specified array.
	 *
	 * @param v the array of length 2 containing xy in order
	 */
	public JaxbVector2d(double[] v) {
		super(v);
	}

	/**
	 * Constructs and initializes a Vector2d from the specified Vector2d.
	 *
	 * @param v1 the Vector2d containing the initialization x y data
	 */
	public JaxbVector2d(javax.vecmath.Vector2d v1) {
		super(v1);
	}

	/**
	 * Constructs and initializes a Vector2d from the specified Vector2f.
	 *
	 * @param v1 the Vector2f containing the initialization x y data
	 */
	public JaxbVector2d(Vector2f v1) {
		super(v1);
	}

	/**
	 * Constructs and initializes a Vector2d from the specified Tuple2d.
	 *
	 * @param t1 the Tuple2d containing the initialization x y data
	 */
	public JaxbVector2d(Tuple2d t1) {
		super(t1);
	}

	/**
	 * Constructs and initializes a Vector2d from the specified Tuple2f.
	 *
	 * @param t1 the Tuple2f containing the initialization x y data
	 */
	public JaxbVector2d(Tuple2f t1) {
		super(t1);
	}

	/**
	 * Constructs and initializes a Vector2d to (0,0).
	 */
	public JaxbVector2d() {
		super();
	}
}
