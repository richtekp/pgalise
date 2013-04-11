/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.weathercollector.app;

import java.util.logging.Level;

import de.pgalise.weathercollector.util.Log;
import de.pgalise.weathercollector.weatherservice.WeatherServiceManager;
import de.pgalise.weathercollector.weatherstation.WeatherStationManager;

/**
 * This is the entrance point of the weather collector. This program gets various informations of weather stations and
 * weather services and saves these information to the given database.
 * 
 * @author Andreas Rehfeldt
 * @version 2.1 (Jun 24, 2012)
 */
public final class WeatherCollector {

	/**
	 * Starts the application
	 * 
	 * @param args
	 *            no args
	 */
	public static void main(String[] args) {
		WeatherCollector collector = new WeatherCollector(false);

		Log.writeLog("### PROGRAMM START ###", Level.INFO);

		// Weather services
		collector.collectServiceData();

		// Weather stations
		collector.collectStationData();

		Log.writeLog("### PROGRAMM END ###", Level.INFO);
	}

	/**
	 * Option to enable the test mode (no database commits)
	 */
	private boolean testmode = false;

	/**
	 * Constructor
	 * 
	 * @param testmode
	 *            Option to enable the test mode (no database commits)
	 */
	public WeatherCollector(boolean testmode) {
		this.testmode = testmode;

		this.init();
	}

	/**
	 * Collect weather informations of the weather services
	 */
	public void collectServiceData() {
		WeatherServiceManager collector = new WeatherServiceManager(this.testmode);

		// Get informations and save them
		Log.writeLog("### --- Wetterdienste --- ###", Level.INFO);
		collector.saveInformations();
	}

	/**
	 * Collect weather informations of the weather stations
	 */
	public void collectStationData() {
		WeatherStationManager collector = new WeatherStationManager(this.testmode);

		// Get informations and save them
		Log.writeLog("### --- Wetterstationen --- ###", Level.INFO);
		collector.saveInformations();
	}

	/**
	 * Initiate the class (especially the logger)
	 */
	private void init() {
		// Start logger
		try {
			Log.configLogging(WeatherCollector.class.getName());
		} catch (Exception e) {
			e.printStackTrace();

			// Close program
			System.exit(0);
		}
	}

}
