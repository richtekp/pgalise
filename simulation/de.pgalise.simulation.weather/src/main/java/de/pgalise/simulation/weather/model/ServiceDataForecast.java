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
public interface ServiceDataForecast<C extends WeatherCondition> extends ServiceData<C> {
	
	Measure<Float, Temperature> getTemperatureHigh();
	
	Measure<Float, Temperature> getTemperatureLow();
}
