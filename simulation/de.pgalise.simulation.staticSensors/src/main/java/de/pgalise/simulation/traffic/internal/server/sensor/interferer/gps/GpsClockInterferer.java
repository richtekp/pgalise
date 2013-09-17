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
 
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.service.RandomSeedService;

/**
 * Represents an interferer that shows errors caused by satellite clocks
 * 
 * @author Andreas
 * @version 1.0 (Nov 12, 2012)
 */
public class GpsClockInterferer extends GpsBaseInterferer {

	/**
	 * File path for property file
	 */
	public static final String PROPERTIES_FILE_PATH = "/interferer_gps_clock.properties";
	
	// @TODO GPSMapper als abhängigkeit hinzufügen um die VECTOR_UNIT zu bestimmen
	private static final double VECTOR_UNIT = 100.0;

	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 */
	public GpsClockInterferer(RandomSeedService randomseedservice) {
		super(randomseedservice, GpsClockInterferer.PROPERTIES_FILE_PATH);
	}

	@Override
	public Coordinate interfere(Coordinate mutablePosition, Coordinate realPosition, long simTime) {
		// Should be changed?
		if (this.random.nextDouble() <= this.changeProbability) {
			double changeValue = (this.changeAmplitude / VECTOR_UNIT) * this.random.nextGaussian();
			return new Coordinate(mutablePosition.x + changeValue, mutablePosition.y + changeValue);
		}

		// Returns with no change
		return mutablePosition;
	}
}
