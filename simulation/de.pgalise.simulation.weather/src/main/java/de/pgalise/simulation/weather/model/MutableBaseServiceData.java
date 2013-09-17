/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.shared.city.City;

/**
 *
 * @author richter
 */
public interface MutableBaseServiceData<C extends WeatherCondition> extends BaseServiceData<C>, MutableBaseWeatherInformation {
	void setCondition(C condition);
	
	void setCity(City city);
}
