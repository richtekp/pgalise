/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import java.util.List;


public class InfrastructureStartParameter extends StartParameter {
	private City city;

	public InfrastructureStartParameter() {
	}

	public InfrastructureStartParameter(City city,
		boolean aggregatedWeatherDataEnabled,
		List<WeatherEventHelper> weatherEventHelperList) {
		super(aggregatedWeatherDataEnabled,
			weatherEventHelperList);
		this.city = city;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
	
}
