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
package de.pgalise.util.weathercollector.app;

import de.pgalise.util.weathercollector.WeatherCollector;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import de.pgalise.util.weathercollector.weatherservice.DefaultWeatherServiceManager;
import de.pgalise.util.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.util.weathercollector.weatherservice.WeatherServiceManager;
import de.pgalise.util.weathercollector.weatherstation.DefaultWeatherStationManager;
import de.pgalise.util.weathercollector.weatherstation.StationStrategy;
import de.pgalise.util.weathercollector.weatherstation.WeatherStationManager;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.Stateful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the entrance point of the weather collector.
 *
 * @author Andreas Rehfeldt
 * @version 2.1 (Jun 24, 2012)
 */
@Stateful
public class DefaultWeatherCollector implements
	WeatherCollector {

	private final static Logger LOGGER = LoggerFactory.getLogger(
		DefaultWeatherCollector.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 */
	public DefaultWeatherCollector() {
	}

	@Override
	public void collectServiceData(
		DatabaseManager weatherServiceSaver,
		Set<ServiceStrategy> serviceStrategys) {
		WeatherServiceManager collector = new DefaultWeatherServiceManager(
			weatherServiceSaver,
			serviceStrategys);

		// Get informations and save them
		LOGGER.debug("### --- Wetterdienste --- ###",
			Level.INFO);
		collector.saveInformations(weatherServiceSaver);
	}

	/**
	 * Collect weather informations of the weather services
	 */
	@Override
	public void collectServiceData(
		DatabaseManager weatherServiceSaver) {
		collectServiceData(weatherServiceSaver,
			null);
	}

	@Override
	public void collectStationData(
		DatabaseManager databaseManager,
		Set<StationStrategy> stationStrategys) {
		WeatherStationManager collector = new DefaultWeatherStationManager(
			databaseManager,
			stationStrategys);

		// Get informations and save them
		LOGGER.debug("### --- Wetterstationen --- ###",
			Level.INFO);
		collector.saveInformations();
	}

	/**
	 * Collect weather informations of the weather stations
	 */
	@Override
	public void collectStationData(
		DatabaseManager databaseManager) {
		collectStationData(databaseManager,
			null);
	}

}
