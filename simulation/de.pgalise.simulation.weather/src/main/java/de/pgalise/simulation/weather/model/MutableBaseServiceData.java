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
public interface MutableBaseServiceData extends BaseServiceData, MutableBaseWeatherInformation {
	void setCondition(WeatherCondition condition);
	
	void setCity(City city);
}
