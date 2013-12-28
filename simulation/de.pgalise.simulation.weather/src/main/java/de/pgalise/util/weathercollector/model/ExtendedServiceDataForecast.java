/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.weather.model.ServiceDataForecast;
import de.pgalise.simulation.weather.model.WeatherCondition;

/**
 *
 * @param <C> 
 * @author richter
 */
public interface ExtendedServiceDataForecast extends ServiceDataForecast, ExtendedServiceDataForecastCompleter {
	
}
