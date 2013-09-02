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
 
package de.pgalise.weathercollector.weatherservice;

import java.util.List;
import java.util.logging.Level;

import de.pgalise.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.weathercollector.model.City;
import de.pgalise.weathercollector.model.ServiceDataHelper;
import de.pgalise.weathercollector.util.DatabaseManager;
import de.pgalise.weathercollector.util.Log;
import javax.persistence.EntityManagerFactory;

/**
 * Gets and saves informations of various weather services
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Jun 23, 2012)
 */
public final class WeatherServiceManager {

	/**
	 * Class to save the informations
	 */
	public WeatherServiceSaver saver;

	/**
	 * Constructor
	 * 
	 * @param testmode
	 *            Option to enable the test mode (no database commits)
	 */
	public WeatherServiceManager(EntityManagerFactory entityManagerFactory) {
		this.saver = DatabaseManager.getInstance(entityManagerFactory);
	}

	/**
	 * Gets informations from weather services
	 */
	public void saveInformations(EntityManagerFactory entityManagerFactory) {
		// Read all cities
		List<City> cities = this.getCities();

		// Get all informations for every city
		if ((cities != null) && (cities.size() > 0)) {
			for (City city : cities) {
				// Get data
				ServiceDataHelper weather = this.getServiceData(city, entityManagerFactory);

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
		Log.writeLog("Laden der Staedte aus der Datenbank.", Level.INFO);

		List<City> cities = this.saver.getReferenceCities();

		Log.writeLog(cities.size() + " Staedte wurden geladen.", Level.INFO);

		return cities;
	}

	/**
	 * Returns the ServiceData for the given city
	 * 
	 * @param city
	 *            City
	 * @return ServiceData
	 */
	private ServiceDataHelper getServiceData(City city, EntityManagerFactory entityManagerFactory) {
		ServiceDataHelper data = null;

		Log.writeLog("Holen der Daten zur Stadt " + city.getName() + " beginnt.", Level.INFO);

		WeatherServiceContext context = new WeatherServiceContext();

		// Use best strategy
		data = context.getBestWeather(city, entityManagerFactory);

		Log.writeLog("Holen der Daten zur Stadt " + city.getName() + " beendet.", Level.INFO);

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
				Log.writeLog("Speichern der Wetterdaten zur Stadt " + data.getCity().getName() + " beginnt.", Level.INFO);
				this.saver.saveServiceData(data);
				Log.writeLog("Speichern der Wetterdaten zur Stadt " + data.getCity().getName() + " war erfolgreich.", Level.INFO);
			}
		} catch (IllegalArgumentException e) {
			Log.writeLog(e.getMessage(), Level.WARNING);
			e.printStackTrace();
		}
	}

}
