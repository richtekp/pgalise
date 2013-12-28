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
public interface ServiceDataCurrent extends ServiceData {

	Measure<Float, Temperature> getTemperature() ;
}
