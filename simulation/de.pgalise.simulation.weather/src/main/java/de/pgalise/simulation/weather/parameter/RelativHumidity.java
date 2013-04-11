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
 
/**
 * 
 */
package de.pgalise.simulation.weather.parameter;

import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.service.WeatherService;

/**
 * Weather parameter: relativ humidity
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (03.07.2012)
 */
public final class RelativHumidity extends WeatherParameterBase {

	/**
	 * Constructor
	 * 
	 * @param base
	 *            WeatherService
	 */
	public RelativHumidity(WeatherService base) {
		super(base);
	}

	/*
	 * (non-Javadoc)
	 * @see de.pgalise.simulation.weather.parameter.WeatherParameter#getValue(java.sql.Timestamp)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Number> T getValue(long time) throws NoWeatherDataFoundException {
		Float value = this.getWeather(time).getRelativHumidity();
		return (T) value;
	}

}
