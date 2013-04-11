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
 
package de.pgalise.simulation.shared.geometry;

import de.pgalise.util.vector.Vector2d;

/**
 * Represents a rectangle
 * 
 * @author Mustafa
 * @version 1.0 (Sep 26, 2012)
 */
public class Rectangle implements Geometry {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1663471437429814169L;

	/**
	 * End x coordinate
	 */
	private double endX;

	/**
	 * End y coordinate
	 */
	private double endY;

	/**
	 * Start x coordinate
	 */
	private double startX;

	/**
	 * Start y coordinate
	 */
	private double startY;

	/**
	 * Default contructor
	 */
	public Rectangle() {
	}

	/**
	 * Constructor
	 * 
	 * @param startX
	 *            Start x coordinate
	 * @param startY
	 *            Start y coordinate
	 * @param endX
	 *            End x coordinate
	 * @param endY
	 *            End y coordinate
	 */
	public Rectangle(double startX, double startY, double endX, double endY) {
		this.setStartX(startX);
		this.setStartY(startY);
		this.setEndX(endX);
		this.setEndY(endY);
	}

	@Override
	public boolean covers(Vector2d point) {
		return (point.getX() >= this.getStartX()) && (point.getX() < this.getEndX())
				&& (point.getY() >= this.getStartY()) && (point.getY() < this.getEndY());
	}

	@Override
	public double getCenterX() {
		return Math.abs((this.getStartX() + this.getEndX()) / 2);
	}

	@Override
	public double getCenterY() {
		return Math.abs((this.getStartY() + this.getEndY()) / 2);
	}

	@Override
	public double getEndX() {
		return this.endX;
	}

	@Override
	public double getEndY() {
		return this.endY;
	}

	@Override
	public double getHeight() {
		return Math.abs(this.endY - this.startY);
	}

	@Override
	public double getStartX() {
		return this.startX;
	}

	@Override
	public double getStartY() {
		return this.startY;
	}

	@Override
	public double getWidth() {
		return Math.abs(this.endX - this.startX);
	}

	@Override
	public void setEndX(double endX) {
		this.endX = endX;
	}

	@Override
	public void setEndY(double endY) {
		this.endY = endY;
	}

	@Override
	public void setStartX(double startX) {
		this.startX = startX;
	}

	@Override
	public void setStartY(double startY) {
		this.startY = startY;
	}

	@Override
	public String toString() {
		return String.format("x=%s, y=%s, width=%s, height=%s", this.startX, this.startY, this.getWidth(),
				this.getHeight());
	}
}
