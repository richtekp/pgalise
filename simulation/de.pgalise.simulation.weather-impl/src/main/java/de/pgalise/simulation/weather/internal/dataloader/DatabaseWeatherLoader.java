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
 
package de.pgalise.simulation.weather.internal.dataloader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.DefaultServiceDataCurrent;
import de.pgalise.simulation.weather.internal.dataloader.entity.AbstractStationData;
import de.pgalise.simulation.weather.internal.dataloader.entity.DefaultCondition;
import de.pgalise.simulation.weather.internal.dataloader.entity.DefaultServiceDataForecast;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataNormal;
import de.pgalise.simulation.weather.model.Condition;
import de.pgalise.simulation.weather.model.MutableStationData;
import de.pgalise.simulation.weather.model.ServiceDataForecast;
import de.pgalise.simulation.weather.model.StationData;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.simulation.weather.util.DateConverter;
import java.sql.Time;
import javax.measure.Measure;
import javax.measure.unit.SI;
import javax.persistence.NoResultException;

/**
 * This class loads the weather station data from the database. <br />
 * <br />
 * The file jndi.properties describes the configuration to the database. In 
 * particular, the file includes the settingsof the Java Persistence API (JPA) 
 * and the database authorization. To change the database, one has to change the
 * database driver in this file. Notice that the XML file persistence.xml in 
 * the META-INF folder configures the JPA process of this component. <br />
 * <br />
 * The file weatherloader.properties explains the configuration of the JPA in 
 * more detail. It indicates the used persistence unit and the Java classes for 
 * the JPA entities concerning the {@link StationData}. Notice that the 
 * database tables must be created before the {@link WeatherService} has 
 * started for the first time, otherwise an exception will occur. If there are 
 * changes in the scheme, one has to change also the JPA entities in the 
 * package dataloader.entity or the properties in the file 
 * weatherloader.properties regarding the classes for {@link StationData}.
 * 
 * @author Andreas Rehfeldt
 * @author Marcus
 * @version 1.0 (01.07.2012)
 */
@Lock(LockType.READ)
@Local
@Singleton(name = "de.pgalise.simulation.weather.dataloader.WeatherLoader", mappedName = "de.pgalise.simulation.weather.dataloader.DatabaseWeatherLoader")
public class DatabaseWeatherLoader implements WeatherLoader {

	private EntityManager entityManager;

	/**
	 * File path for property file
	 */
	private static final String PROPERTIES_FILE_PATH = "/weatherloader.properties";

	/**
	 * Option to take normal station data
	 */
	private boolean loadOption = true;

	/**
	 * Properties
	 */
	private Properties prop = null;

	/**
	 * Class for station class
	 */
	private Class<AbstractStationData> stationDataClass = null;

	/**
	 * Name for station class
	 */
	private String stationDataClassName = "";

