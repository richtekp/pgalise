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
 
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.WeatherCondition;

/**
 * Interface to complete weather service data
 * 
 * @param <C> 
 * @author Andreas Rehfeldt
 * @version 1.0 (Apr 16, 2012)
 */
public interface ServiceDataCompleter<C extends WeatherCondition> {

	/**
	 * Concentrate the objects
	 * 
	 * @param obj
	 *            new obj
	 */
	public void complete(ServiceDataCurrent obj);

}
