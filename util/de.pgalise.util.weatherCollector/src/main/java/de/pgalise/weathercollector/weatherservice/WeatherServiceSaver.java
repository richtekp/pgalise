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
 
package de.pgalise.weathercollector.weatherservice;

import java.util.List;

import de.pgalise.weathercollector.model.City;
import de.pgalise.weathercollector.model.ServiceDataHelper;

/**
 * Interface for the saver of weather services
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
public interface WeatherServiceSaver {

	/**
	 * Returns a list of reference cities from the database
	 * 
	 * @return list of cities
	 */
	public List<City> getReferenceCities();

	/**
	 * Saves all informations from the weather services
	 * 
	 * @param weather
	 *            ServiceData instance
	 */
	public void saveServiceData(ServiceDataHelper weather);

}
