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
 
package de.pgalise.util.weathercollector.weatherstation;

import de.pgalise.weathercollector.weatherstation.WeatherStationSaver;

/**
 * Interface for the weather stations
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
public interface StationStrategy {

	/**
	 * Saves the informations of the weather station
	 * 
	 * @param saver
	 *            WeatherStationSaver
	 * @param testmode
	 *            Option to enable the test mode (no database commits)
	 */
	public void saveWeather(WeatherStationSaver saver);
}
