/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.util;

import de.pgalise.util.weathercollector.exceptions.SaveStationDataException;
import de.pgalise.util.weathercollector.model.City;
import de.pgalise.util.weathercollector.model.Condition;
import de.pgalise.util.weathercollector.model.ServiceDataCurrent;
import de.pgalise.util.weathercollector.model.ServiceDataForecast;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.model.StationData;
import de.pgalise.util.weathercollector.weatherservice.WeatherServiceSaver;
import de.pgalise.util.weathercollector.weatherstation.WeatherStationSaver;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
public class JTADatabaseManager extends BaseDatabaseManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(JTADatabaseManager.class);

	/**
	 * Instance of the class
	 */
	private static final Map<EntityManagerFactory,JTADatabaseManager> instances = new HashMap<>(1);

	/**
	 * File path for property file
	 */
	private static final String DATABASE_FILE_PATH = "/database.properties";

	/**
	 * Name of the persistent unit
	 */
	private EntityManagerFactory factory;

	/**
	 * Returns the instance of the database manager
	 * 
	 * @return instance of the database manager
	 */
	public static synchronized JTADatabaseManager getInstance(EntityManagerFactory entityManagerFactory) {
		JTADatabaseManager instance = instances.get(entityManagerFactory);
		if (instance == null) {
			instance = new JTADatabaseManager(entityManagerFactory);
			instances.put(entityManagerFactory,
				instance);
		}
		return instance;
	}

	/**
	 * Private constructor for singleton pattern
	 */
	private JTADatabaseManager(EntityManagerFactory entityManagerFactory) {
		this.factory = entityManagerFactory;
	}

	/**
	 * Get Condition code
	 * 
	 * @param condition
	 *            Code als String
	 * @return condition code
	 */
	public Condition getCondition(String condition) {
		final EntityManager em = this.factory.createEntityManager();

		// Get cities
		Condition result = null;
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

		// Get cities
		List<City> citylist;
		try {
			TypedQuery<City> query = em.createNamedQuery("City.getAll", City.class);
			citylist = query.getResultList();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

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
		int cityID = weather.getCity().getId();

		// Current weather
		this.saveCurrentWeather(cityID, weather.getCurrentCondition());

		// Forecast
		this.saveForecastWeather(cityID, weather.getForecastCondition());
	}

	@Override
	public boolean saveStationDataSet(Set<StationData> list) throws SaveStationDataException {
		if ((list == null) || list.isEmpty()) {
			throw new SaveStationDataException("No data available");
		}

		final EntityManager em = this.factory.createEntityManager();

		// Get the last station data
		StationData lastData = this.getLastStationData(em);
		long lastTime = (lastData == null) ? 1 : lastData.getDate().getTime() + lastData.getTime().getTime();

		// Save all data
		boolean result = true;
		int count = 0;
		long actTime = 0;
		for (StationData station_data : list) {
			actTime = station_data.getDate().getTime() + station_data.getTime().getTime();
			// Check if there is any data
			if (actTime <= lastTime) {
				LOGGER.debug("Holen von Daten abgebrochen! Datensaetze bis zum " + station_data.getDate() + " - "
						+ station_data.getTime() + " wurden gespeichert.", Level.INFO);

				// Break!
				result = false;
				break;
			}

			// Save data
			em.persist(station_data);
			count++;
		}

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
	private void deleteCurrentWeather(List<ServiceDataCurrent> list, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		} else if ((list == null) || list.isEmpty()) {
			return;
		}

		for (ServiceDataCurrent serviceData : list) {
			em.remove(serviceData);
		}
	}

	/**
	 * Deletes the forecast weather service informations
	 * 
	 * @param list
	 *            List with ServiceDataForecast objects
	 * @param em
	 *            EntityManager
	 */
	private void deleteForeCastWeather(List<ServiceDataForecast> list, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		} else if ((list == null) || list.isEmpty()) {
			return;
		}

		// Remove
		for (ServiceDataForecast serviceData : list) {
			em.remove(serviceData);
		}
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

		// Get station data
		TypedQuery<StationData> query = em.createNamedQuery("StationData.findLastData", StationData.class);
		query.setMaxResults(1);
		StationData result = query.getSingleResult();

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
	private List<ServiceDataCurrent> getServiceDataCurrent(int city, Date date, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		}

		// Get forecast service data
		TypedQuery<ServiceDataCurrent> query = em.createNamedQuery("ServiceDataCurrent.findByCityAndDate",
				ServiceDataCurrent.class);
		query.setParameter("date", date);
		query.setParameter("city", city);
		List<ServiceDataCurrent> result = query.getResultList();

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
	private List<ServiceDataForecast> getServiceDataForecast(int city, Date date, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		}

		// Get forecast service data
		TypedQuery<ServiceDataForecast> query = em.createNamedQuery("ServiceDataForecast.findByCityAndDate",
				ServiceDataForecast.class);
		query.setParameter("date", date);
		query.setParameter("city", city);
		List<ServiceDataForecast> result = query.getResultList();

		// Return list
		return result;
	}

	/**
	 * Saves entries of the current service weather
	 * 
	 * @param cityID
	 *            Primary key of the city
	 * @param service_data
	 *            Current service weather data
	 */
	private void saveCurrentWeather(int cityID, ServiceDataCurrent service_data) {
		if (cityID < 1) {
			throw new IllegalArgumentException("city");
		} else if ((service_data == null) || (service_data.getDate() == null)) {
			throw new IllegalArgumentException("service_data");
		}

		final EntityManager em = this.factory.createEntityManager();

		// Delete old entries
		this.deleteCurrentWeather(this.getServiceDataCurrent(cityID, service_data.getDate(), em), em);

		// Save data
		em.persist(service_data);

		// Close Manager ?
		if (em.isOpen()) {
			em.close();
		}

		LOGGER.debug("Datensatz fuer Stadt " + cityID + " vom " + service_data.getDate() + " gespeichert.", Level.INFO);
	}

	/**
	 * Saves forecast weather data
	 * 
	 * @param cityID
	 *            Primary key of the city
	 * @param service_data
	 *            Set with forecasts for future days
	 */
	private void saveForecastWeather(int cityID, Set<ServiceDataForecast> service_data) {
		if (cityID < 1) {
			throw new IllegalArgumentException("city");
		} else if ((service_data == null) || service_data.isEmpty()) {
			throw new IllegalArgumentException("service_data");
		}

		final EntityManager em = this.factory.createEntityManager();

		// For all entries
		for (ServiceDataForecast data : service_data) {
			// Delete old entries
			this.deleteForeCastWeather(this.getServiceDataForecast(cityID, data.getDate(), em), em);

			// Save data
			em.persist(data);

			LOGGER.debug("Datensatz fuer Stadt " + cityID + " vom " + data.getDate() + " gespeichert.", Level.INFO);

		}

		// Close Manager ?
		if (em.isOpen()) {
			em.close();
		}
	}
}
