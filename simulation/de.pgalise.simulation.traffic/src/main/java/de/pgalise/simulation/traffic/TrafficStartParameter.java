/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import java.util.List;

public class TrafficStartParameter extends StartParameter<TrafficCity> {

	private static final long serialVersionUID = 1L;

	public TrafficStartParameter() {
	}

	public TrafficStartParameter(TrafficCity city,
		boolean aggregatedWeatherDataEnabled,
		List<WeatherEvent> weatherEventHelperList) {
		super(
			aggregatedWeatherDataEnabled,
			weatherEventHelperList,
			city);
	}
}
