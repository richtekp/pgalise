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
 
package de.pgalise.util.weathercollector.weatherservice;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.model.WeatherCondition;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
import java.util.List;
import java.util.logging.Level;

import de.pgalise.util.weathercollector.util.DatabaseManager;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets and saves informations of various weather services
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Jun 23, 2012)
 */
public class DefaultWeatherServiceManager implements WeatherServiceManager<DefaultServiceDataHelper> {
	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultWeatherServiceManager.class);

	/**
	 * Class to save the informations
	 */
	private WeatherServiceSaver<DefaultServiceDataHelper> saver;
	
	private Set<ServiceStrategy<DefaultServiceDataHelper>> serviceStrategys;

	/**
	 * Constructor
	 * 
	 * @param weatherServiceSaver 
	 */
	public DefaultWeatherServiceManager(WeatherServiceSaver<DefaultServiceDataHelper> weatherServiceSaver) {
		this(weatherServiceSaver, null);
	}
	
	public DefaultWeatherServiceManager(WeatherServiceSaver<DefaultServiceDataHelper> weatherServiceSaver, Set<ServiceStrategy<DefaultServiceDataHelper>> serviceStrategys) {
		this.saver = weatherServiceSaver;
		this.serviceStrategys = serviceStrategys;
	}

	/**
	 * Gets informations from weather services
	 * 
	 * @param databaseManager 
	 */
	@Override
	public void saveInformations(DatabaseManager databaseManager) {
		// Read all cities
		List<City> cities = this.getCities();

		// Get all informations for every city
		if ((cities != null) && (cities.size() > 0)) {
			for (City city : cities) {
				// Get data
				DefaultServiceDataHelper weather;
				if(this.serviceStrategys == null || this.serviceStrategys.isEmpty()) {
					weather = this.getServiceData(city, databaseManager);
				}else {
					weather = this.getServiceData(city,
						databaseManager,
						serviceStrategys);
				}

				this.saveServiceData(weather);
			}
		}
	}

	/**
	 * Returns all cities from the database
	 * 
	 * @return Set with cities
	 */
	private List<City> getCities() {
		LOGGER.debug("Laden der Staedte aus der Datenbank.", Level.INFO);

		List<City> cities = this.getSaver().getReferenceCities();

		LOGGER.debug(cities.size() + " Staedte wurden geladen.", Level.INFO);

		return cities;
	}
	
	private DefaultServiceDataHelper getServiceData(City city, DatabaseManager databaseManager, Set<ServiceStrategy<DefaultServiceDataHelper>> serviceStrategys) {
		LOGGER.debug("Holen der Daten zur Stadt " + city.getName() + " beginnt.", Level.INFO);

		WeatherServiceContext<DefaultServiceDataHelper> context = new DefaultWeatherServiceContext(serviceStrategys);

		// Use best strategy
		DefaultServiceDataHelper data = context.getBestWeather(city, databaseManager);

		LOGGER.debug("Holen der Daten zur Stadt " + city.getName() + " beendet.", Level.INFO);

		return data;
		
	}

	/**
	 * Returns the ServiceData for the given city
	 * 
	 * @param city
	 *            City
	 * @return ServiceData
	 */
	private DefaultServiceDataHelper getServiceData(City city, DatabaseManager databaseManager) {
		DefaultServiceDataHelper data;

		LOGGER.debug("Holen der Daten zur Stadt " + city.getName() + " beginnt.", Level.INFO);

		DefaultWeatherServiceContext context = new DefaultWeatherServiceContext();

		// Use best strategy
		data = context.getBestWeather(city, databaseManager);

		LOGGER.debug("Holen der Daten zur Stadt " + city.getName() + " beendet.", Level.INFO);

		return data;
	}

	/**
	 * Saves all informations to the database
	 * 
	 * @param data
	 *            ServiceData
	 */
	private void saveServiceData(DefaultServiceDataHelper data) {
		if (data != null) {
			LOGGER.debug("Speichern der Wetterdaten zur Stadt " + data.getCity().getName() + " beginnt.", Level.INFO);
			this.getSaver().saveServiceData(data);
			LOGGER.debug("Speichern der Wetterdaten zur Stadt " + data.getCity().getName() + " war erfolgreich.", Level.INFO);
		}
	}

	/**
	 * @return the saver
	 */
	@Override
	public WeatherServiceSaver<DefaultServiceDataHelper> getSaver() {
		return saver;
	}

	/**
	 * @param saver the saver to set
	 */
	protected void setSaver(
		WeatherServiceSaver<DefaultServiceDataHelper> saver) {
		this.saver = saver;
	}

}
