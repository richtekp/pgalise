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
 
package de.pgalise.simulation.weather.dataloader;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;

/**
 * The aim and the purpose of the interface {@link WeatherLoader} is to get weather information from a source during
 * runtime. It is implemented as an EJB. The tasks of this interface are to load weather station values and to load
 * weather service values as well as reference cities.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0
 */
public interface WeatherLoader {

	/**
	 * Checks if the weather informations for that day are correct
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return true, if the data is correct
	 */
	public boolean checkStationDataForDay(long timestamp);

	/**
	 * Returns the ID of the reference city for the given city
	 * 
	 * @param city
	 *            City
	 * @return ID of the reference city
	 */
	public int getReferenceCity(City city);

	/**
	 * Returns the current weather informations form the weather services for the given city and timestamp
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @param city
	 *            ID to the city
	 * @throws NoWeatherDataFoundException
	 *             There is no service weather data for the given timestamp.
	 * @return ServiceWeather object
	 */
	public ServiceWeather loadCurrentServiceWeatherData(long timestamp, int city) throws NoWeatherDataFoundException;

	/**
	 * Returns the forecast weather informations form the weather services for the given city and timestamp
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @param city
	 *            ID to the city
	 * @throws NoWeatherDataFoundException
	 *             There is no service weather data for the given timestamp.
	 * @return ServiceWeather object
	 */
	public ServiceWeather loadForecastServiceWeatherData(long timestamp, int city) throws NoWeatherDataFoundException;

	/**
	 * Returns the weather data of the weather station. They are sorted ascending.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return WeatherMap with weather data
	 * @throws NoWeatherDataFoundException
	 *             There is no weather data for the given date.
	 */
	public WeatherMap loadStationData(long timestamp) throws NoWeatherDataFoundException;

	/**
	 * Set the load option for station data
	 * 
	 * @param takeNormalData
	 *            Option to take normal data
	 */
	public void setLoadOption(boolean takeNormalData);

}
