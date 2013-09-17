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
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;

/**
 * The main interaction point of the component Weather is the interface {@link WeatherController} that represents the
 * weather environment controller of the simulation. Its public methods can be called by other components. Therefore,
 * the controller is implemented as an EJB. For one thing the controller sends all requests to the interface
 * {@link WeatherService} and the responses back to the other components, for another thing it inherits from the generic
 * {@link Controller} interface and implements its state transitions. So this controller acts as intermediary between
 * components of the simulation and the {@link WeatherService}.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 24, 2012)
 */
public interface WeatherController extends Controller {

	/**
	 * Checks if the weather data can be loaded for the given date.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @throws IllegalArgumentException  
	 */
	public void checkDate(long timestamp) throws IllegalArgumentException;

	/**
	 * Returns the position of the reference point
	 * 
	 * @return 
	 */
	public Coordinate getReferencePosition();

	/**
	 * Get the reference value for the given timestamp and position. If the value can be cached the method looks into
	 * the cached parameters. The weather values are stored with a specific time interval (see
	 * {@link Weather#INTERPOLATE_INTERVAL}). If you want to get a value between this interval, you will get the value
	 * to the next reference timestamp.
	 * 
	 * @param key
	 *            WeatherParameterEnum
	 * @param timestamp
	 *            Timestamp
	 * @param position
	 *            Position of the sensor
	 * @return Number
	 */
	public Number getValue(WeatherParameterEnum key, long timestamp, Coordinate position);
}
