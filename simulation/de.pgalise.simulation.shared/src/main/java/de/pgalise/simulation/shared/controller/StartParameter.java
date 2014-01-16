/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.controller;

import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.SimulationInitParameter;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import java.util.LinkedList;
import java.util.List;

public class StartParameter<C extends City> extends SimulationInitParameter {

	private static final long serialVersionUID = 1L;
	private List<WeatherEvent> weatherEvents = new LinkedList<>();
	private boolean aggregatedWeatherDataEnabled;
	private C city;

	public StartParameter() {
	}

	public StartParameter(
		boolean aggregatedWeatherDataEnabled,
		List<WeatherEvent> weatherEvents,
		C city) {
		this.weatherEvents = weatherEvents;
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
		this.city = city;
	}

	public void setCity(C city) {
		this.city = city;
	}

	public C getCity() {
		return city;
	}

	public void setWeatherEvents(
		List<WeatherEvent> weatherEvents) {
		this.weatherEvents = weatherEvents;
	}

	public void setAggregatedWeatherDataEnabled(
		boolean aggregatedWeatherDataEnabled) {
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
	}

	public boolean isAggregatedWeatherDataEnabled() {
		return aggregatedWeatherDataEnabled;
	}

	public List<WeatherEvent> getWeatherEvents() {
		return weatherEvents;
	}

}
