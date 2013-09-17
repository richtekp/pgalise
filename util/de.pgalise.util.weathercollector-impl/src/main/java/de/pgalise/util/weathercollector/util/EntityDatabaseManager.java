/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.util;

import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;

/**
 * provides type safety for injections
 * @author richter
 */
public interface EntityDatabaseManager extends BaseDatabaseManager<DefaultServiceDataHelper, DefaultWeatherCondition>{
	
}
