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
public interface MutableServiceDataCurrent extends ServiceDataCurrent, MutableBaseServiceData {
	
	void setTemperature(Measure<Float, Temperature> temperature);
}

