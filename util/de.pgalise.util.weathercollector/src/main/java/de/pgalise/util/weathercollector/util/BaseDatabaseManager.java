/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.util;

import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.weatherservice.WeatherServiceSaver;
import de.pgalise.util.weathercollector.weatherstation.WeatherStationSaver;

/**
 *
 * @param <T> 
 * @author richter
 */
public interface BaseDatabaseManager<T extends ServiceDataHelper<?,?>> extends DatabaseManager, WeatherServiceSaver<T>, WeatherStationSaver {
	
}
