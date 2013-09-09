/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import javax.measure.Measure;
import javax.measure.quantity.Temperature;

/**
 *
 * @author richter
 */
public interface ServiceDataForecast extends BaseServiceData {
	
	Measure<Float, Temperature> getTemperatureHigh();
	
	Measure<Float, Temperature> getTemperatureLow();
}