/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;

/**
 *
 * @param <T> the type of the {@link ServiceDataHelper} (in subclasses which use JPA, a persistent class can be enforced)
 * @author richter
 */
public interface WeatherServiceContext<T extends ServiceDataHelper<?,?>> {
	
	/**
	 * Returns the best ServiceData
	 * 
	 * @param city
	 *            City
	 * @param databaseManager 
	 * @return Best ServiceData
	 */
	public T getBestWeather(City city, DatabaseManager databaseManager) ;

	/**
	 * Returns the weather informations with the help of a random strategy for the given city
	 * 
	 * @param city
	 *            City
	 * @param databaseManager 
	 * @return ServiceData object
	 * @throws ReadServiceDataException
	 *             Data can not be read by strategy
	 */
	public T getSingleWeather(City city, DatabaseManager databaseManager) throws ReadServiceDataException;

}

