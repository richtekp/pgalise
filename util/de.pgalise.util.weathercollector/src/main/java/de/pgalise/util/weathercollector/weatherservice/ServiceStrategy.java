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
import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;

/**
 * Interface for weather service strategies. Uses the strategy pattern
 * 
 * @param <T> the type of the {@link ServiceDataHelper} (in subclasses which use JPA, a persistent class can be enforced)
 * @param <C> 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
public interface ServiceStrategy<T extends ServiceDataHelper<?,?,C>,C extends WeatherCondition> {

	/**
	 * Returns the current weather informations to the given city
	 * 
	 * @param city
	 *            City
	 * @param databaseManager 
	 * @return ServiceData instance
	 * @throws ReadServiceDataException
	 *             Data can not be read by strategy
	 */
	public T getWeather(City city, DatabaseManager<C> databaseManager) throws ReadServiceDataException;
}