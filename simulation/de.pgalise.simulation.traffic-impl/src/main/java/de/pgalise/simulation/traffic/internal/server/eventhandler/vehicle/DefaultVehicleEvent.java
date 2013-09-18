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

import java.util.Map;

import org.graphstream.graph.Graph;

import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.event.traffic.TrafficEvent;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventType;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;

/**
 * This type of events is raised whenever something happens that concerns a single vehicle,
 * e.g. a vehicle has passed a node.
 * 
 * @author mustafa
 *
 */
public class DefaultVehicleEvent implements VehicleEvent {
	private final TrafficServerLocal server;
	private final Vehicle<? extends VehicleData> currentVehicle;
	private final long simulationTime;
	private final long elapsedTime;
	private final VehicleEventType type;

	public DefaultVehicleEvent(VehicleEventType type, TrafficServerLocal server,
			Vehicle<? extends VehicleData> currentVehicle, long simulationTime, long elapsedTime) {
		this.server = server;
		this.currentVehicle = currentVehicle;
		this.simulationTime = simulationTime;
		this.elapsedTime = elapsedTime;
		this.type = type;
	}

	@Override
	public Vehicle<? extends VehicleData> getVehicle() {
		return currentVehicle;
	}

	@Override
	public long getSimulationTime() {
		return simulationTime;
	}

	@Override
	public long getElapsedTime() {
		return elapsedTime;
	}

	@Override
	public ServiceDictionary getServiceDictionary() {
		return server.getServiceDictionary();
	}

	/**
	 * @return shallow copy of currently driving vehicles
	 */
	@Override
	public Scheduler getDrivingVehicles() {
		return server.getScheduler();
	}

	@Override
	public VehicleEventType getTargetEventType() {
		return type;
	}

	@Override
	public Graph getGraph() {
		return server.getGraph();
	}

	@Override
	public TrafficGraphExtensions getTrafficGraphExtensions() {
		return server.getTrafficGraphExtesions();
	}
	
	@Override
	public Map<Long, TrafficEvent> getEventForVehicleMap() {
		return server.getEventForVehicle();
	}
	
	@Override
	public TrafficServerLocal getServer() {
		return server;
	}

	@Override
	public VehicleEventType getType() {
		return type;
	}
}
