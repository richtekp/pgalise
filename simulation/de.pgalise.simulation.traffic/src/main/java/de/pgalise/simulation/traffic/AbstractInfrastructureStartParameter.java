/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.AbstractStartParameter;
import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import java.util.List;


public class AbstractInfrastructureStartParameter extends AbstractStartParameter implements InfrastructureStartParameter {
private City city;

	public AbstractInfrastructureStartParameter() {
	}

	public AbstractInfrastructureStartParameter(City city,
		boolean aggregatedWeatherDataEnabled,
		List<WeatherEventHelper> weatherEventHelperList) {
		super(weatherEventHelperList, aggregatedWeatherDataEnabled);
		this.city = city;
	}

	@Override
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
	
}
