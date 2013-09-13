/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector;

import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import de.pgalise.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.weathercollector.weatherstation.StationStrategy;
import java.util.Set;
import java.util.logging.Level;

/**
 *
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
