/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.weathercollector.model;

import de.pgalise.simulation.weather.model.MutableServiceDataForecast;
import java.sql.Time;

/**
 *
 * @author richter
 */
public interface MutableExtendedServiceDataCurrent extends ExtendedServiceDataCurrent, MutableServiceDataForecast {
	
	void setSunrise(Time sunrise);

	void setSunset(Time sunset);

	void setVisibility(Float visibility);
}
