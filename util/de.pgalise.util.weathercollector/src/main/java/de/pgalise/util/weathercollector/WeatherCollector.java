/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector;

import de.pgalise.simulation.weather.model.WeatherCondition;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import de.pgalise.util.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.util.weathercollector.weatherstation.StationStrategy;
import java.util.Set;

/**
 * This program gets various informations of weather stations and
 * weather services and saves these information to the given database. It only 
 * retrieves current weather data and short term forecast data, it can not be 
 * used to setup historical weather data!
 * @param <T> the type of the {@link ServiceDataHelper} (in subclasses which use JPA, a persistent class can be enforced)
 * @param <C> the type of the {@link WeatherCondition} (in subclasses which use JPA, a persistent class can be enforced)
 * @author richter
 */
public interface WeatherCollector<T extends ServiceDataHelper<?,?>> {
	
	void collectServiceData(BaseDatabaseManager<T> weatherServiceSaver, Set<ServiceStrategy<T>> serviceStrategys);

	/**
	 * Collect weather informations of the weather services
	 * 
	 * @param weatherServiceSaver 
	 */
	public void collectServiceData(BaseDatabaseManager<T> weatherServiceSaver) ;
	
	public void collectStationData(BaseDatabaseManager<T> databaseManager, Set<StationStrategy> stationStrategys) ;

	/**
	 * Collect weather informations of the weather stations
	 * 
	 * @param databaseManager 
	 */
	public void collectStationData(BaseDatabaseManager<T> databaseManager);

}
