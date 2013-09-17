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

import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.model.WeatherCondition;
import de.pgalise.util.weathercollector.model.ExtendedServiceDataCurrent;
import de.pgalise.util.weathercollector.model.ExtendedServiceDataForecast;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.EntityDatabaseManager;

/**
 * Helper for weather service strategies
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Apr 07, 2012)
 */
public class ServiceStrategyLib {

	/**
	 * Completes the ServiceData objects to one better object
	 * 
	 * @param <A> enforces type security in the completion
	 * @param <B> enforces type security in the completion
	 * @param <C> 
	 * @param <T> 
	 * @param weather1
	 *            Best ServiceData
	 * @param weather2
	 *            New ServiceData
	 * @return Better ServiceData
	 */
	public static <A extends ExtendedServiceDataCurrent<C>, B extends ExtendedServiceDataForecast<C>, C extends WeatherCondition, T extends ServiceDataHelper<A,B,C>> T completeWeather(T weather1, T weather2) {
		if (weather2 == null) {
			return weather1;
		}

		// Get current ServiceData
		T bestWeather;
		T tempWeather;
		if (weather1.getMeasureTime().getTime() > weather2.getMeasureTime().getTime()) {
			bestWeather = weather1;
			tempWeather = weather2;
		} else {
			bestWeather = weather2;
			tempWeather = weather1;
		}

		// Complete ServiceData
		bestWeather.complete(tempWeather);

		// Return best ServiceData
		return bestWeather;
	}

	/**
	 * Returns the condition code from the database
	 * 
	 * @param condition
	 *            Condition
	 * @param databaseManager 
	 * @return Condition code
	 */
	public static int getConditionCode(String condition, EntityDatabaseManager databaseManager) {
		if ((condition == null) || condition.isEmpty()) {
			return DefaultWeatherCondition.UNKNOWN_CONDITION_CODE;
		}

		// Prepare
		String condition0 = condition.toLowerCase();

		// Get condition
		DefaultWeatherCondition result = databaseManager.getCondition(condition0);

		if (result != null) {
			return result.getCode();
		} else {
			return DefaultWeatherCondition.UNKNOWN_CONDITION_CODE;
		}
	}

	private ServiceStrategyLib() {
	}
}
