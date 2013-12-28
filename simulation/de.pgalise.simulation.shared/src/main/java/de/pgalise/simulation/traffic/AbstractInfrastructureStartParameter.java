/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.AbstractStartParameter;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import java.util.List;

public class AbstractInfrastructureStartParameter extends AbstractStartParameter
	implements InfrastructureStartParameter {

	private static final long serialVersionUID = 1L;
	private City city = new City();

	public AbstractInfrastructureStartParameter() {
	}

	public AbstractInfrastructureStartParameter(City city,
		boolean aggregatedWeatherDataEnabled,
		List<WeatherEvent> weatherEventHelperList) {
		super(weatherEventHelperList,
			aggregatedWeatherDataEnabled);
		this.city = city;
	}

	@Override
	public City getCity() {
		return city;
	}

	@Override
	public void setCity(City city) {
		this.city = city;
	}

}
