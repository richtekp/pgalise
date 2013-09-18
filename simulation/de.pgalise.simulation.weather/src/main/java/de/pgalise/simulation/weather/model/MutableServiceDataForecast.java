/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import javax.measure.Measure;
import javax.measure.quantity.Temperature;

/**
 *
 * @param <C> 
 * @author richter
 */
public interface MutableServiceDataForecast<C extends WeatherCondition> extends ServiceDataForecast<C>, MutableServiceData<C> {
	
	void setTemperatureHigh(Measure<Float, Temperature> temperatureHigh);
	
	void setTemperatureLow(Measure<Float, Temperature> temperatureLow);
}
