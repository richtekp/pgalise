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

import de.pgalise.simulation.traffic.event.AbstractVehicleEvent;
import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.event.RoadBarrierTrafficEvent;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This enum describes the events being fired during the update of a specific vehicle.
 * 
 * @author mustafa
 */
public enum VehicleEventTypeEnum implements EventType {
	/**
	 * Event is fired before the vehicles in the simulation get updated one by one.
	 */
	PREPARE_UPDATING_VEHICLES(1, AbstractVehicleEvent.class),
	/**
	 * Event is fired after all vehicles have been updated.
	 */
	VEHICLES_UPDATED(2, AbstractVehicleEvent.class),
	/**
	 * Event is fired when a vehicle has passed a node in the traffic graph.
	 */
	VEHICLE_PASSED_NODE(3, AbstractVehicleEvent.class),
	/**
	 * Event is fired when a vehicle has reached its target.
	 */
	VEHICLE_REACHED_TARGET(4, AbstractVehicleEvent.class),
	/**
	 * Event is fired to perform an update on a vehicle
	 */
	VEHICLE_UPDATE(5, AbstractVehicleEvent.class),

	/**
	 * Event is fired when a vehicle has been added.
	 */
	VEHICLE_ADDED(6, AbstractVehicleEvent.class),

	/**
	 * Event is fired when a vehicle has been removed.
	 */
	VEHICLE_REMOVED(7, AbstractVehicleEvent.class);

	/**
	 * ID of the event
	 */
	private final int EVENT_ID;

	/**
	 * class which implements this event.
	 */
	private final Class<?> IMPLEMENTATION_CLASS;

	/**
	 * maps simulation event type ids integers to {@link SimulationEventTypeEnum}
	 */
	private static Map<Integer, VehicleEventTypeEnum> SIMULATION_EVENT_TYPE_IDS;

	/**
	 * Returns simulation event type ids.
	 * 
	 * @return simulation event type ids.
	 */
	private static Map<Integer, VehicleEventTypeEnum> getSimulationEventTypeIds() {
		if (VehicleEventTypeEnum.SIMULATION_EVENT_TYPE_IDS == null) {
			VehicleEventTypeEnum.SIMULATION_EVENT_TYPE_IDS = new HashMap<>();
		}
		return VehicleEventTypeEnum.SIMULATION_EVENT_TYPE_IDS;
	}

	public static Map<Integer, VehicleEventTypeEnum> getSimulationEventTypeMapAsUnmodifiable() {
		return Collections.unmodifiableMap(VehicleEventTypeEnum.SIMULATION_EVENT_TYPE_IDS);
	}
	
//	@SuppressWarnings("LeakingThisInConstructor")
	private VehicleEventTypeEnum(final int simulationEventID, final Class<?> implementationClass)
			throws IllegalArgumentException, IllegalStateException {
		if (simulationEventID < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("simulationEventID", true));
		}
		if (VehicleEventTypeEnum.getSimulationEventTypeIds().containsKey(simulationEventID)) {
			throw new IllegalStateException("Argument 'simulationEventID' = " + simulationEventID
					+ " is already registered");
		}
		this.EVENT_ID = simulationEventID;
		VehicleEventTypeEnum.getSimulationEventTypeIds().put(simulationEventID, this);
		this.IMPLEMENTATION_CLASS = implementationClass;
	}

	public int getSimulationEventID() {
		return EVENT_ID;
	}

	@Override
	public Class<?> getImplementationClass() {
		return IMPLEMENTATION_CLASS;
	}

	/**
	 * Event handler listening for this set of events are not supposed to update all currently driving vehicles but the
	 * one that resides in the events payload. This is due the traffic server fires these events while iterating through
	 * the vehicles by itself.
	 */
	public static final EnumSet<VehicleEventTypeEnum> EVENTS_PROCESSING_VEHICLE = EnumSet.of(VEHICLE_PASSED_NODE,
			VEHICLE_REACHED_TARGET, VEHICLE_UPDATE);
}
