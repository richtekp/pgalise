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

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;

/**
 * Abstract super class for a {@link WeatherPositionConverter}. This class
 * provides some methods for calculations.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 12, 2012)
 */
public abstract class WeatherPositionConverterBase implements
	WeatherPositionConverter {

	/**
	 * Round the value
	 *
	 * @param value Value
	 * @param digits Number of decimal places
	 * @return rounded value
	 */
	public static double round(double value,
		int digits) {
		double rValue = Math.round(value * Math.pow(10d,
			digits));
		return rValue / Math.pow(10d,
			digits);
	}

	/**
	 * Distance from the reference point to the grid
	 */
	private double gridDistance;

	/**
	 * Position of the reference values
	 */
	private Polygon grid;

	public WeatherPositionConverterBase() {
	}

	/**
	 * Constructor
	 *
	 *
	 * @param grid
	 */
	public WeatherPositionConverterBase(Polygon grid) {
		this.grid = grid;
	}

	public double getGridDistance() {
		double retValue = Double.MIN_VALUE;
		JaxRSCoordinate referncePoint = getReferencePosition();
		for (com.vividsolutions.jts.geom.Coordinate coordinate : grid.
			getCoordinates()) {
			double distance = referncePoint.distance(coordinate);
			if (distance > retValue) {
				retValue = distance;
			}
		}
		return retValue;
	}

	public JaxRSCoordinate getReferencePosition() {
		Point centroid = this.grid.getCentroid();
		return new JaxRSCoordinate(centroid.getX(),
			centroid.getY());
	}

	@Override
	public Polygon getGrid() {
		return grid;
	}

	@Override
	public void setGrid(Polygon grid) {
		this.grid = grid;
	}
}
