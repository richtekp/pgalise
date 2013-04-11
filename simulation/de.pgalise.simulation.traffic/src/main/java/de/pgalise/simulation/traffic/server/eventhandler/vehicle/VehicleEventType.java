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
 
package de.pgalise.simulation.traffic.server.eventhandler.vehicle;

import java.util.EnumSet;

/**
 * This enum describes the events being fired during the update of a specific vehicle.
 * 
 * @author mustafa
 */
public enum VehicleEventType {
	/**
	 * Event is fired before the vehicles in the simulation get updated one by one.
	 */
	PREPARE_UPDATING_VEHICLES,
	/**
	 * Event is fired after all vehicles have been updated.
	 */
	VEHICLES_UPDATED,
	/**
	 * Event is fired when a vehicle has passed a node in the traffic graph.
	 */
	VEHICLE_PASSED_NODE,
	/**
	 * Event is fired when a vehicle has reached its target.
	 */
	VEHICLE_REACHED_TARGET,
	/**
	 * Event is fired to perform an update on a vehicle
	 */
	VEHICLE_UPDATE,

	/**
	 * Event is fired when a vehicle has been added.
	 */
	VEHICLE_ADDED,

	/**
	 * Event is fired when a vehicle has been removed.
	 */
	VEHICLE_REMOVED;

	/**
	 * Event handler listening for this set of events are not supposed to update all currently driving vehicles but the
	 * one that resides in the events payload. This is due the traffic server fires these events while iterating through
	 * the vehicles by itself.
	 */
	public static final EnumSet<VehicleEventType> EVENTS_PROCESSING_VEHICLE = EnumSet.of(VEHICLE_PASSED_NODE,
			VEHICLE_REACHED_TARGET, VEHICLE_UPDATE);
}
