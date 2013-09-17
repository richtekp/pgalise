/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.util;

import de.pgalise.simulation.weather.model.WeatherCondition;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.weatherservice.WeatherServiceSaver;
import de.pgalise.util.weathercollector.weatherstation.WeatherStationSaver;

/**
 *
 * @param <T> 
 * @param <C> 
 * @author richter
 */
public interface BaseDatabaseManager<T extends ServiceDataHelper<?,?,C>, C extends WeatherCondition> extends DatabaseManager<C>, WeatherServiceSaver<T,C>, WeatherStationSaver {
	
}
