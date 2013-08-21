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
 
package de.pgalise.simulation.staticsensor.sensor.weather;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorInterferer;
import javax.vecmath.Vector2d;

/**
 * Interface for an interferer which manipulates the weather input
 * 
 * @author Marcus
 * @author Andreas
 * @version 1.0
 */
public interface WeatherInterferer extends SensorInterferer {

	/**
	 * Interferes the sensor value
	 * 
	 * @param mutableValue
	 *            Value to change
	 * @param position
	 *            Position of the sensor
	 * @param simTime
	 *            Simulation timestamp
	 * @return new value
	 */
	double interfere(final double mutableValue, final Coordinate position, final long simTime);
}
