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
import org.graphstream.graph.Node;

import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultMotorizedVehicle;
import de.pgalise.simulation.traffic.internal.server.eventhandler.AbstractVehicleEventHandler;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle.State;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandler;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleCallback;

/**
 * ...
 * 
 * @author marcus
 */
public class ExtendedVehiclePassedNodeHandler extends AbstractVehicleEventHandler<VehicleEvent> {
	private VehiclePassedNodeHandler defaultHandler;

	public ExtendedVehiclePassedNodeHandler() {
		defaultHandler = new VehiclePassedNodeHandler();
	}

	@Override
	public EventType getTargetEventType() {
		return VehicleEventTypeEnum.VEHICLE_PASSED_NODE;
	}

	@Override
	public void handleEvent(VehicleEvent event) {
		defaultHandler.handleEvent(event);

		final Vehicle<? extends VehicleData> vehicle = event.getVehicle();

		if (vehicle instanceof DefaultMotorizedVehicle<?>) {
			if ((vehicle.getPreviousNode() == null) || (vehicle.getNextNode() == null)) {
				// car eliminates the NPE
				return;
			}

			Node passedNode = vehicle.getCurrentNode();
			vehicle.setPosition(event.getTrafficGraphExtensions().getPosition(passedNode));
			final double vel = vehicle.getVelocity();
			vehicle.setVelocity(0);
			vehicle.setState(State.STOPPED);
			event.getTrafficGraphExtensions().getTrafficRule(passedNode)
					.register(vehicle, vehicle.getPreviousNode(), vehicle.getNextNode(), new TrafficRuleCallback() {

						@Override
						public boolean onEnter() {
							// is allowed to drive
							vehicle.setState(State.IN_TRAFFIC_RULE);
							return true;
						}

						@Override
						public boolean onExit() {
							// leaves the trafficRule
							vehicle.setState(State.DRIVING);
							vehicle.setVelocity(vel);
							return true;
						}
					});
		}
	}
}
