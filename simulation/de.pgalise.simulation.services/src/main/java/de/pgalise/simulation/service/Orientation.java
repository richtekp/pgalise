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

import javax.vecmath.Vector2d;

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
	 * @return Direction enum
	 */
	public final static Orientation getOrientation(Vector2d direction) {
		Orientation orientation;
		if ((direction.x == 0) && (direction.y < 0)) {
			orientation = Orientation.NORTH;
		} else if ((direction.x > 0) && (direction.y < 0)) {
			orientation = Orientation.NORTH_EAST;
		} else if ((direction.x > 0) && (direction.y == 0)) {
			orientation = Orientation.EAST;
		} else if ((direction.x > 0) && (direction.y > 0)) {
			orientation = Orientation.SOUTH_EAST;
		} else if ((direction.x == 0) && (direction.y > 0)) {
			orientation = Orientation.SOUTH;
		} else if ((direction.x < 0) && (direction.y > 0)) {
			orientation = Orientation.SOUTH_WEST;
		} else if ((direction.x < 0) && (direction.y == 0)) {
			orientation = Orientation.WEST;
		} else if ((direction.x < 0) && (direction.y < 0)) {
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
	public final static boolean isBeyond(Orientation orientation,
			Vector2d position, Vector2d borderPosition) {
		return (((orientation == Orientation.NORTH) && (position.y <= borderPosition
				.y))
				|| ((orientation == Orientation.NORTH_EAST)
						&& (position.x >= borderPosition.x) && (position
						.y <= borderPosition.y))
				|| ((orientation == Orientation.EAST) && (position.x >= borderPosition
						.x))
				|| ((orientation == Orientation.SOUTH_EAST)
						&& (position.x >= borderPosition.x) && (position
						.y >= borderPosition.y))
				|| ((orientation == Orientation.SOUTH) && (position.y >= borderPosition
						.y))
				|| ((orientation == Orientation.SOUTH_WEST)
						&& (position.x <= borderPosition.x) && (position
						.y >= borderPosition.y))
				|| ((orientation == Orientation.WEST) && (position.x <= borderPosition
						.x)) || ((orientation == Orientation.NORTH_WEST)
				&& (position.x <= borderPosition.x) && (position
				.y <= borderPosition.y)));
	}
}
