/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.weather.entity.ExtendedServiceDataForecast;
import de.pgalise.simulation.weather.entity.WeatherCondition;

/**
 *
 * @param <C> 
 * @author richter
 */
public interface ExtendedServiceDataForecastCompleter {
	void complete(ExtendedServiceDataForecast serviceDataForecast);
}
