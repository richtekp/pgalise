/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.controller;

import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import java.util.List;


public class AbstractStartParameter implements StartParameter {
	private List<WeatherEventHelper> weatherEventHelperList;
	private boolean aggregatedWeatherDataEnabled;

	public AbstractStartParameter() {
	}

	public AbstractStartParameter(List<WeatherEventHelper> weatherEventHelperList,
		boolean aggregatedWeatherDataEnabled) {
		this.weatherEventHelperList = weatherEventHelperList;
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
	}

	public void setWeatherEventHelperList(
		List<WeatherEventHelper> weatherEventHelperList) {
		this.weatherEventHelperList = weatherEventHelperList;
	}

	public void setAggregatedWeatherDataEnabled(
		boolean aggregatedWeatherDataEnabled) {
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
	}

	@Override
	public boolean isAggregatedWeatherDataEnabled() {
		return aggregatedWeatherDataEnabled;
	}

	@Override
	public List<WeatherEventHelper> getWeatherEventHelperList() {
		return weatherEventHelperList;
	}
	
}
