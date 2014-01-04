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
 
package de.pgalise.simulation.service;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.shared.city.JaxbVector2d;

/**
 * Enum of directions
 * 
 * @author Mustafa
 * @version 1.0 (Oct 1, 2012)
 */
public enum Orientation {
	EAST, NORTH, NORTH_EAST, NORTH_WEST, ORIGIN, SOUTH, SOUTH_EAST, SOUTH_WEST, WEST;

	/**
	 * Returns a direction enum for the coordinates of the private transport
	 * vehicle.
	 * 
	 * @param direction 
	 * @return Direction enum
	 */
	public static Orientation getOrientation(JaxbVector2d direction) {
		Orientation orientation;
		if ((direction.getX() == 0) && (direction.getY() < 0)) {
			orientation = Orientation.NORTH;
		} else if ((direction.getX() > 0) && (direction.getY() < 0)) {
			orientation = Orientation.NORTH_EAST;
		} else if ((direction.getX() > 0) && (direction.getY() == 0)) {
			orientation = Orientation.EAST;
		} else if ((direction.getX() > 0) && (direction.getY() > 0)) {
			orientation = Orientation.SOUTH_EAST;
		} else if ((direction.getX() == 0) && (direction.getY() > 0)) {
			orientation = Orientation.SOUTH;
		} else if ((direction.getX() < 0) && (direction.getY() > 0)) {
			orientation = Orientation.SOUTH_WEST;
		} else if ((direction.getX() < 0) && (direction.getY() == 0)) {
			orientation = Orientation.WEST;
		} else if ((direction.getX() < 0) && (direction.getY() < 0)) {
			orientation = Orientation.NORTH_WEST;
		} else {
			orientation = Orientation.ORIGIN;
		}
		return orientation;
	}

	/**
	 * Checks whether the given position is beyond the border position.
	 * 
	 * @param orientation
	 *            Orientation of the vehicle
	 * @param position
	 *            Position of the vehicle to check
	 * @param borderPosition
	 *            Border position
	 * @return True, if the position is beyond the border position
	 */
	public static boolean isBeyond(Orientation orientation,
			JaxRSCoordinate position, JaxRSCoordinate borderPosition) {
		return (((orientation == Orientation.NORTH) && (position.getY() <= borderPosition
				.getY()))
				|| ((orientation == Orientation.NORTH_EAST)
						&& (position.getX() >= borderPosition.getX()) && (position
						.getY() <= borderPosition.getY()))
				|| ((orientation == Orientation.EAST) && (position.getX() >= borderPosition
						.getX()))
				|| ((orientation == Orientation.SOUTH_EAST)
						&& (position.getX() >= borderPosition.getX()) && (position
						.getY() >= borderPosition.getY()))
				|| ((orientation == Orientation.SOUTH) && (position.getY() >= borderPosition
						.getY()))
				|| ((orientation == Orientation.SOUTH_WEST)
						&& (position.getX() <= borderPosition.getX()) && (position
						.getY() >= borderPosition.getY()))
				|| ((orientation == Orientation.WEST) && (position.getX() <= borderPosition
						.getX())) || ((orientation == Orientation.NORTH_WEST)
				&& (position.getX() <= borderPosition.getX()) && (position
				.getY() <= borderPosition.getY())));
	}
}
