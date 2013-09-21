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
 
package de.pgalise.simulation.traffic.event;

import java.util.Map;

import org.graphstream.graph.Graph;

import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;

/**
 * This type of events is raised whenever something happens that concerns a single vehicle,
 * e.g. a vehicle has passed a node.
 * 
 * @param <D> 
 * @author mustafa
 *
 */
public abstract class AbstractVehicleEvent<D extends VehicleData> extends AbstractTrafficEvent implements VehicleEvent<D> {
	private final Vehicle<D> vehicle;

	public AbstractVehicleEvent(TrafficServerLocal<?>  server, long simulationTime, long elapsedTime, Vehicle<D> vehicles) {
		super(server, simulationTime,
			elapsedTime);
		this.vehicle = vehicles;
	}

	@Override
	public Vehicle<D> getVehicle() {
		return vehicle;
	}

	@Override
	public ServiceDictionary getServiceDictionary() {
		return getResponsibleServer().getServiceDictionary();
	}

	/**
	 * @return shallow copy of currently driving vehicles
	 */
	@Override
	public Scheduler getDrivingVehicles() {
		return getResponsibleServer().getScheduler();
	}

	@Override
	public Graph getGraph() {
		return getResponsibleServer().getGraph();
	}

	@Override
	public TrafficGraphExtensions getTrafficGraphExtensions() {
		return getResponsibleServer().getTrafficGraphExtesions();
	}
	
	@Override
	public Map<Long, AbstractTrafficEvent> getEventForVehicleMap() {
		return getResponsibleServer().getEventForVehicle();
	}
	
	@Override
	public TrafficServerLocal getResponsibleServer() {
		return getResponsibleServer();
	}
}
