/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.weather.model.ServiceDataCurrent;
import de.pgalise.simulation.weather.model.WeatherCondition;
import java.sql.Time;

/**
 *
 * @param <C> 
 * @author richter
 */
public interface  ExtendedServiceDataCurrent extends ServiceDataCurrent, ExtendedServiceDataCurrentCompleter {
	
	Time getSunrise();

	Time getSunset();

	Float getVisibility();
	
	
}
