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
 
package de.pgalise.simulation.traffic.internal.server;

import de.pgalise.simulation.service.internal.event.AbstractEventHandlerManager;
import javax.ejb.Local;
import javax.ejb.Stateless;

import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandlerManager;

/**
 * ...
 * 
 * @author Mustafa
 * @version 1.0 (Feb 17, 2013)
 */
@Local(VehicleEventHandlerManager.class)
@Stateless(name = "de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandlerManager")
public class DefaultVehicleEventHandlerManager extends
		AbstractEventHandlerManager<VehicleEventHandler<VehicleEvent<?>>, VehicleEvent<?>> implements
		VehicleEventHandlerManager {

	@Override
	public boolean responsibleFor(VehicleEventHandler<VehicleEvent<?>> handler, VehicleEvent<?> event) {
		return handler.getTargetEventType().equals(event.getType());
	}

}
