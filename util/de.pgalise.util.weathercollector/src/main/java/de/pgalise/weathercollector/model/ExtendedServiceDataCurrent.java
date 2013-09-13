/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.weathercollector.model;

import de.pgalise.simulation.weather.model.ServiceDataCurrent;
import java.sql.Time;

/**
 *
 * @author richter
 */
public interface  ExtendedServiceDataCurrent extends ServiceDataCurrent, ExtendedServiceDataCurrentCompleter {
	
	Time getSunrise();

	Time getSunset();

	Float getVisibility();
}
