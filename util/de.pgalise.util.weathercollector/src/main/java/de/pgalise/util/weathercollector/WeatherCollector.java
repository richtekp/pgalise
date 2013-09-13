/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector;

import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import de.pgalise.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.weathercollector.weatherstation.StationStrategy;
import java.util.Set;

/**
 * This program gets various informations of weather stations and
 * weather services and saves these information to the given database. It only 
 * retrieves current weather data and short term forecast data, it can not be 
 * used to setup historical weather data!
 * @author richter
 */
public interface WeatherCollector {
	
	void collectServiceData(BaseDatabaseManager weatherServiceSaver, Set<ServiceStrategy> serviceStrategys);

	/**
	 * Collect weather informations of the weather services
	 */
	public void collectServiceData(BaseDatabaseManager weatherServiceSaver) ;
	
	public void collectStationData(BaseDatabaseManager databaseManager, Set<StationStrategy> stationStrategys) ;

	/**
	 * Collect weather informations of the weather stations
	 */
	public void collectStationData(BaseDatabaseManager databaseManager);

}
