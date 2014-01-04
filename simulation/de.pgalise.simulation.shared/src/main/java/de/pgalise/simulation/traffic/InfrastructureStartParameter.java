/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import java.util.List;

public class InfrastructureStartParameter extends StartParameter {

	private static final long serialVersionUID = 1L;
	private City city = new City();

	public InfrastructureStartParameter() {
	}

	public InfrastructureStartParameter(City city,
		boolean aggregatedWeatherDataEnabled,
		List<WeatherEvent> weatherEventHelperList) {
		super(
			aggregatedWeatherDataEnabled,weatherEventHelperList);
		this.city = city;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
}
