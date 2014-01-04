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
 
package de.pgalise.simulation.shared.city;

import java.io.Serializable;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;

/**
 * A boundary is a rectangle with instances of {@link GeoLocation} for north-east (biggest lat/lng values) and south-west (smallest lat/lng values).
 * @author Timo
 */
public class Boundary implements Serializable {
	private static final long serialVersionUID = -1661074617259218811L;
	private JaxRSCoordinate northEast, southWest;

	/**
	 * Constructor
	 * @param northEast
	 * @param southWest
	 */
	public Boundary(JaxRSCoordinate northEast, JaxRSCoordinate southWest) {
		this.northEast = northEast;
		this.southWest = southWest;
	}

	public JaxRSCoordinate getNorthEast() {
		return northEast;
	}

	public void setNorthEast(JaxRSCoordinate northEast) {
		this.northEast = northEast;
	}

	public JaxRSCoordinate getSouthWest() {
		return southWest;
	}

	public void setSouthWest(JaxRSCoordinate southWest) {
		this.southWest = southWest;
	}
	
	/**
	 * Determines if the given location is inside or on the boundary.
	 * @param location
	 * @return	true, if it is inside this boundary.
	 */
	public boolean isLocationInBoundary(JaxRSCoordinate location) {
		if(location.getX() <= this.northEast.getX()
				&& location.getX() >= this.southWest.getX()
				&& location.getY() <= this.northEast.getY()
				&& location.getY() >= this.southWest.getY()) {
			return true;
		}
		
		return false;
	}
}
