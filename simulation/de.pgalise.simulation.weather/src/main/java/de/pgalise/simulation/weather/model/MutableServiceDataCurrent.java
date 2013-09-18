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
public interface MutableServiceDataCurrent<C extends WeatherCondition> extends ServiceDataCurrent<C>, MutableServiceData<C> {
	
	void setTemperature(Measure<Float, Temperature> temperature);
}

