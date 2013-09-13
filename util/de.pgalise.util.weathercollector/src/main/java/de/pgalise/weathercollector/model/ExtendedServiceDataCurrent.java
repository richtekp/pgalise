/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.weathercollector.model;

import de.pgalise.simulation.weather.model.BaseServiceData;
import java.sql.Time;

/**
 *
 * @author richter
 */
public interface  ExtendedServiceDataCurrent extends BaseServiceData {
	
	Time getSunrise();

	Time getSunset();

	Float getVisibility();
}
