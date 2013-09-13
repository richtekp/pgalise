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

import de.pgalise.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.weathercollector.weatherservice.WeatherServiceSaver;
import de.pgalise.simulation.shared.city.City;
import java.util.List;
import java.util.logging.Level;

import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import de.pgalise.util.weathercollector.util.NonJTADatabaseManager;
import de.pgalise.weathercollector.model.ServiceDataHelper;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets and saves informations of various weather services
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Jun 23, 2012)
 */
public class WeatherServiceManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(WeatherServiceManager.class);

	/**
	 * Class to save the informations
	 */
	public WeatherServiceSaver saver;
	
	private Set<ServiceStrategy> serviceStrategys;

	/**
	 * Constructor
	 * 
	 * @param testmode
	 *            Option to enable the test mode (no database commits)
	 */
	public WeatherServiceManager(WeatherServiceSaver weatherServiceSaver) {
		this(weatherServiceSaver, null);
	}
	
	public WeatherServiceManager(WeatherServiceSaver weatherServiceSaver, Set<ServiceStrategy> serviceStrategys) {
		this.saver = weatherServiceSaver;
		this.serviceStrategys = serviceStrategys;
	}

	/**
	 * Gets informations from weather services
	 */
	public void saveInformations(DatabaseManager databaseManager) {
		// Read all cities
		List<City> cities = this.getCities();

		// Get all informations for every city
		if ((cities != null) && (cities.size() > 0)) {
			for (City city : cities) {
				// Get data
				ServiceDataHelper weather;
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

		List<City> cities = this.saver.getReferenceCities();

		LOGGER.debug(cities.size() + " Staedte wurden geladen.", Level.INFO);

		return cities;
	}
	
	private ServiceDataHelper getServiceData(City city, DatabaseManager databaseManager, Set<ServiceStrategy> serviceStrategys) {
		LOGGER.debug("Holen der Daten zur Stadt " + city.getName() + " beginnt.", Level.INFO);

		WeatherServiceContext context = new WeatherServiceContext(serviceStrategys);

		// Use best strategy
		ServiceDataHelper data = context.getBestWeather(city, databaseManager);

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
	private ServiceDataHelper getServiceData(City city, DatabaseManager databaseManager) {
		ServiceDataHelper data;

		LOGGER.debug("Holen der Daten zur Stadt " + city.getName() + " beginnt.", Level.INFO);

		WeatherServiceContext context = new WeatherServiceContext();

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
	private void saveServiceData(ServiceDataHelper data) {
		try {
			if (data != null) {
				LOGGER.debug("Speichern der Wetterdaten zur Stadt " + data.getCity().getName() + " beginnt.", Level.INFO);
				this.saver.saveServiceData(data);
				LOGGER.debug("Speichern der Wetterdaten zur Stadt " + data.getCity().getName() + " war erfolgreich.", Level.INFO);
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e); //is thrown if the query fails, so this should be thrown
		}
	}

}
