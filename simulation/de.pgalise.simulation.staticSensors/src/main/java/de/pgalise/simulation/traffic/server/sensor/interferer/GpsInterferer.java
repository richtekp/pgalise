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
 
package de.pgalise.simulation.traffic.server.sensor.interferer;

import de.pgalise.simulation.shared.sensor.SensorInterferer;
import de.pgalise.util.vector.Vector2d;

/**
 * Interface for an interferer which manipulates the {@link GpsSensor}'s input
 * 
 * @author Marcus
 * @author Andreas
 * @version 1.0
 */
public interface GpsInterferer extends SensorInterferer {

	/**
	 * Interferes the GPS position
	 * 
	 * @param mutablePosition
	 *            Position to change
	 * @param realPosition
	 *            Fixed position (e.g. for the weather controller)
	 * @param simTime
	 *            Simulation timestamp
	 * @param vectorUnit
	 *            Vector unit of the gps mapper
	 * @return new position as Vector2d
	 */
	Vector2d interfere(final Vector2d mutablePosition, final Vector2d realPosition, final long simTime,
			final double vectorUnit);
}
