/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.traffic.AbstractInfrastructureStartParameter;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import java.util.List;
import javax.ejb.Stateless;

@Stateless
public class DefaultInfrastructureStartParameter extends AbstractInfrastructureStartParameter implements InfrastructureStartParameter {
	private static final long serialVersionUID = 1L;

	public DefaultInfrastructureStartParameter() {
	}
	
	public DefaultInfrastructureStartParameter(City city,
		boolean aggregatedWeatherDataEnabled,
		List<WeatherEvent> weatherEventHelperList) {
		super(city,  aggregatedWeatherDataEnabled, weatherEventHelperList);
	}
	
	
}
