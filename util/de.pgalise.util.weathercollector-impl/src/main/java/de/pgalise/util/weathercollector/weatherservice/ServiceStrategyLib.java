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

import de.pgalise.simulation.weather.internal.dataloader.entity.DefaultCondition;
import de.pgalise.simulation.weather.model.Condition;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import de.pgalise.util.weathercollector.util.NonJTADatabaseManager;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;

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
	 * @param weather1
	 *            Best ServiceData
	 * @param weather2
	 *            New ServiceData
	 * @return Better ServiceData
	 */
	public static ServiceDataHelper completeWeather(ServiceDataHelper weather1, ServiceDataHelper weather2) {
		if (weather2 == null) {
			return weather1;
		}

		// Get current ServiceData
		ServiceDataHelper bestWeather = null;
		ServiceDataHelper tempWeather = null;
		if (weather1.getMeasureTimestamp().getTime() > weather2.getMeasureTimestamp().getTime()) {
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
	 * @return Condition code
	 */
	public static int getConditionCode(String condition, DatabaseManager databaseManager) {
		if ((condition == null) || condition.equals("")) {
			return Condition.UNKNOWN_CONDITION_CODE;
		}

		// Prepare
		condition = condition.toLowerCase();

		// Get condition
		Condition result = databaseManager.getCondition(condition);

		if (result != null) {
			return result.getCode();
		} else {
			return Condition.UNKNOWN_CONDITION_CODE;
		}
	}

	private ServiceStrategyLib() {
	}
}
