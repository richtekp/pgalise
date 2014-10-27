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
 
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared;

import de.pgalise.simulation.traffic.server.sensor.interferer.gps.InfraredInterferer;

/**
 * Represents an interferer that shows no errors
 * 
 * @author Andreas
 * @version 1.0 (Nov 12, 2012)
 */
public class InfraredNoInterferer implements InfraredInterferer {
	private static final long serialVersionUID = 1L;

	@Override
	public int interfere(final int passengersCount) {
		// Returns with no change
		return 1;
	}

}
