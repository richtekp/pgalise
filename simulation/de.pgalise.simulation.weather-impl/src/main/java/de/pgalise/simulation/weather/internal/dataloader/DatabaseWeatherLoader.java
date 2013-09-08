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
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.dataloader.ReferenceCityEvaluator;
import de.pgalise.simulation.weather.dataloader.ServiceWeather;
import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.internal.dataloader.entity.ServiceDataForecast;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationData;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataMap;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.simulation.weather.util.DateConverter;
import javax.persistence.Query;

/**
 * This class loads the weather station data from the database. <br />
 * <br />
 * The file jndi.properties describes the configuration to the database. In particular, the file includes the settings
 * of the Java Persistence API (JPA) and the database authorization. To change the database, one has to change the
 * database driver in this file. Notice that the XML file persistence.xml in the META-INF folder configures the JPA
 * process of this component. <br />
 * <br />
 * The file weatherloader.properties explains the configuration of the JPA in more detail. It indicates the used
 * persistence unit and the Java classes for the JPA entities concerning the {@link StationData}. Notice that the
 * database tables must be created before the {@link WeatherService} has started for the first time, otherwise an
 * exception will occur. If there are changes in the scheme, one has to change also the JPA entities in the package
 * dataloader.entity or the properties in the file weatherloader.properties regarding the classes for
 * {@link StationData}.
 * 
 * @author Andreas Rehfeldt
 * @author Marcus
 * @version 1.0 (01.07.2012)
 */
@Lock(LockType.READ)
@Local
@Singleton(name = "de.pgalise.simulation.weather.dataloader.WeatherLoader", mappedName = "de.pgalise.simulation.weather.dataloader.DatabaseWeatherLoader")
public class DatabaseWeatherLoader implements WeatherLoader {

