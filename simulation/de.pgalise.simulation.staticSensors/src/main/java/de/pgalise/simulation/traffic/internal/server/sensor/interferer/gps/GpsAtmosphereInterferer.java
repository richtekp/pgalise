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

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.util.vector.Vector2d;

/**
 * Represents an interferer that shows errors caused by the atmosphere
 * 
 * @author Marcus
 * @version 1.0 (Nov 14, 2012)
 */
public final class GpsAtmosphereInterferer extends GpsBaseInterferer {

	/**
	 * File path for property file
	 */
	public static final String PROPERTIES_FILE_PATH = "/interferer_gps_atmosphere.properties";

	/**
	 * Weather controller
	 */
	private final WeatherController weatherController;

	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 * @param weatherController
	 *            Weather Controller
	 */
	public GpsAtmosphereInterferer(RandomSeedService randomseedservice, final WeatherController weatherController)
			throws IllegalArgumentException {
		super(randomseedservice, GpsAtmosphereInterferer.PROPERTIES_FILE_PATH);
		if (weatherController == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("weatherController"));
		}
		this.weatherController = weatherController;
	}

	@Override
	public Vector2d interfere(final Vector2d mutablePosition, final Vector2d realPosition, final long simTime,
			final double vectorUnit) {
		// Should be changed?
		if (this.getRandom().nextDouble() <= this.changeProbability) {
			double radiation = this.weatherController.getValue(WeatherParameterEnum.RADIATION, simTime, realPosition)
					.doubleValue();
			return Vector2d.valueOf(mutablePosition.getX() + radiation, mutablePosition.getY() + radiation);
		}
		// Returns with no change
		return mutablePosition;
	}
}
