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
 
package de.pgalise.simulation.controlCenter.internal.util.service;

import de.pgalise.simulation.controlCenter.internal.model.RandomVehicleBundle;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;

/**
 * Service to create a {@link TrafficEvent} from {@link RandomDynamicSensorBundle}.
 * @author Timo
 */
public interface CreateRandomVehicleService {
	/**
	 * Creates dynamic sensors with random IDs.
	 * @param randomDynamicSensorBundle
	 * 			contains how many random vehicles shall be created
	 * @param randomSeedService
	 * 			to produce random IDs.
	 * @param withSensorInterferer
	 * 			sensors with interferer or not
	 * @return
	 */
	public AbstractTrafficEvent createRandomDynamicSensors(RandomVehicleBundle randomDynamicSensorBundle, 
			RandomSeedService randomSeedService, boolean withSensorInterferer);
}