	private EntityManager em;

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
	private Class<StationData> stationDataClass = null;

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
			em = entityManager;
	}

	public EntityManager getEntityManager() {
			return em;
	}

	@Override
	public boolean checkStationDataForDay(long timestamp) {
		List<StationData> weatherList;
		StationData lastday, nextday;
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
		for (StationData stationData : weatherList) {
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
	public ServiceWeather loadCurrentServiceWeatherData(long timestamp, City city) throws NoWeatherDataFoundException {

		// Get the data
		ServiceDataCurrent data;
		TypedQuery<ServiceDataCurrent> query = this.em.createNamedQuery("ServiceDataCurrent.findByDate",
					ServiceDataCurrent.class);
		query.setParameter("date", new Date(timestamp));
		query.setParameter("city", city);
		data = query.getSingleResult();
		

		// Copy informations to the weather object
		ServiceWeather weather = new ServiceWeather(
			data.getMeasureDate().getTime(),
			data.getCity(), 
			data.getRelativHumidity(), 
			data.getTemperature(),
			data.getTemperature(),
			data.getWindDirection(),
			data.getWindVelocity()
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
	 * @throws NoWeatherDataFoundException
	 *             There is no weather data for the given timestamp.
	 * @return ServiceDataForecast
	 */
	@Override
	public ServiceWeather loadForecastServiceWeatherData(long timestamp, City city) throws NoWeatherDataFoundException {

		// Get the data
		ServiceDataForecast data;
		TypedQuery<ServiceDataForecast> query = this.em.createNamedQuery("ServiceDataForecast.findByDate",
				ServiceDataForecast.class);
		if(query == null) {
			return null;
		}
		query.setParameter("date", new Date(timestamp));
		query.setParameter("city", city);
		data = query.getSingleResult();

		// Copy informations to the weather object
		ServiceWeather weather = new ServiceWeather(data.getMeasureDate().getTime(), data.getCity(), data.getRelativHumidity(), data.getTemperatureLow(), data.getTemperatureHigh(), data.getWindDirection(), data.getWindVelocity());
		return weather;
	}

	/**
	 * Returns the weather data of the weather station. They are sorted ascending.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return WeatherMap with weather data
	 * @throws NoWeatherDataFoundException
	 *             There is no weather data for the given date.
	 */
	@Override
	public WeatherMap loadStationData(long timestamp) throws NoWeatherDataFoundException {
		// Get list
		List<StationData> weatherList = this.getStationData(timestamp);

		// Add values of the previous day
		weatherList.add(this.loadLastStationDataForDate(DateConverter.getPreviousDay(timestamp)));

		// Add values of the next day
		weatherList.add(this.loadFirstStationDataForDate(DateConverter.getNextDay(timestamp)));

		// Sort list
		Collections.sort(weatherList);

		// Interpolate
		return this.createWeatherMap(weatherList, Weather.INTERPOLATE_INTERVAL);
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
			this.stationDataClass = (Class<StationData>) Class.forName(this.prop.getProperty(data));
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
	private WeatherMap createWeatherMap(List<StationData> list, int interval) {
		WeatherMap map = new StationDataMap();

		// No interpolate! --> Error
		if (list.isEmpty()) {
			return map;
		}

		// Add missing values
		Weather weather = null;
		for (int i = 1; i < list.size(); i++) {
			StationData min = list.get(i - 1);
			StationData max = list.get(i);

			// Set times and delete seconds and milliseconds
			long minTime = min.getDate().getTime() + min.getTime().getTime() + DateConverter.HOUR;
			minTime -= minTime % DateConverter.MINUTE;
			long actTime = minTime + interval;
			long maxTime = max.getDate().getTime() + max.getTime().getTime() + DateConverter.HOUR;
			maxTime -= maxTime % DateConverter.MINUTE;

			// Add min to map
			weather = new Weather(minTime, min.getAirPressure(), min.getLightIntensity(), min.getPerceivedTemperature(), min.getPrecipitationAmount(), min.getRadiation(), min.getRelativHumidity(), min.getTemperature(), min.getWindDirection(), min.getWindVelocity());
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
				weather.setAirPressure(airPressure);

				int lightIntensity = (int) this.interpolate(minTime, min.getLightIntensity(), maxTime,
						max.getLightIntensity(), actTime);
				weather.setLightIntensity(lightIntensity);

				float perceivedTemperature = (float) this.interpolate(minTime, min.getPerceivedTemperature(), maxTime,
						max.getPerceivedTemperature(), actTime);
				weather.setPerceivedTemperature(perceivedTemperature);

				float temperature = (float) this.interpolate(minTime, min.getTemperature(), maxTime,
						max.getTemperature(), actTime);
				weather.setTemperature(temperature);

				float precipitationAmount = (float) this.interpolate(minTime, min.getPrecipitationAmount(), maxTime,
						max.getPrecipitationAmount(), actTime);
				weather.setPrecipitationAmount(precipitationAmount);

				int radiation = (int) this.interpolate(minTime, min.getRadiation(), maxTime, max.getRadiation(),
						actTime);
				weather.setRadiation(radiation);

				float relativHumidity = (float) this.interpolate(minTime, min.getRelativHumidity(), maxTime,
						max.getRelativHumidity(), actTime);
				weather.setRelativHumidity(relativHumidity);

				int windDirection = (int) this.interpolate(minTime, min.getWindDirection(), maxTime,
						max.getWindDirection(), actTime);
				weather.setWindDirection(windDirection);

				float windVelocity = (float) this.interpolate(minTime, min.getWindVelocity(), maxTime,
						max.getWindVelocity(), actTime);
				weather.setWindVelocity(windVelocity);
				weather = new Weather(actTime, airPressure, lightIntensity, perceivedTemperature, precipitationAmount, radiation, relativHumidity, temperature, windDirection, windVelocity);

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
	 * @return List with the station data
	 * @throws NoWeatherDataFoundException
	 *             There is no weather data for the given date.
	 */
	private List<StationData> getStationData(long timestamp) throws NoWeatherDataFoundException {
		// Check station data
		if ((this.stationDataClass == null) || !(this.stationDataClass.getSuperclass().equals(StationData.class))) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("stationDataClass"));
		}

		// Get information for that day
		List<StationData> weatherList;
			TypedQuery<StationData> query = this.em.createNamedQuery(String.format("%s.findByDate", stationDataClass.getSimpleName()),
					this.stationDataClass);
		query.setParameter("date", new Date(timestamp));
		weatherList = query.getResultList();

		if ((weatherList == null) || weatherList.isEmpty()) {
			throw new NoWeatherDataFoundException(timestamp);
		}

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
	private StationData loadFirstStationDataForDate(long timestamp) {
		// Check station data
		if ((this.stationDataClass == null) || !(this.stationDataClass.getSuperclass().equals(StationData.class))) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("stationDataClass"));
		}

		// Get first result of that day
		StationData data;
		try {
			TypedQuery<StationData> query = this.em.createNamedQuery(this.stationDataClassName
					+ ".findFirstEntryByDate", this.stationDataClass);
			query.setParameter("date", new Date(timestamp));
			query.setMaxResults(1);
			data = query.getSingleResult();
		} catch (Exception e) {
			throw new NoWeatherDataFoundException(timestamp);
		}

		return data;
	}

	/**
	 * Returns the last station data for the given timestamp
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return Last station data instance
	 */
	private StationData loadLastStationDataForDate(long timestamp) {

		// Check station data
		if ((this.stationDataClass == null) || !(this.stationDataClass.getSuperclass().equals(StationData.class))) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("stationDataClass"));
		}

		// Get last result of that day
		StationData data;
		TypedQuery<StationData> query = this.em.createNamedQuery(
				this.stationDataClassName + ".findLastEntryByDate", this.stationDataClass);
		query.setParameter("date", new Date(timestamp));
		query.setMaxResults(1);
		data = query.getSingleResult();
		return data;
	}
}
