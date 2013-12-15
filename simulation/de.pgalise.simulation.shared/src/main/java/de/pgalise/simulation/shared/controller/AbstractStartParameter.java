/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.controller;

import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import java.util.List;


public class AbstractStartParameter implements StartParameter {
	private static final long serialVersionUID = 1L;
	private List<WeatherEvent> weatherEventHelperList;
	private boolean aggregatedWeatherDataEnabled;

	public AbstractStartParameter() {
	}

	public AbstractStartParameter(List<WeatherEvent> weatherEventHelperList,
		boolean aggregatedWeatherDataEnabled) {
		this.weatherEventHelperList = weatherEventHelperList;
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
	}

	@Override
	public void setWeatherEventList(
		List<WeatherEvent> weatherEventHelperList) {
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
	public List<WeatherEvent> getWeatherEventList() {
		return weatherEventHelperList;
	}
	
}
