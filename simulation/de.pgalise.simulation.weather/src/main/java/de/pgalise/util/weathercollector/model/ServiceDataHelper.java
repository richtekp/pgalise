/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.traffic.City;
import de.pgalise.simulation.weather.model.TimeSensitive;
import de.pgalise.simulation.weather.model.WeatherCondition;
import java.util.Set;

/**
 *
 * @param <S> 
 * @param <T> 
 * @param <C> 
 * @author richter
 */
public interface ServiceDataHelper<S extends ExtendedServiceDataCurrent<C>, T extends ExtendedServiceDataForecast<C>, C extends WeatherCondition> extends TimeSensitive, ServiceDataHelperCompleter<S,T,C> {
	
	public String getApicity() ;

	public City getCity() ;

	public S getCurrentCondition() ;

	public Set<T> getForecastConditions() ;

	public String getSource();

}
