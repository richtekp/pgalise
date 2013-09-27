/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.traffic.City;
import de.pgalise.simulation.weather.model.MutableTimeSensitive;
import de.pgalise.simulation.weather.model.WeatherCondition;
import java.util.Set;

/**
 *
 * @param <S> 
 * @param <T> 
 * @param <C> 
 * @author richter
 */
public interface MutableServiceDataHelper<S extends MutableExtendedServiceDataCurrent<C>, T extends MutableExtendedServiceDataForecast<C>, C extends WeatherCondition> extends ServiceDataHelper<S, T,C>, MutableTimeSensitive {
	

	void setApicity(String apicity) ;

	void setCity(City city) ;

	void setCurrentCondition(S currentCondition) ;
	
	void setForecastConditions(Set<T> forecastConditions) ;

	void setSource(String source) ;
}
