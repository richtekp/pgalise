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

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsAtmosphereInterferer;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import javax.ejb.EJB;

/**
 * Represents an interferer that shows errors caused by the atmosphere
 * 
 * @author Marcus
 * @version 1.0 (Nov 14, 2012)
 */
public class DefaultGpsAtmosphereInterferer extends GpsBaseInterferer implements GpsAtmosphereInterferer {

	private static final long serialVersionUID = 1L;
  @EJB
  private IdGenerator idGenerator;

	/**
	 * Weather controller
	 */
  @EJB
	private WeatherController weatherController;

  public DefaultGpsAtmosphereInterferer() {
    super();
  }

	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 * @param weatherController
	 *            Weather Controller
	 */
	public DefaultGpsAtmosphereInterferer(RandomSeedService randomseedservice, final WeatherController weatherController)
			throws IllegalArgumentException {
		super(randomseedservice, DefaultGpsAtmosphereInterferer.PROPERTIES_FILE_PATH);
		if (weatherController == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("weatherController"));
		}
		this.weatherController = weatherController;
	}

	@Override
	public BaseCoordinate interfere(final BaseCoordinate mutablePosition, final BaseCoordinate realPosition, final long simTime) {
		// Should be changed?
		if (this.getRandom().nextDouble() <= this.getChangeProbability()) {
			double radiation = this.weatherController.getValue(WeatherParameterEnum.RADIATION, simTime, realPosition)
					.doubleValue();
			return new BaseCoordinate(idGenerator.getNextId(),mutablePosition.getX() + radiation, mutablePosition.getY() + radiation);
		}
		// Returns with no change
		return mutablePosition;
	}
}
