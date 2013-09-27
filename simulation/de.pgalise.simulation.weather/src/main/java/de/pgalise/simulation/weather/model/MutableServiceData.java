/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.traffic.City;

/**
 *
 * @author richter
 */
public interface MutableServiceData<C extends WeatherCondition> extends ServiceData<C>, MutableBaseWeatherInformation {
	void setCondition(C condition);
	
	void setCity(City city);
}
