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
package de.pgalise.simulation.controlCenter.internal.ejb;

import de.pgalise.simulation.controlCenter.internal.util.service.CreateAttractionEventService;
import de.pgalise.simulation.controlCenter.internal.util.service.CreateRandomVehicleService;
import de.pgalise.simulation.controlCenter.model.RandomVehicleBundle;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import javax.ejb.Stateful;

/**
 * The default implementation of {@link CreateAttractionEventService}. It uses
 * the {@link CreateRandomVehicleService} to achieve its work.
 *
 * @author Timo
 */
@Stateful
public class DefaultCreateAttractionEventService implements
	CreateAttractionEventService {

	private CreateRandomVehicleService createRandomVehiclesService;
	private TrafficServerLocal trafficServerLocal;

	public DefaultCreateAttractionEventService() {
	}

	/**
	 * Constructor
	 *
	 * @param sensorInterfererService
	 */
	public DefaultCreateAttractionEventService(
		CreateRandomVehicleService createRandomVehiclesService,
		TrafficServerLocal trafficServerLocal) {
		this.createRandomVehiclesService = createRandomVehiclesService;
		this.trafficServerLocal = trafficServerLocal;
	}

	@Override
	public AttractionTrafficEvent createAttractionTrafficEvent(
		RandomVehicleBundle randomDynamicSensorBundle,
		RandomSeedService randomSeedService,
		boolean withSensorInterferer,
		TrafficNode nodeID,
		JaxRSCoordinate position,
		long startTimestamp,
		long endTimestamp) {

		CreateRandomVehiclesEvent createRandomVehiclesEvent
			= (CreateRandomVehiclesEvent) this.createRandomVehiclesService.
			createRandomDynamicSensors(randomDynamicSensorBundle,
				randomSeedService,
				withSensorInterferer);

		return new AttractionTrafficEvent(
			trafficServerLocal,
			System.currentTimeMillis(),
			0,
			startTimestamp,
			endTimestamp,
			nodeID,
			createRandomVehiclesEvent.getCreateRandomVehicleDataList());
	}
}
