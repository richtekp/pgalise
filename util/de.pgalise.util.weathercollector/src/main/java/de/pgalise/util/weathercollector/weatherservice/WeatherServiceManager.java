/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice;

import de.pgalise.util.weathercollector.util.DatabaseManager;

/**
 *
 * @author richter
 */
public interface WeatherServiceManager {

	public void saveInformations(DatabaseManager databaseManager);

	/**
	 * @return the saver
	 */
	public DatabaseManager getSaver();

}