	/**
	 * Default constructor
	 */
	public DatabaseWeatherLoader() {
		// Read props
		try (InputStream propInFile = DatabaseWeatherLoader.class
				.getResourceAsStream(DatabaseWeatherLoader.PROPERTIES_FILE_PATH)) {
			this.prop = new Properties();
			this.prop.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Change load options
		this.changeLoadOption();
	}

	@PersistenceContext(unitName = "weather_data")
	public void setEntityManager(EntityManager entityManager) {
			this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
			return entityManager;
	}

	/**
	 * ??? checks whether data for the day of the <tt>timestamp</tt> and the preceeding and following day are available (both at the same time of the day)
	 * @param timestamp
	 * @return 
	 */
	@Override
	public boolean checkStationDataForDay(long timestamp) {
		List<AbstractStationData> weatherList;
		AbstractStationData lastday, nextday;
//		try {
			// Previous day
			lastday = this.loadLastStationDataForDate(DateConverter.getPreviousDay(timestamp));

			// First day
			weatherList = this.getStationData(timestamp);

			// Next day
			nextday = this.loadFirstStationDataForDate(DateConverter.getNextDay(timestamp));
//		} catch (Exception e) {
//			return false;
//		}

		// Check for null values
		if ((lastday == null) || (nextday == null) || (weatherList == null) || weatherList.isEmpty()) {
			return false;
		}

		// Check for null values
		for (AbstractStationData stationData : weatherList) {
			if ((stationData.getAirPressure() == null) || (stationData.getLightIntensity() == null)
					|| (stationData.getPerceivedTemperature() == null)
					|| (stationData.getPrecipitationAmount() == null) || (stationData.getRadiation() == null)
					|| (stationData.getRelativHumidity() == null) || (stationData.getTemperature() == null)
					|| (stationData.getWindDirection() == null) || (stationData.getWindVelocity() == null)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ServiceDataForecast loadCurrentServiceWeatherData(long timestamp, City city)   {

		// Get the data
		DefaultServiceDataCurrent data;
		TypedQuery<DefaultServiceDataCurrent> query = this.entityManager.createNamedQuery("DefaultServiceDataCurrent.findByDate",
					DefaultServiceDataCurrent.class);
		query.setParameter("date", new Date(timestamp));
		query.setParameter("city", city);
		data = query.getSingleResult();
		

		// Copy informations to the weather object
		ServiceDataForecast weather = new DefaultServiceDataForecast(
			data.getMeasureDate(),
			data.getMeasureTime(),
			data.getCity(), 
			data.getTemperature(),
			data.getTemperature(),
			data.getRelativHumidity(), 
			data.getWindDirection(),
			data.getWindVelocity(),
			DefaultCondition.retrieveCondition(Condition.UNKNOWN_CONDITION_CODE)
		);
		return weather;
	}

	/**
	 * Returns the ServiceDataForecast object for the given city and timestamp
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @param city
	 *            Database id to the city
	 * @return ServiceDataForecast
	 */
	@Override
	public ServiceDataForecast loadForecastServiceWeatherData(long timestamp, City city) {

		// Get the data
		ServiceDataForecast data;
		TypedQuery<DefaultServiceDataForecast> query = this.entityManager.createNamedQuery("DefaultServiceDataForecast.findByDate",
				DefaultServiceDataForecast.class);
		if(query == null) {
			return null;
		}
		query.setParameter("date", new Date(timestamp));
		query.setParameter("city", city);
		data = query.getSingleResult();

		// Copy informations to the weather object
		DefaultServiceDataForecast weather = new DefaultServiceDataForecast(
			data.getMeasureDate(),
			data.getMeasureTime(),
			data.getCity(), 
			data.getTemperatureLow(), 
			data.getTemperatureHigh(), 
			data.getRelativHumidity(), 
			data.getWindDirection(),
			data.getWindVelocity(),
			DefaultCondition.retrieveCondition(Condition.UNKNOWN_CONDITION_CODE)
		);
		return weather;
	}

	/**
	 * Returns the weather data of the weather station for the day of <tt>timestamp</tt>, as well as the preceeding and following day (at the same time of the day). They are sorted ascending. This method assumes that the availability of weather data has been check before (using {@link #checkStationDataForDay(long) }). If the check didin't occur the returned <tt>WeatherMap</tt> possibly contains <code>null</code>.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return WeatherMap with weather data
	 */
	@Override
	public WeatherMap loadStationData(long timestamp) {
		// Get list
		List<AbstractStationData> weatherList = this.getStationData(timestamp);

		// Add values of the previous day
		weatherList.add(this.loadLastStationDataForDate(DateConverter.getPreviousDay(timestamp)));

		// Add values of the next day
		weatherList.add(this.loadFirstStationDataForDate(DateConverter.getNextDay(timestamp)));

		// Sort list
		Collections.sort(weatherList);

		// Interpolate
		return this.createWeatherMap(weatherList, WeatherMap.INTERPOLATE_INTERVAL);
	}

	@Override
	public void setLoadOption(boolean takeNormalData) {
		if (takeNormalData != this.loadOption) {
			this.changeLoadOption();
		}

		this.loadOption = takeNormalData;

	}

	/**
	 * Changes the classes for normal or aggregate station data
	 */
	@SuppressWarnings("unchecked")
	private void changeLoadOption() {
		// Normal or aggregate?
		String data = "station_data_normal";
		String classdata = "station_data_normal_classname";
		if (!this.loadOption) {
			data = "station_data_aggregate";
			classdata = "station_data_aggregate_classname";
		}

		// Set class for StationData
		try {
			this.stationDataClass = (Class<AbstractStationData>) Class.forName(this.prop.getProperty(data));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		// Set class name
		this.stationDataClassName = this.prop.getProperty(classdata);
	}

	/**
	 * Create the weather map by interpolate all data with the given interval
	 * 
	 * @param list
	 *            Sorted list with station data
	 * @param interval
	 *            interpolate interval
	 * @return WeatherMap
	 */
	private WeatherMap createWeatherMap(List<AbstractStationData> list, int interval) {
		WeatherMap map = new StationDataMap();

		// No interpolate! --> Error
		if (list.isEmpty()) {
			return map;
		}

		// Add missing values
		MutableStationData weather;
		for (int i = 1; i < list.size(); i++) {
			AbstractStationData min = list.get(i - 1);
			AbstractStationData max = list.get(i);

			// Set times and delete seconds and milliseconds
			long minTime = min.getMeasureDate().getTime() + min.getMeasureTime().getTime() + DateConverter.HOUR;
			minTime -= minTime % DateConverter.MINUTE;
			long actTime = minTime + interval;
			long maxTime = max.getMeasureDate().getTime() + max.getMeasureTime().getTime() + DateConverter.HOUR;
			maxTime -= maxTime % DateConverter.MINUTE;

			// Add min to map
			weather = new StationDataNormal(
				new Date(minTime), 
				new Time(minTime), 
				min.getAirPressure(), 
				min.getLightIntensity(), 
				min.getPerceivedTemperature(), min.getTemperature(),
				min.getPrecipitationAmount(), 
				min.getRadiation(), 
				min.getRelativHumidity(), 
				min.getWindDirection(), 
				min.getWindVelocity()
			);
			map.put(minTime, weather);

			// Calculate missing values between max and min
			while (actTime < maxTime) {
				// New Weather
				actTime -= actTime % DateConverter.MINUTE;

				/*
				 * Values
				 */
				int airPressure = (int) this.interpolate(minTime, min.getAirPressure(), maxTime, max.getAirPressure(),
						actTime);

				int lightIntensity = (int) this.interpolate(minTime, min.getLightIntensity(), maxTime,
						max.getLightIntensity(), actTime);

				float perceivedTemperature = (float) this.interpolate(minTime, min.getPerceivedTemperature(), maxTime,
						max.getPerceivedTemperature(), actTime);

				float temperature = (float) this.interpolate(minTime, min.getTemperature().floatValue(SI.CELSIUS), maxTime,
						max.getTemperature().floatValue(SI.CELSIUS), actTime);

				float precipitationAmount = (float) this.interpolate(minTime, min.getPrecipitationAmount(), maxTime,
						max.getPrecipitationAmount(), actTime);

				int radiation = (int) this.interpolate(minTime, min.getRadiation(), maxTime, max.getRadiation(),
						actTime);

				float relativHumidity = (float) this.interpolate(minTime, min.getRelativHumidity(), maxTime,
						max.getRelativHumidity(), actTime);

				float windDirection = (int) this.interpolate(minTime, min.getWindDirection(), maxTime,
						max.getWindDirection(), actTime);

				float windVelocity = (float) this.interpolate(minTime, min.getWindVelocity(), maxTime,
						max.getWindVelocity(), actTime);
				weather = new StationDataNormal(
					new Date(actTime), 
					new Time(actTime),
					airPressure, 
					lightIntensity, 
					perceivedTemperature, 
					Measure.valueOf(temperature, SI.CELSIUS), 
					precipitationAmount, 
					radiation, 
					relativHumidity, windDirection, windVelocity);

				// Add a new object
				map.put(actTime, weather);

				// Add the interpolate interval
				actTime += interval;
			}
		}

		// Return
		return map;
	}

	/**
	 * Returns a query for the given date.
	 * 
	 * @param em
	 *            EntityManager
	 * @param type
	 *            class
	 * @param date
	 *            Date
	 * @param datecolumn
	 *            Date colomn in the database
	 * @return List with the given class objects
	 */
	@SuppressWarnings("unused")
	private <T> TypedQuery<T> getDateQuery(EntityManager em, Class<T> type, Date date, String datecolumn, int city) {
		// Build Query with the help of CriteriaBuilder
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = builder.createQuery(type);

		// From
		Root<T> from = criteriaQuery.from(type);

		// Select
		CriteriaQuery<T> select = criteriaQuery.select(from);

		// Compare month and day
		Expression<Integer> month = builder.function("month", Integer.class, from.get(datecolumn));
		Expression<Integer> day = builder.function("day", Integer.class, from.get(datecolumn));

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);

		// Where
		Predicate predicate1 = builder.equal(month, (cal.get(Calendar.MONTH) + 1));
		Predicate predicate2 = builder.equal(day, cal.get(Calendar.DAY_OF_MONTH));

		// City
		if (city > 0) {
			Predicate predicate3 = builder.equal(day, cal.get(Calendar.DAY_OF_MONTH));
			criteriaQuery.where(builder.and(predicate1, predicate2, predicate3));
		} else {
			criteriaQuery.where(builder.and(predicate1, predicate2));
		}

		// Return
		return em.createQuery(select);
	}

	/**
	 * Returns the station data for the given timestamp
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return List with the station data or <code>null</code> if no such data exists
	 */
	private List<AbstractStationData> getStationData(long timestamp) {
		// Check station data
		if ((this.stationDataClass == null) || !(this.stationDataClass.getSuperclass().equals(AbstractStationData.class))) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("stationDataClass"));
		}

		// Get information for that day
		List<AbstractStationData> weatherList;
			TypedQuery<AbstractStationData> query = this.entityManager.createNamedQuery(String.format("%s.findByDate", stationDataClass.getSimpleName()),
					this.stationDataClass);
		query.setParameter("date", new Date(timestamp));
		weatherList = query.getResultList();

		return weatherList;
	}

	/**
	 * Interpolate two values
	 * 
	 * @param minTime
	 *            min time
	 * @param minValue
	 *            min value
	 * @param maxTime
	 *            max time
	 * @param maxValue
	 *            max value
	 * @param actTime
	 *            time for the new value
	 * @return interpolated value
	 */
	private double interpolate(long minTime, float minValue, long maxTime, float maxValue, long actTime) {
		/*
		 * Linear interpolation: f(x)=f0+((f1-f0)/(x1-x0))*(x-x0)
		 */
		double value = minValue + (((maxValue - minValue) / (maxTime - minTime)) * (actTime - minTime));

		/*
		 * Round
		 */
		value = Math.round(value * 1000.) / 1000.;

		return value;
	}

	/**
	 * Returns the first station data for the given timestamp
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return First station data instance
	 */
	private AbstractStationData loadFirstStationDataForDate(long timestamp) {
		// Check station data
		if ((this.stationDataClass == null) || !(this.stationDataClass.getSuperclass().equals(AbstractStationData.class))) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("stationDataClass"));
		}

		// Get first result of that day
		AbstractStationData data;
		TypedQuery<AbstractStationData> query = this.entityManager.createNamedQuery(this.stationDataClassName
				+ ".findFirstEntryByDate", this.stationDataClass);
		query.setParameter("date", new Date(timestamp));
		query.setMaxResults(1);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Returns the last station data for the given timestamp 
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return Last station data instance or <code>null</code> if no data has been found
	 */
	private AbstractStationData loadLastStationDataForDate(long timestamp) {

		// Check station data
		if ((this.stationDataClass == null) || !(this.stationDataClass.getSuperclass().equals(AbstractStationData.class))) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("stationDataClass"));
		}

		// Get last result of that day
		TypedQuery<AbstractStationData> query = this.entityManager.createNamedQuery(
				this.stationDataClassName + ".findLastEntryByDate", this.stationDataClass);
		query.setParameter("date", new Date(timestamp));
		query.setMaxResults(1);
		try {
			return query.getSingleResult();
		}catch(NoResultException ex) {
			return null;
		}
	}
}
