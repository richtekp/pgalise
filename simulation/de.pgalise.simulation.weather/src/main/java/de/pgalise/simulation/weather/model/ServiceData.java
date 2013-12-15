/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.shared.city.City;

/**
 *
 * @author richter
 * @param <C>
 */
public interface ServiceData<C extends WeatherCondition> extends WeatherInformation {
	
	C getCondition();
	
	City getCity();
	
}
