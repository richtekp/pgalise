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
 
/**
 * 
 */
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.util.vector.Vector2d;

/**
 * Represents an interferer that shows errors caused by corrupted antenna calibration
 * 
 * @author Andreas
 * @version 1.0 (Nov 12, 2012)
 */
public final class GpsReceiverInterferer extends GpsBaseInterferer {

	/**
	 * File path for property file
	 */
	public static final String PROPERTIES_FILE_PATH = "/interferer_gps_receiver.properties";

	// @TODO GPSMapper als abhängigkeit hinzufügen um die VECTOR_UNIT zu bestimmen
	private static final double VECTOR_UNIT = 100.0;
	
	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 */
	public GpsReceiverInterferer(RandomSeedService randomseedservice) {
		super(randomseedservice, GpsReceiverInterferer.PROPERTIES_FILE_PATH);
	}

	@Override
	public Vector2d interfere(Vector2d mutablePosition, Vector2d realPosition, long simTime, final double vectorUnit) {
		// Should be changed?
		if (this.random.nextDouble() <= this.changeProbability) {
			double changeValue = (this.changeAmplitude / VECTOR_UNIT) * this.random.nextGaussian();
			return Vector2d.valueOf(mutablePosition.getX() + changeValue, mutablePosition.getY() + changeValue);
		}

		// Returns with no change
		return mutablePosition;
	}
}
