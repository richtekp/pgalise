/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.weather.model.WeatherCondition;

/**
 *
 * @param <C> 
 * @author richter
 */
public interface ExtendedServiceDataForecastCompleter<C extends WeatherCondition> {
	void complete(ExtendedServiceDataForecast<C> serviceDataForecast);
}
