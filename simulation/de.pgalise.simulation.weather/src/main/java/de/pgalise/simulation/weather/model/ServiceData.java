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
public interface ServiceData extends WeatherInformation {
	
	WeatherCondition getCondition();
	
	City getCity();
	
}
