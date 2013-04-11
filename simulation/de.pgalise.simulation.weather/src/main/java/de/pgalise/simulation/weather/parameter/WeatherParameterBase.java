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

import java.sql.Date;
import java.sql.Time;

import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.simulation.weather.util.DateConverter;

/**
 * Abstract super class for weather parameter
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (07.07.2012)
 */
public abstract class WeatherParameterBase implements WeatherParameter {

	/**
	 * WeatherService
	 */
	protected WeatherService weatherService;

	/**
	 * Default constructor
	 */
	public WeatherParameterBase() {

	}

	/**
	 * Constructor
	 * 
	 * @param weatherService
	 *            WeatherService
	 */
	public WeatherParameterBase(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	/**
	 * Set the WeatherService
	 * 
	 * @param base
	 *            WeatherService
	 */
	@Override
	public void setWeatherService(WeatherService base) {
		this.weatherService = base;
	}

	/**
	 * Returns the weather object to the given time. If there is no weather object for that time, than it will return
	 * the next value for the next reference time.
	 * 
	 * @param time
	 *            Timestamp
	 * @return weather object to the given time
	 * @throws NoWeatherDataFoundException
	 *             There is no value for the given time.
	 */
	protected Weather getWeather(long time) throws NoWeatherDataFoundException {
		// Check if it is the right date
		if ((this.weatherService.getLoadedTimestamp() < 1)
				|| !DateConverter.isTheSameDay(this.weatherService.getLoadedTimestamp(), time)) {
			throw new NoWeatherDataFoundException("Wrong date: " + new Date(time) + "! Date for the simulation is:  "
					+ new Date(this.weatherService.getLoadedTimestamp()));
		}

		// Get weather
		Weather weather = this.weatherService.getReferenceValues().get(time);
		if (weather == null) {

			// Calculate next minute
			long newTime = time + (Weather.INTERPOLATE_INTERVAL - (time % Weather.INTERPOLATE_INTERVAL));
			weather = this.weatherService.getReferenceValues().get(newTime);

			// Get weather
			if (weather == null) {

				// Look for the last 5 Minutes
				for (int i = 0; i < 5; i++) {
					newTime -= Weather.INTERPOLATE_INTERVAL;
					weather = this.weatherService.getReferenceValues().get(newTime);
					if (weather != null) {
						break;
					}
				}

				if (weather == null) {
					// Load new Data
					throw new NoWeatherDataFoundException("No weather information are available for "
							+ new Date(newTime) + " - " + new Time(newTime));
				}
			}
		}

		// Returns
		return weather;
	}
}
