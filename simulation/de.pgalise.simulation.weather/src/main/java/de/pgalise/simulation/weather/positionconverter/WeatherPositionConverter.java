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
 
package de.pgalise.simulation.weather.positionconverter;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherService;

/**
 * The {@link WeatherService} provides only weather information for one reference point of the simulation city.
 * Therefore, the interface {@link WeatherPostionConverter} makes methods available that convert the reference value to
 * a modified value on another position of the used graph.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 12, 2012)
 */
public interface WeatherPositionConverter {

	/**
	 * Returns the modified reference value for the given position
	 * 
	 * @param <T> 
	 * @param key
	 *            WeatherParameterEnum
	 * @param time
	 *            Timestamp
	 * @param position
	 *            Position
	 * @param refValue
	 *            Reference value
	 * @return Modified value
	 */
	public <T extends Number> T getValue(WeatherParameterEnum key, long time, Coordinate position, T refValue);
}
