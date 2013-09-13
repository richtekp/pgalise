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

import de.pgalise.simulation.weather.service.WeatherService;

/**
 * Weather parameter: wind strength
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (03.07.2012)
 */
public class WindStrength extends WeatherParameterBase {

	/**
	 * Constructor
	 * 
	 * @param base
	 *            WeatherService
	 */
	public WindStrength(WeatherService base) {
		super(base);
	}

	/*
	 * (non-Javadoc)
	 * @see de.pgalise.simulation.weather.parameter.WeatherParameter#getValue(java.sql.Timestamp)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Number> T getValue(long time)   {
		Integer value = this.getBeaufortValue(this.getWeather(time).getWindVelocity());
		return (T) value;
	}

	/**
	 * Returns the beaufort value (http://www.nuernberg.de/internet/umweltanalytik/luft_wetter.html#10)
	 * 
	 * @param windVelocity
	 *            wind velocity
	 * @return beaufort value
	 */
	private int getBeaufortValue(float windVelocity) {
		if ((windVelocity >= 0) && (windVelocity < 0.3)) {
			return 0;
		} else if ((windVelocity >= 0.3) && (windVelocity < 1.6)) {
			return 1;
		} else if ((windVelocity >= 1.6) && (windVelocity < 3.4)) {
			return 2;
		} else if ((windVelocity >= 3.4) && (windVelocity < 5.5)) {
			return 3;
		} else if ((windVelocity >= 5.5) && (windVelocity < 8.0)) {
			return 4;
		} else if ((windVelocity >= 8.0) && (windVelocity < 10.8)) {
			return 5;
		} else if ((windVelocity >= 10.8) && (windVelocity < 13.9)) {
			return 6;
		} else if ((windVelocity >= 13.9) && (windVelocity < 17.2)) {
			return 7;
		} else if ((windVelocity >= 17.2) && (windVelocity < 20.8)) {
			return 8;
		} else if ((windVelocity >= 20.8) && (windVelocity < 24.5)) {
			return 9;
		} else if ((windVelocity >= 24.5) && (windVelocity < 28.5)) {
			return 10;
		} else if ((windVelocity >= 28.5) && (windVelocity < 32.7)) {
			return 11;
		} else if (windVelocity >= 32.7) {
			return 12;
		} else {
			// Error!
			return -1;
		}
	}

}
