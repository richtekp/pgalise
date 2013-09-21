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
 
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.shared.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * Create an attraction
 * 
 * @author Andreas
 * @version 1.0
 */
public class CreateAttractionEventHandler extends CreateRandomVehicleEventHandler<AttractionTrafficEvent<?>> {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CreateAttractionEventHandler.class);

	/**
	 * Simulation event type
	 */
	private static final EventType type = TrafficEventTypeEnum.ATTRACTION_TRAFFIC_EVENT;

	/**
	 * Constructor
	 */
	public CreateAttractionEventHandler() {
	}

	@Override
	public EventType getTargetEventType() {
		return CreateAttractionEventHandler.type;
	}

	@Override
	public void handleEvent(AttractionTrafficEvent<?> event) {
		log.info("Processing ATTRACTION_TRAFFIC_EVENT: Vehicles=" + event.getCreateRandomVehicleDataList().size()
				+ " ; Target=" + event.getNodeID());

		// Create two vehicles with the same ID and properties
		for (CreateRandomVehicleData data : event.getCreateRandomVehicleDataList()) {

			/*
			 * Way there: Create way from a random node to the target node ID
			 */

			// Calculate start time
			long startTime = event.getAttractionStartTimestamp();

			// use node as target node
			TrafficTrip trip = this.getResponsibleServer().createTrip(this.getResponsibleServer().getCityZone(), event.getNodeID(), startTime,
					false);

			// Create vehicle
			Vehicle<? extends VehicleData> v = this.createVehicle(data, trip);

			if (v == null) {
				continue;
			}

			// Schedule vehicle
			this.scheduleVehicle(v, trip.getStartTime());

			this.getResponsibleServer().getEventForVehicle().put(v.getId(), event);
		}
	}
}
