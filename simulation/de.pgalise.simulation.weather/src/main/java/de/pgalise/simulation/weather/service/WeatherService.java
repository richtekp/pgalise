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
 
package de.pgalise.simulation.weather.service;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.List;

import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.modifier.WeatherStrategy;
import de.pgalise.simulation.weather.parameter.WeatherParameter;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.util.WeatherStrategyHelper;
import javax.measure.quantity.Temperature;
import javax.measure.unit.Unit;

/**
 * The {@link WeatherService} coordinates the {@link WeatherParameter} requests and provides access with the help of the
 * {@link WeatherLoader} to the needed weather information. Due to the fact that the component should not allocate too
 * much space during runtime, it provides only a specific amount of modified weather information. If there is a request
 * for weather parameter values, which cannot be calculated from the provided data, the {@link WeatherService} try to
 * get the needed data automatically.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (09.07.2012)
 */
public interface WeatherService {

	/**
	 * Clear all stored data to use new data
	 */
	public void _clearValues();

	/**
	 * Get new weather informations for the given timestamp from the database
	 * which have to be present in the database. In case they're not present 
	 * throws a {@link NoWeatherDataFoundException}.
	 * 
	 * @param startTimestamp
	 *            Timestamp (from 2003-07-07 to today)
	 * @param endTimestamp
	 *            Timestamp (from 2003-07-07 to today)
	 * @param takeNormalData
	 *            Option to take normal data
	 * @param strategyList
	 *            List with weather strategies to modify the data.
	 * @throws NoWeatherDataFoundException
	 *             There is no data for the given date in the database
	 */
	public void addNewWeather(long startTimestamp, long endTimestamp, boolean takeNormalData,
			List<WeatherStrategyHelper> strategyList) throws NoWeatherDataFoundException;

	/**
	 * Get new weather informations for the next day of the simulation date from 
	 * the database which have to be present in the database. In case they're not 
	 * present throws a {@link NoWeatherDataFoundException}.
	 * 
	 * @throws NoWeatherDataFoundException
	 *             There is no data for the given date in the database
	 * @throws IllegalStateException if there's been no weather added for any day 
	 * (which dertermines the next day used by this method)
	 */
	public void addNewNextDayWeather() throws NoWeatherDataFoundException;

	/**
	 * Checks if the weather data can be loaded for the given date.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return 
	 */
	public boolean checkDate(long timestamp);

	/**
	 * Modifies the current weather data with the given weather strategy
	 * 
	 * @param strategy
	 *            Weather strategy to modify the current weather data
	 * @throws NoWeatherDataFoundException
	 *             There is no data for the given date in the database
	 */
	public void deployStrategy(WeatherStrategy strategy) throws NoWeatherDataFoundException;

	/**
	 * Returns the current loaded timestamp
	 * 
	 * @return loadedTimestamp
	 */
	public long getLoadedTimestamp();

	/**
	 * Returns the position of the reference values
	 * 
	 * @return referencePosition
	 */
	public Coordinate getReferencePosition();

	/**
	 * Returns the reference values
	 * 
	 * @return referenceValues
	 */
	public WeatherMap getReferenceValues();

	/**
	 * Get the reference value for the given timestamp. If the value can be cached the method looks into the cached
	 * parameters. The weather values are stored with a specific time interval (see {@link Weather#INTERPOLATE_INTERVAL}
	 * ). If you want to get a value between this interval, you will get the value to the next reference timestamp.
	 * 
	 * @param <T>
	 * @param key
	 *            WeatherParameterEnum
	 * @param time
	 *            Timestamp
	 * @return Return value of the WeatherParameterEnum
	 * @throws IllegalArgumentException
	 *             There is no value linked to the timestamp (e.g. perhaps wrong date)
	 */
	public <T extends Number> T getValue(WeatherParameterEnum key, long time) throws IllegalArgumentException;

	/**
	 * Get the reference value for the given timestamp and position. If the value can be cached the method looks into
	 * the cached parameters. The weather values are stored with a specific time interval (see
	 * {@link Weather#INTERPOLATE_INTERVAL}). If you want to get a value between this interval, you will get the value
	 * to the next reference timestamp.
	 * 
	 * @param <T>
	 * @param key
	 *            WeatherParameterEnum
	 * @param time
	 *            Timestamp
	 * @param position
	 *            Position of the sensor
	 * @return Return value of the WeatherParameterEnum
	 * @throws IllegalArgumentException
	 *             There is no value linked to the timestamp or wrong position (e.g. perhaps wrong date)
	 */
	public <T extends Number> T getValue(WeatherParameterEnum key, long time, Coordinate position)
			throws IllegalArgumentException;

	/**
	 * Clear all stored data to use new data
	 */
	public void initValues();

	/**
	 * Set new City
	 * 
	 * @param city
	 *            City
	 */
	public void setCity(City city);
	
	Unit<Temperature> getTemperatureUnit();
}
