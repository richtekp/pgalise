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
 
package de.pgalise.simulation.weather.positionconverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * Abstract super class for a {@link WeatherPositionConverter}. This class provides some methods for calculations.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 12, 2012)
 */
public abstract class WeatherPositionConverterBase implements WeatherPositionConverter {

	/**
	 * Round the value
	 * 
	 * @param value
	 *            Value
	 * @param digits
	 *            Number of decimal places
	 * @return rounded value
	 */
	public static double round(double value, int digits) {
		double rValue = Math.round(value * Math.pow(10d, digits));
		return rValue / Math.pow(10d, digits);
	}

	/**
	 * Distance from the reference point to the grid
	 */
	private double gridDistance;

	/**
	 * Position of the reference values
	 */
	private Geometry grid;

	/**
	 * Constructor
	 * 
	 * 
	 * @param grid 
	 */
	public WeatherPositionConverterBase(Geometry grid) {
		this.grid = grid;
	}

	public double getGridDistance() {
		double retValue = Double.MIN_VALUE;
		Coordinate referncePoint = getReferencePosition();
		for(Coordinate coordinate : grid.getCoordinates()) {
			double distance = referncePoint.distance(coordinate);
			if(distance > retValue) {
				retValue = distance;
	}
	}
		return retValue;
	}

	public Coordinate getReferencePosition() {
		Point centroid = this.grid.getCentroid();
		return new Coordinate(centroid.getX(), centroid.getY());
	}
}
