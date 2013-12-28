/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.util;

import de.pgalise.simulation.weather.model.WeatherCondition;


/**
 *
 * @param <C> the type of the {@link WeatherCondition} (this can be used to 
 * enforce a persistable type in a subclass)
 * @author richter
 */
public interface DatabaseManager {
	WeatherCondition getCondition(String condition);
}
