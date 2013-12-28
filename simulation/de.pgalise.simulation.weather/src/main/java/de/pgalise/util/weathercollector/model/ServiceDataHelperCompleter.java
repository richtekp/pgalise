/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.weather.model.WeatherCondition;

/**
 *
 * @param <S> 
 * @param <T> 
 * @param <C> 
 * @author richter
 */
public interface ServiceDataHelperCompleter<S extends ExtendedServiceDataCurrent, T extends ExtendedServiceDataForecast>{
	void complete(ServiceDataHelper<S,T> serviceDataHelper);
}
