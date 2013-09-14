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
 * @author richter
 */
public abstract class BaseDatabaseManager<T extends ServiceDataHelper<?,?>> implements DatabaseManager, WeatherServiceSaver<T>, WeatherStationSaver {
	
}
