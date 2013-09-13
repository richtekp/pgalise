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
 
package de.pgalise.util.weathercollector.util;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.model.Condition;
import de.pgalise.simulation.weather.model.StationData;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import de.pgalise.util.weathercollector.exceptions.SaveStationDataException;
import de.pgalise.util.weathercollector.model.DefaultExtendedServiceDataCurrent;
import de.pgalise.util.weathercollector.model.DefaultExtendedServiceDataForecast;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
import de.pgalise.weathercollector.model.ExtendedServiceDataCurrent;
import de.pgalise.weathercollector.model.ExtendedServiceDataForecast;
import de.pgalise.weathercollector.model.ServiceDataHelper;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles all database statements of the weather collector. Uses the singleton pattern.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
public class NonJTADatabaseManager extends BaseDatabaseManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(NonJTADatabaseManager.class);

	/**
	 * Instance of the class
	 */
	private static Map<EntityManagerFactory,NonJTADatabaseManager> instances = new HashMap<>(1);

	/**
	 * File path for property file
	 */
	private static final String DATABASE_FILE_PATH = "/database.properties";

	/**
	 * Name of the persistent unit
	 */
	private String PERSISTENT_UNIT_NAME = "weather_data";

	/**
	 * Returns the instance of the database manager
	 * 
	 * @return instance of the database manager
	 */
	public static synchronized NonJTADatabaseManager getInstance(EntityManagerFactory entityManagerFactory) {
		NonJTADatabaseManager instance = instances.get(entityManagerFactory);
		if (instance == null) {
			instance = new NonJTADatabaseManager(entityManagerFactory);
			instances.put(entityManagerFactory,
				instance);
		}
		return instance;
	}

	/**
	 * Factory for the database connection
	 */
	private EntityManagerFactory factory;

	/**
	 * Private constructor for singleton pattern
	 */
	private NonJTADatabaseManager() {
		Properties database = null;

		// Read database props
		try (InputStream propInFile = NonJTADatabaseManager.class.getResourceAsStream(DATABASE_FILE_PATH)) {
			database = new Properties();
			database.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Set persistence unit name
		this.factory = Persistence.createEntityManagerFactory(PERSISTENT_UNIT_NAME, database);
	}

	private NonJTADatabaseManager(EntityManagerFactory factory) {
		this.factory = factory;
	}

	/**
	 * Get Condition code
	 * 
	 * @param condition
	 *            Code als String
	 * @return condition code
	 */
	@Override
	public Condition getCondition(String condition) {
		final EntityManager em = this.factory.createEntityManager();

		// Get cities
		Condition result;
		try {
			TypedQuery<Condition> query = em.createNamedQuery("Condition.getConditionByString", Condition.class);
			query.setParameter("condition", condition);
			query.setMaxResults(1);
			result = query.getSingleResult();
		} catch (Exception e) {
			return null;
		}

		// Returns
		return result;
	}

	@Override
	public List<City> getReferenceCities() {
		final EntityManager em = this.factory.createEntityManager();

		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		// Get cities
		List<City> citylist;
		try {
			TypedQuery<City> query = em.createNamedQuery("City.getAll", City.class);
			citylist = query.getResultList();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		tx.commit();

		// Close Manager
		if (em.isOpen()) {
			em.close();
		}

		// Returns
		return (citylist == null) ? new ArrayList<City>() : citylist;
	}

	@Override
	public void saveServiceData(ServiceDataHelper weather) {
		// Get city
		City city = weather.getCity();

		// Current weather
		this.saveCurrentWeather(city, weather.getCurrentCondition());

		// Forecast
		this.saveForecastWeather(city, weather.getForecastConditions());
	}

	@Override
	public boolean saveStationDataSet(Set<StationData> list) throws SaveStationDataException {
		if ((list == null) || list.isEmpty()) {
			throw new SaveStationDataException("No data available");
		}

		final EntityManager em = this.factory.createEntityManager();
		final EntityTransaction tx = em.getTransaction();

		// Get the last station data
		StationData lastData = this.getLastStationData(em);
		long lastTime = (lastData == null) ? 1 : lastData.getMeasureDate().getTime() + lastData.getMeasureTime().getTime();

		// Save all data
		tx.begin();
		boolean result = true;
		int count = 0;
		long actTime = 0;
		for (StationData station_data : list) {
			actTime = station_data.getMeasureDate().getTime() + station_data.getMeasureTime().getTime();
			// Check if there is any data
			if (actTime <= lastTime) {
				LOGGER.debug("Holen von Daten abgebrochen! Datensaetze bis zum " + station_data.getMeasureDate() + " - "
						+ station_data.getMeasureTime() + " wurden gespeichert.", Level.INFO);

				// Break!
				result = false;
				break;
			}

			// Save data
			em.persist(station_data);
			count++;
		}

		tx.commit();

		// Close Manager
		if (em.isOpen()) {
			em.close();
		}

		LOGGER.debug(count + " Datensaetze wurden gespeichert.", Level.INFO);

		// Can save new data?
		return result;
	}

	/**
	 * Deletes the current weather service informations
	 * 
	 * @param list
	 *            List with ServiceDataCurrent objects
	 * @param em
	 *            EntityManager
	 */
	private void deleteCurrentWeather(List<DefaultExtendedServiceDataCurrent> list, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		} else if ((list == null) || list.isEmpty()) {
			return;
		}

		final EntityTransaction tx = em.getTransaction();

		// Remove
		tx.begin();
		for (DefaultExtendedServiceDataCurrent serviceData : list) {
			em.remove(serviceData);
		}
		tx.commit();
	}

	/**
	 * Deletes the forecast weather service informations
	 * 
	 * @param list
	 *            List with ServiceDataForecast objects
	 * @param em
	 *            EntityManager
	 */
	private void deleteForeCastWeather(List<DefaultExtendedServiceDataForecast> list, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		} else if ((list == null) || list.isEmpty()) {
			return;
		}

		final EntityTransaction tx = em.getTransaction();

		// Remove
		tx.begin();
		for (DefaultExtendedServiceDataForecast serviceData : list) {
			em.remove(serviceData);
		}
		tx.commit();
	}

	/**
	 * Returns the last entry of station data from the database
	 * 
	 * @param em
	 *            EntityManager
	 * @return last entry of station data
	 */
	private StationData getLastStationData(EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		}

		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		// Get station data
		TypedQuery<StationData> query = em.createNamedQuery("StationData.findLastData", StationData.class);
		query.setMaxResults(1);
		StationData result = query.getSingleResult();

		tx.commit();

		// Returns
		return result;
	}

	/**
	 * Returns the list of ServiceDataCurrent objects to the given city and date
	 * 
	 * @param city
	 *            City
	 * @param date
	 *            Date
	 * @param em
	 *            EntityManager
	 * @return List with ServiceDataCurrent objects
	 */
	private List<DefaultExtendedServiceDataCurrent> getServiceDataCurrent(City city, Date date, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		}

		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		// Get forecast service data
		TypedQuery<DefaultExtendedServiceDataCurrent> query = em.createNamedQuery(
			String.format("%s.findByCityAndDate", DefaultExtendedServiceDataCurrent.class.getSimpleName()),
			DefaultExtendedServiceDataCurrent.class
		);
		query.setParameter("date", date);
		query.setParameter("city", city);
		List<DefaultExtendedServiceDataCurrent> result = query.getResultList();

		tx.commit();

		// Return list
		return result;
	}

	/**
	 * Returns the list of ServiceDataForecast objects to the given city and date
	 * 
	 * @param city
	 *            City
	 * @param date
	 *            Date
	 * @param em
	 *            EntityManager
	 * @return List with ServiceDataForecast objects
	 */
	private List<DefaultExtendedServiceDataForecast> getServiceDataForecast(City city, Date date, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		}

		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		// Get forecast service data
		TypedQuery<DefaultExtendedServiceDataForecast> query = em.createNamedQuery(
			String.format("%s.findByCityAndDate", DefaultExtendedServiceDataForecast.class.getSimpleName()),
			DefaultExtendedServiceDataForecast.class
		);
		query.setParameter("date", date);
		query.setParameter("city", city);
		List<DefaultExtendedServiceDataForecast> result = query.getResultList();

		tx.commit();

		// Return list
		return result;
	}

	/**
	 * Saves entries of the current service weather (using 
	 * {@link EntityManager#merge(java.lang.Object) }, so use <tt>merge</tt> 
	 * after modifications to save them to the database)
	 * 
	 * @param city
	 *            Primary key of the city
	 * @param serviceData
	 *            Current service weather data
	 */
	private void saveCurrentWeather(City city, ExtendedServiceDataCurrent serviceData) {
		if ((serviceData == null) || (serviceData.getMeasureDate() == null)) {
			throw new IllegalArgumentException("serviceData");
		}

		final EntityManager em = this.factory.createEntityManager();

		// Delete old entries
		this.deleteCurrentWeather(this.getServiceDataCurrent(city, serviceData.getMeasureDate(), em), em);

		final EntityTransaction tx = em.getTransaction();

		// Save all data
		tx.begin();

		// Save data
		em.merge(serviceData);

		// Commit
		tx.commit();

		// Close Manager ?
		if (em.isOpen()) {
			em.close();
		}

		LOGGER.debug("Datensatz fuer Stadt " + city + " vom " + serviceData.getMeasureDate() + " gespeichert.", Level.INFO);
	}

	/**
	 * Saves forecast weather data (using 
	 * {@link EntityManager#merge(java.lang.Object) }, so use <tt>merge</tt> 
	 * after modifications to save them to the database)
	 * 
	 * @param cityID
	 *            Primary key of the city
	 * @param service_data
	 *            Set with forecasts for future days
	 */
	private void saveForecastWeather(City cityID, Set<ExtendedServiceDataForecast> service_data) {
		if ((service_data == null) || service_data.isEmpty()) {
			throw new IllegalArgumentException("service_data");
		}

		final EntityManager em = this.factory.createEntityManager();

		// For all entries
		for (ExtendedServiceDataForecast data : service_data) {
			// Delete old entries
			this.deleteForeCastWeather(this.getServiceDataForecast(cityID, data.getMeasureDate(), em), em);

			final EntityTransaction tx = em.getTransaction();

			// Save all data
			tx.begin();

			// Save data
			em.merge(data);

			// Commit
			tx.commit();

			LOGGER.debug("Datensatz fuer Stadt " + cityID + " vom " + data.getMeasureDate() + " gespeichert.", Level.INFO);

		}

		// Close Manager ?
		if (em.isOpen()) {
			em.close();
		}
	}

}
