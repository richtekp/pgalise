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
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsNoInterferer;
import javax.ejb.Stateless;

/**
 * Represents an interferer that shows no errors
 *
 * @author Marcus
 * @version 1.0 (Nov 12, 2012)
 */
@Stateless
public class DefaultGpsNoInterferer implements GpsNoInterferer {

	private static final long serialVersionUID = 1L;

	@Override
	public BaseCoordinate interfere(BaseCoordinate mutablePosition,
		BaseCoordinate realPosition,
		long simTime) {
		// Returns with no change
		return mutablePosition;
	}
}
