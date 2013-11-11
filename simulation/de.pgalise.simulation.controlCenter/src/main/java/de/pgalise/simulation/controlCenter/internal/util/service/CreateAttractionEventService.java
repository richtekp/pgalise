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
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;

/**
 * This services creates an attraction event.
 * @author Timo
 */
public interface CreateAttractionEventService {
	/**
	 * Creates an attraction traffic event.
	 * @param randomDynamicSensorBundle
	 * 			contains how many random vehicles shall be created
	 * @param randomSeedService
	 * 			for random IDs
	 * @param withSensorInterferer
	 * 			sensors with interferer or not
	 * @param nodeID
	 * 			the ID of the attractions node. Where the attraction takes place. This is
	 * 			needed by the simulation to find the point in the graph.
	 * @param position
	 * 			the position of the attraction. Where the attraction takes place. This is
	 * 			needed by the client.
	 * @param startTimestamp
	 * 			the start time of the attraction
	 * @param endTimestamp
	 * 			the end time of the attraction
	 * @return
	 */
	public AttractionTrafficEvent createAttractionTrafficEvent(RandomVehicleBundle 
			randomDynamicSensorBundle, 
			RandomSeedService randomSeedService,
			boolean withSensorInterferer,
			TrafficNode nodeID,
			Coordinate position,
			long startTimestamp,
			long endTimestamp);
}
