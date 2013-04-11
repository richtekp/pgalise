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
 
package de.pgalise.simulation.weather.parameter;

import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.service.WeatherService;

/**
 * At the time of the {@link WeatherService} creation, the class loads the {@link WeatherParameter} by the help of the
 * enum {@link WeatherParameter} automatically. There are eleven {@link WeatherParameter} available. Some of them are
 * calculated by other weather parameters. These parameters will be cached by the {@link WeatherService} for another
 * request to reduce the CPU processing. An example is the parameter {@link WindStrength}.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (03.07.2012)
 */
public interface WeatherParameter {

	/**
	 * Returns the value for the given time
	 * 
	 * @param time
	 *            Timestamp
	 * @return Value as a number
	 * @throws NoWeatherDataFoundException
	 *             There is no value for the given time.
	 */
	public <T extends Number> T getValue(long time) throws NoWeatherDataFoundException;

	/**
	 * Set the WeatherService
	 * 
	 * @param weatherService
	 *            WeatherService
	 */
	public void setWeatherService(WeatherService weatherService);

}
