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
 
package de.pgalise.simulation.shared.controller;

import java.io.Serializable;
import java.util.List;

import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
/**
 * The start parameters will be send to every controller on start.
 * It contains all information needed in the start state.
 * @author Timo
 * @author mustafa
 */
public class StartParameter implements Serializable {
	private static final long serialVersionUID = -292240397372883199L;
	private boolean aggregatedWeatherDataEnabled;
	private List<WeatherEventHelper> weatherEventHelperList;
	
	/**
	 * Default
	 */
	public StartParameter() {}
	
	/**
	 * Constructor
	 * @param city
	 * @param aggregatedWeatherDataEnabled
	 * @param weatherEventHelperList
	 * @param busRouteList
	 */
	public StartParameter(
			boolean aggregatedWeatherDataEnabled,
			List<WeatherEventHelper> weatherEventHelperList) {
		super();
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
		this.weatherEventHelperList = weatherEventHelperList;
	}

	public List<WeatherEventHelper> getWeatherEventHelperList() {
		return weatherEventHelperList;
	}

	public void setWeatherEventHelperList(
			List<WeatherEventHelper> weatherEventHelperList) {
		this.weatherEventHelperList = weatherEventHelperList;
	}

	public boolean isAggregatedWeatherDataEnabled() {
		return aggregatedWeatherDataEnabled;
	}

	public void setAggregatedWeatherDataEnabled(boolean aggregatedWeatherDataEnabled) {
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
	}
}
