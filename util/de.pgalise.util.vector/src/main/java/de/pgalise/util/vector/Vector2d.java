/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.util.vector;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Representation of a 2d vector.
 * 
 * @author mustafa
 *
 */
public final class Vector2d implements Serializable {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -597446140870293429L;
	private final double x;
	private final double y;

	private static final Vector2d ZERO_VECTOR = new Vector2d(0, 0);
	private static final Vector2d TOP_VECTOR = new Vector2d(0, -1);
	private static final Vector2d RIGHT_VECTOR = new Vector2d(1, 0);
	private static final Vector2d BOTTOM_VECTOR = new Vector2d(0, 1);
	private static final Vector2d LEFT_VECTOR = new Vector2d(-1, 0);

	private Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns first coordinate of this vector
	 * 
	 * @return first coordinate of this vector
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * Returns second coordinate of this vector
	 * 
	 * @return second coordinate of this vector
	 */
	public double getY() {
		return y;
	}

	/**
	 * Factory method to instantiate an object of this class; the returned vector points to (x, y).
	 * 
	 * @param x
	 *            value of the first coordinate
	 * @param y
	 *            value of the second coordinate
	 * @return vector pointing to (x, y)
	 */
	public static Vector2d valueOf(double x, double y) {
		if (x == 0 && y == 0) {
			return ZERO_VECTOR;
		} else if (x == 0 && y == -1) {
			return TOP_VECTOR;
		} else if (x == 1 && y == 0) {
			return RIGHT_VECTOR;
		} else if (x == 0 && y == 1) {
			return BOTTOM_VECTOR;
		} else if (x == -1 && y == 0) {
			return LEFT_VECTOR;
		}
		return new Vector2d(x, y);
	}

	/**
	 * Adds this vector to the parameter v and returns the result.
	 * 
	 * @param v
	 *            vector to add
	 * @return sum of this vector and v
	 */
	public Vector2d add(Vector2d v) {
		return valueOf(this.getX() + v.x, this.getY() + v.y);
	}

	/**
	 * Subtracts parameter v from this vector and returns the result.
	 * 
	 * @param v
	 *            vector to subtract
	 * @return difference of this vector and v
	 */
	public Vector2d sub(Vector2d v) {
		return valueOf(this.getX() - v.x, this.getY() - v.y);
	}

	/**
	 * Returns the scalar product of this vector and parameter s.
	 * 
	 * @param s
	 *            the scalar
	 * @return scalar product of this vector and s
	 */
	public Vector2d scale(double s) {
		return valueOf(this.getX() * s, this.getY() * s);
	}

	/**
	 * Returns the dot product of this vector and parameter v.
	 * 
	 * @param v
	 *            other vector
	 * @return dot product of this vector and v
	 */
	public double dot(Vector2d v) {
		return (this.getX() * v.x + this.getY() * v.y);
	}

	/**
	 * Returns the length of this vector.
	 * 
	 * @return length of this vector
	 */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * Returns this vector normalized.
	 * 
	 * @return this vector normalized
	 */
	public Vector2d normalize() {
		return scale(1 / length());
	}

	/**
	 * Returns the angle in radians between this vector and the vector parameter; the return value is constrained to the
	 * range [0,PI].
	 * 
	 * @param v
	 *            the other vector
	 * @return the angle in radians in the range [0,PI]
	 */
	// implementation based on javax.vecmath.Vector2d#angle
	public double angle(Vector2d v) {
		double vDot = this.dot(v) / (this.length() * v.length());
		if (vDot < -1.0)
			vDot = -1.0;
		if (vDot > 1.0)
			vDot = 1.0;
		return ((double) (Math.acos(vDot)));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true; // reflexiv
		if (!(obj instanceof Vector2d))
			return false; // returns false when o = null, instanceof is ok because this class is immutable

		Vector2d v = (Vector2d) obj;
		return this.getX() == v.x && this.getY() == v.y; // symetrisch, transitiv
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(x).append(y).toHashCode();
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", getX(), getY());
	}
}
