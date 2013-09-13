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
 
package de.pgalise.weathercollector.weatherstation;

import de.pgalise.simulation.weather.model.StationData;
import java.util.Set;

import de.pgalise.util.weathercollector.exceptions.SaveStationDataException;

/**
 * Interface for the saver of weather stations
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
public interface WeatherStationSaver {

	/**
	 * Saves a list of station data objects
	 * 
	 * @param list
	 *            List of station data objects
	 * @return true if all the list could be saved
	 * @throws SaveStationDataException
	 *             Will be thrown if the list can not be saved
	 */
	public boolean saveStationDataSet(Set<StationData> list) throws SaveStationDataException;

}
