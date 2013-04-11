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

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Representation of a 3d vector.
 * 
 * @author mustafa
 *
 */
public class Vector3d {
	private final double x;
	private final double y;
	private final double z;

	private static final Vector3d ZERO_VECTOR = new Vector3d(0, 0, 0);
	private static final Vector3d BASE_VECTOR_100 = new Vector3d(1, 0, 0);
	private static final Vector3d BASE_VECTOR_010 = new Vector3d(0, 1, 0);
	private static final Vector3d BASE_VECTOR_001 = new Vector3d(0, 0, 1);

	private Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Returns first coordinate of this vector
	 * @return first coordinate of this vector
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Returns second coordinate of this vector
	 * @return second coordinate of this vector
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns third coordinate of this vector
	 * @return third coordinate of this vector
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Factory method to instantiate an object of this class;
	 * the returned vector points to (x, y, z).
	 * 
	 * @param x value of the first coordinate
	 * @param y value of the second coordinate
	 * @param z value of the third coordinate
	 * @return vector pointing to (x, y, z)
	 */
	public static Vector3d valueOf(double x, double y, double z) {
		if(x == 0 && y ==0 && z == 0) {
			return ZERO_VECTOR;
		}
		else if(x == 1 && y == 0 && z == 0) {
			return BASE_VECTOR_100;
		}
		else if(x == 0 && y == 1 && z == 0) {
			return BASE_VECTOR_010;
		}
		else if(x == 0 && y == 0 && z == 1) {
			return BASE_VECTOR_001;
		}
		
		return new Vector3d(x, y, z);
	}

	/**
	 * Adds this vector to the parameter v and returns the result.
	 * @param v vector to add
	 * @return sum of this vector and v
	 */
	public Vector3d add(Vector3d v) {
		return valueOf(this.getX() + v.getX(), this.getY() + v.getY(), this.getZ() + v.getZ());
	}

	/**
	 * Subtracts parameter v from this vector and returns the result.
	 * @param v vector to subtract
	 * @return difference of this vector and v
	 */
	public Vector3d sub(Vector3d v) {
		return valueOf(this.getX() - v.getX(), this.getY() - v.getY(), this.getZ() - v.getZ());
	}	
	
	/**
	 * Returns the scalar product of this vector and parameter s.
	 * 
	 * @param s the scalar
	 * @return scalar product of this vector and s
	 */
	public Vector3d scale(double s) {
		return valueOf(this.getX() * s, this.getY() * s, this.getZ() * s);
	}
	
	/**
	 * Returns the dot product of this vector and parameter v.
	 * @param v other vector
	 * @return dot product of this vector and v
	 */
	public double dot(Vector3d v) {
		return (this.getX() * v.getX() + this.getY() * v.getY() + this.getZ() * v.getZ());
	}
	
	/**
	 * Returns the length of this vector.
	 * @return length of this vector
	 */
	public double length() {
		return Math.sqrt(dot(this));
	}

	/**
	 * Returns this vector normalized.
	 * @return this vector normalized
	 */
	public Vector3d normalize() {
		return scale(1/length());
	}
	
	/**
    *   Returns the angle in radians between this vector and the vector
    *   parameter; the return value is constrained to the range [0,PI].
    *   @param v    the other vector
    *   @return   the angle in radians in the range [0,PI]
	 */
	// implementation based on javax.vecmath.Vector2d#angle
	public double angle(Vector3d v) {
		double vDot = this.dot(v) / (this.length() * v.length());
		if( vDot < -1.0) vDot = -1.0;
		if( vDot >  1.0) vDot =  1.0;
		return((double) (Math.acos( vDot ))); 
	}
	
	/**
     * Sets this vector to the vector cross product of this vector and parameter v.
     * 
     * @param v
     *          the second vector
     */
	// implementation based on javax.vecmath.Vector3d#cross
    public Vector3d cross(Vector3d v) {
        double x, y;

        x = this.getY() * v.getZ() - this.getZ() * v.getY();
        y = v.getX() * this.getZ() - v.getZ() * this.getX();
        return valueOf(x, y, this.getX() * v.getY() - this.getY() * v.getX());
    }

	
	@Override
	public boolean equals(Object obj) {
		if( this == obj ) return true;	// reflexiv
		if ( !(obj instanceof Vector3d) ) return false; // returns false when o = null, instanceof is ok because this class is immutable
		
		Vector3d v = (Vector3d) obj;
		return this.getX() == v.getX() && this.getY() == v.getY() && this.getZ() == v.getZ(); // symetrisch, transitiv
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
			       append(x).
			       append(y).
			       append(z).
			       toHashCode();
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", getX(), getY(), getZ());
	}
}
