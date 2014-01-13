/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector;

import de.pgalise.util.weathercollector.util.DatabaseManager;
import de.pgalise.util.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.util.weathercollector.weatherstation.StationStrategy;
import java.util.Set;

/**
 * This program gets various informations of weather stations and
 * weather services and saves these information to the given database. It only 
 * retrieves current weather data and short term forecast data, it can not be 
 * used to setup historical weather data!
 * @author richter
 */
public interface WeatherCollector {
	
	void collectServiceData(DatabaseManager weatherServiceSaver, Set<ServiceStrategy> serviceStrategys);

	/**
	 * Collect weather informations of the weather services
	 * 
	 * @param weatherServiceSaver 
	 */
	public void collectServiceData(DatabaseManager weatherServiceSaver);
	
	public void collectStationData(DatabaseManager databaseManager, Set<StationStrategy> stationStrategys);

	/**
	 * Collect weather informations of the weather stations
	 * 
	 * @param databaseManager 
	 */
	public void collectStationData(DatabaseManager databaseManager);

}
