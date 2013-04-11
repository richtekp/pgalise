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
 
package de.pgalise.simulation.traffic.internal.model.vehicle;

import java.io.Serializable;
import java.util.UUID;

import org.graphstream.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * Represents a car in the simulation
 * 
 * @author Mustafa
 * @version 1.0 (Nov 7, 2012)
 */
public class DefaultMotorizedVehicle<T extends VehicleData> extends BaseVehicle<T> implements Vehicle<T>, Serializable {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 6917530411538903262L;

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultMotorizedVehicle.class);

	/**
	 * Last registered node of the graph
	 */
	private Node lastRegisteredNode;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the car
	 * @param name
	 *            Name of the car
	 * @param carData
	 *            Information of the car
	 */
	public DefaultMotorizedVehicle(UUID id, String name, T carData, TrafficGraphExtensions trafficGraphExtensions) {
		super(id, name, trafficGraphExtensions);
		this.setData(carData);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the car
	 * @param carData
	 *            Information of the car
	 */
	public DefaultMotorizedVehicle(UUID id, T carData, TrafficGraphExtensions trafficGraphExtensions) {
		super(id, trafficGraphExtensions);
		if (carData == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("carData"));
		}
		this.setData(carData);
	}

	@Override
	protected void passedNode(Node passedNode) {
		if (this.getPreviousEdge() != null) {
			// log.debug("Unregister car " + this.getName() + " from edge: " + this.getPreviousEdge().getId());
			this.getTrafficGraphExtensions().unregisterFromEdge(this.getPreviousEdge(), this.getPreviousNode(),
					this.getCurrentNode(), this);
		}

		if (Vehicle.State.UPDATEABLE_VEHICLES.contains(this.getState())) {
			if (this.getCurrentEdge() != null) {
				// log.debug("Register car " + this.getName() + " on edge: " + this.getCurrentEdge().getId());
				this.getTrafficGraphExtensions().registerOnEdge(this.getCurrentEdge(), this.getCurrentNode(),
						this.getNextNode(), this);
			}
		}
	}

	@Override
	protected void postUpdate(Node passedNode) {
		if (this.getState() != State.REACHED_TARGET) {
			if (passedNode != null) {
				if (this.getTrafficGraphExtensions().getPosition(passedNode).equals(this.getPosition())
						&& this.getState() != State.PAUSED) {
					this.getTrafficGraphExtensions().registerOnNode(passedNode, this);
					log.debug("Registering car "
							+ this.getName()
							+ " on node "
							+ passedNode.getId()
							+ ", new size: "
							+ this.getTrafficGraphExtensions().getVehiclesOnNode(passedNode, this.getData().getType())
									.size());
					lastRegisteredNode = passedNode;
				}
			} else {
				if (lastRegisteredNode != null
						&& !this.getTrafficGraphExtensions().getPosition(lastRegisteredNode).equals(this.getPosition())) {
					this.getTrafficGraphExtensions().unregisterFromNode(lastRegisteredNode, this);
					log.debug("Unregisterering car "
							+ this.getName()
							+ " from node "
							+ lastRegisteredNode.getId()
							+ ", new size: "
							+ this.getTrafficGraphExtensions()
									.getVehiclesOnNode(lastRegisteredNode, this.getData().getType()).size());
					lastRegisteredNode = null;
				}
			}
		}
	}

	@Override
	public String toString() {
		return "DefaultCar [lastRegisteredNode=" + lastRegisteredNode + ", super=" + super.toString() + "]";
	}
}
