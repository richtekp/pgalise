/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice;

import de.pgalise.simulation.weather.model.WeatherCondition;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;

/**
 *
 * @param <T> the type of the {@link ServiceDataHelper} (in subclasses which use JPA, a persistent class can be enforced)
 * @param <C> 
 * @author richter
 */
public interface WeatherServiceManager<T extends ServiceDataHelper<?,?,C>, C extends WeatherCondition> {
	public void saveInformations(DatabaseManager<C> databaseManager) ;

	/**
	 * @return the saver
	 */
	public WeatherServiceSaver<T,C> getSaver() ;

}
