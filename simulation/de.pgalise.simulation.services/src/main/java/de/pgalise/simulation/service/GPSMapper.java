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

import de.pgalise.simulation.shared.geolocation.GeoLocation;
import javax.vecmath.Vector2d;

/**
 * An vector based coordinate system is internal used by the simulation 
 * to position the simulation entities (e.g. sensors, vehicles) in the city,
 * but for the visualization and sensor output GPS coordinates are needed.
 * The GPSMapper takes care to map between these two coordinates.
 * 
 * 
 * @author Mustafa
 * @author Timo
 * @version 1.0 (Oct 1, 2012)
 */
public interface GPSMapper {

	public Vector2d convertToVector(GeoLocation gps);

	public GeoLocation convertVectorToGPS(Vector2d vector);

	/**
	 * Converts velocity from km/h to vu/s
	 * 
	 * @return vel in vu/s
	 */
	public double convertVelocity(double vel);

	/**
	 * Converts a given double value from vector units/s to km/h
	 * 
	 * @param vel
	 *            in km/h
	 * @return
	 */
	public double convertVUStoKMH(double vel);

	public Vector2d getCenterPoint();

	public double getDistanceFromCenterToFarhestPoint();

	public double getHeight();

	public GeoLocation getOrigin();

	public double getVectorUnit();

	public double getWidth();

	public void setOrigin(GeoLocation gps);

}
