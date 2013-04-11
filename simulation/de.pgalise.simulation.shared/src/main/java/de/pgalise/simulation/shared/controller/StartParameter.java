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

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import de.pgalise.simulation.shared.traffic.BusRoute;
/**
 * The start parameters will be send to every controller on start.
 * It contains all information needed in the start state.
 * @author Timo
 * @author mustafa
 */
public class StartParameter implements Serializable {
	private static final long serialVersionUID = -292240397372883199L;
	private City city;
	private boolean aggregatedWeatherDataEnabled;
	private List<WeatherEventHelper> weatherEventHelperList;
	private List<BusRoute> busRouteList;
	
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
	public StartParameter(City city,
			boolean aggregatedWeatherDataEnabled,
			List<WeatherEventHelper> weatherEventHelperList,
			List<BusRoute> busRouteList) {
		super();
		this.city = city;
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
		this.weatherEventHelperList = weatherEventHelperList;
		this.busRouteList = busRouteList;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
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

	public List<BusRoute> getBusRouteList() {
		return busRouteList;
	}

	public void setBusRouteList(List<BusRoute> busRouteList) {
		this.busRouteList = busRouteList;
	}
}
