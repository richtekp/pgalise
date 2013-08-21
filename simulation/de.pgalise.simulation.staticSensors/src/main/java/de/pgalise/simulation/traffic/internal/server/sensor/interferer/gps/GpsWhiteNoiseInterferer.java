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
import javax.vecmath.Vector2d;

/**
 * Represents an interferer that creates generally low errors
 * 
 * @author Marcus
 * @version 1.0 (Nov 17, 2012)
 */
public class GpsWhiteNoiseInterferer extends GpsBaseInterferer {

	/**
	 * File path for property file
	 */
	public static final String PROPERTIES_FILE_PATH = "/interferer_gps_whitenoise.properties";

	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 */
	public GpsWhiteNoiseInterferer(RandomSeedService randomseedservice) {
		super(randomseedservice, GpsWhiteNoiseInterferer.PROPERTIES_FILE_PATH);
	}

	@Override
	public Coordinate interfere(final Coordinate mutablePosition, final Coordinate realPosition, final long simTime) {
		// Should be changed?
		if (this.getRandom().nextDouble() <= this.changeProbability) {
			final double x = 1d / ((1d / ((this.random.nextDouble() * this.changeAmplitude) + Double.MIN_NORMAL)));
			final double y = 1d / ((1d / ((this.random.nextDouble() * this.changeAmplitude) + Double.MIN_NORMAL)));
			return new Coordinate(
					this.getRandom().nextBoolean() ? mutablePosition.x + x : mutablePosition.x - x, this
							.getRandom().nextBoolean() ? mutablePosition.y + y : mutablePosition.y - y);
		}
		return mutablePosition;
	}
}
