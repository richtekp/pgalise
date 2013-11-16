/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.util;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.util.weathercollector.exceptions.SaveStationDataException;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.model.StationData;
import de.pgalise.util.weathercollector.model.MyExtendedServiceDataCurrent;
import de.pgalise.util.weathercollector.model.MyExtendedServiceDataForecast;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
@Singleton(name = "de.pgalise.util.weathercollector.util.JTADatabaseManager", mappedName = "de.pgalise.util.weathercollector.util.EntityDatabaseManager")
@Local
public class JTADatabaseManager implements EntityDatabaseManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(JTADatabaseManager.class);

	/**
	 * File path for property file
	 */
	private static final String DATABASE_FILE_PATH = "/database.properties";

	/**
	 * Name of the persistent unit
	 */
	@PersistenceContext(unitName = "pgalise")
	private EntityManager em;

	public JTADatabaseManager() {
	}

	/**
	 *  
	 * @param entityManagerFactory 
	 */
	public JTADatabaseManager(EntityManager entityManagerFactory) {
		this.em = entityManagerFactory;
	}

	/**
	 * Get Condition code
	 * 
	 * @param condition
	 *            Code als String
	 * @return condition code
	 */
	@Override
	public DefaultWeatherCondition getCondition(String condition) {
		// Get cities
		DefaultWeatherCondition result;
		try {
			TypedQuery<DefaultWeatherCondition> query = em.createNamedQuery("Condition.getConditionByString", DefaultWeatherCondition.class);
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
		// Get cities
		List<City> citylist;
		try {
			TypedQuery<City> query = em.createNamedQuery("City.getAll", City.class);
			citylist = query.getResultList();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Returns
		return (citylist == null) ? new ArrayList<City>(1) : citylist;
	}

	@Override
	public void saveServiceData(DefaultServiceDataHelper weather) {
		// Get city
		City city = weather.getCity();
		em.persist(city);

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

		// Get the last station data
		StationData lastData = this.getLastStationData(em);
		long lastTime = (lastData == null) ? 1 : lastData.getMeasureDate().getTime() + lastData.getMeasureTime().getTime();

		// Save all data
		boolean result = true;
		int count = 0;
		long actTime;
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
			em.merge(station_data);
			count++;
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
	private void deleteCurrentWeather(List<MyExtendedServiceDataCurrent> list, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		} else if ((list == null) || list.isEmpty()) {
			return;
		}

		for (MyExtendedServiceDataCurrent serviceData : list) {
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
	private void deleteForeCastWeather(List<MyExtendedServiceDataForecast> list, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		} else if ((list == null) || list.isEmpty()) {
			return;
		}

		// Remove
		for (MyExtendedServiceDataForecast serviceData : list) {
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
	private List<MyExtendedServiceDataCurrent> getServiceDataCurrent(City city, Date date, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		}

		// Get forecast service data
		TypedQuery<MyExtendedServiceDataCurrent> query = em.createNamedQuery("MyExtendedServiceDataCurrent.findByCityAndDate",
				MyExtendedServiceDataCurrent.class);
		query.setParameter("date", date);
		query.setParameter("city", city);
		List<MyExtendedServiceDataCurrent> result = query.getResultList();

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
	private List<MyExtendedServiceDataForecast> getServiceDataForecast(City city, Date date, EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("em");
		}

		// Get forecast service data
		TypedQuery<MyExtendedServiceDataForecast> query = em.createNamedQuery("MyExtendedServiceDataForecast.findByCityAndDate",
				MyExtendedServiceDataForecast.class);
		query.setParameter("date", date);
		query.setParameter("city", city);
		List<MyExtendedServiceDataForecast> result = query.getResultList();

		// Return list
		return result;
	}

	/**
	 * Saves entries of the current service weather
	 * 
	 * @param cityID
	 *            Primary key of the city
	 * @param serviceData
	 *            Current service weather data
	 */
	private void saveCurrentWeather(City city, MyExtendedServiceDataCurrent serviceData) {
		if ((serviceData == null) || (serviceData.getMeasureDate() == null)) {
			throw new IllegalArgumentException("serviceData");
		}

		// Delete old entries
		this.deleteCurrentWeather(this.getServiceDataCurrent(city, serviceData.getMeasureDate(), em), em);

		// Save data
		em.merge(serviceData);

		LOGGER.debug("Datensatz fuer Stadt " + city + " vom " + serviceData.getMeasureDate() + " gespeichert.", Level.INFO);
	}

	/**
	 * Saves forecast weather data
	 * 
	 * @param cityID
	 *            Primary key of the city
	 * @param serviceData
	 *            Set with forecasts for future days
	 */
	private void saveForecastWeather(City city, Set<MyExtendedServiceDataForecast> serviceData) {
		if ((serviceData == null) || serviceData.isEmpty()) {
			throw new IllegalArgumentException("serviceData");
		}

		// For all entries
		for (MyExtendedServiceDataForecast data : serviceData) {
			// Delete old entries
			this.deleteForeCastWeather(this.getServiceDataForecast(city, data.getMeasureDate(), em), em);

			// Save data
			em.merge(data); //need to merge due to references to city.referenceArea

			LOGGER.debug("Datensatz fuer Stadt " + city + " vom " + data.getMeasureDate() + " gespeichert.", Level.INFO);

		}
	}
}
