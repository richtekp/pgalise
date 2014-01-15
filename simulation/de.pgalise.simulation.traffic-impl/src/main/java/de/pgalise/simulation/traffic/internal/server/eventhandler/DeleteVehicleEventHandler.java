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
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.event.DeleteVehiclesEvent;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;

/**
 * The event handler removes vehicles with the given List of UUIDs from the server. The class are used by the
 * {@link DeleteVehiclesEvent}.
 * 
 * @author Andreas
 * @version 1.0
 */
public class DeleteVehicleEventHandler<D extends VehicleData> extends AbstractTrafficEventHandler<D,DeleteVehiclesEvent> {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DeleteVehicleEventHandler.class);

	/**
	 * Simulation event type
	 */
	private static final EventType type = TrafficEventTypeEnum.DELETE_VEHICLES_EVENT;

	/**
	 * Constructor
	 */
	public DeleteVehicleEventHandler() {

	}

	/**
	 * Init the handler
	 */
	@Override
	public void init(TrafficServerLocal server) {
		this.server = server;
	}

	/**
	 * Traffic server
	 */
	@SuppressWarnings("unused")
	private TrafficServerLocal server;

	@Override
	public EventType getTargetEventType() {
		return DeleteVehicleEventHandler.type;
	}

	@Override
	public void handleEvent(DeleteVehiclesEvent event) {
		log.debug("Processing DELETE_VEHICLES_EVENT handleEvent: VehiclesToDelete=" + event.getVehicle());

		throw new RuntimeException("Not implemented!");
	}
}
