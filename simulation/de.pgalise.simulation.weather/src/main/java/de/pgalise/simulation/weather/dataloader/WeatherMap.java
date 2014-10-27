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
 
/**
 * 
 */
package de.pgalise.simulation.weather.dataloader;

import de.pgalise.simulation.weather.entity.AbstractStationData;
import java.util.HashMap;


/**
 * The class {@link Weather} represents every single weather information entry to a particular timestamp. This class
 * combines all available parameter values and is stored in a {@link WeatherMap}. The {@link WeatherMap} serves as the
 * root for the weather modifiers that are all derived from the class {@link WeatherMapModifier}.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (03.07.2012)
 */
public abstract class WeatherMap extends HashMap<Long, AbstractStationData> {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -3382960454776440036L;
	/**
	 * Interpolate interval
	 */
	public static final int INTERPOLATE_INTERVAL = 60000;

	/**
	 * Deploy changes to the map (decorator pattern)
	 */
	public abstract void deployChanges();

}
