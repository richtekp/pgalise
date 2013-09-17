/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.weather.model.MutableServiceDataCurrent;
import de.pgalise.simulation.weather.model.WeatherCondition;
import java.sql.Time;

/**
 *
 * @param <C> 
 * @author richter
 */
public interface MutableExtendedServiceDataCurrent<C extends WeatherCondition> extends ExtendedServiceDataCurrent<C>, MutableServiceDataCurrent<C> {
	
	void setSunrise(Time sunrise);

	void setSunset(Time sunset);

	void setVisibility(Float visibility);
}
