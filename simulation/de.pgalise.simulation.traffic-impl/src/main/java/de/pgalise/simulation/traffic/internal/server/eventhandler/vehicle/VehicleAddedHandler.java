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
 
package de.pgalise.simulation.traffic.internal.server.eventhandler.vehicle;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.internal.server.eventhandler.AbstractVehicleEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;

/**
 * @author marcus
 */
public class VehicleAddedHandler extends AbstractVehicleEventHandler<VehicleEvent<?>> {

	@Override
	public EventType getTargetEventType() {
		return VehicleEventTypeEnum.VEHICLE_ADDED;
	}

	@Override
	public void handleEvent(VehicleEvent<?> event) {

	}
}
