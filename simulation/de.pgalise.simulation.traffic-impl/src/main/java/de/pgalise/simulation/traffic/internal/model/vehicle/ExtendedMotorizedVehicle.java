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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleCallback;

/**
 * ...
 * 
 * @author Marcus
 * @version 1.0 (Feb 17, 2013)
 * @param <T>
 *            VehicleData
 */
public class ExtendedMotorizedVehicle<T extends VehicleData> extends BaseVehicle<T> {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -6046625845401699913L;

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ExtendedMotorizedVehicle.class);

	/**
	 * Last registered node of the graph
	 */
	private TrafficNode lastRegisteredNode;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name of the car
	 * @param carData
	 *            Information of the car
	 * @param trafficGraphExtensions  
	 */
	public ExtendedMotorizedVehicle( String name, T carData, TrafficGraphExtensions trafficGraphExtensions) {
		super( name, trafficGraphExtensions);
		this.setData(carData);
	}

	/**
	 * Constructor
	 * 
	 * @param carData
	 *            Information of the car
	 * @param trafficGraphExtensions  
	 */
	public ExtendedMotorizedVehicle( T carData, TrafficGraphExtensions trafficGraphExtensions) {
		super( trafficGraphExtensions);
		if (carData == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("carData"));
		}
		this.setData(carData);
	}

	@Override
	protected void passedNode(final TrafficNode passedNode) {

		if ((this.getPreviousNode() == null) || (this.getNextNode() == null)) {
			// car eliminates the NPE
			return;
		}

		this.setPosition(this.getTrafficGraphExtensions().getPosition(passedNode));
		final double vel = this.getVelocity();
		this.setVelocity(0);
		this.setVehicleState(VehicleStateEnum.STOPPED);
		this.getTrafficGraphExtensions().getTrafficRule(passedNode)
				.register(this, this.getPreviousNode(), this.getNextNode(), new TrafficRuleCallback() {

					@Override
					public boolean onEnter() {
						// is allowed to drive
						ExtendedMotorizedVehicle.this.setVehicleState(VehicleStateEnum.IN_TRAFFIC_RULE);
						return true;
					}

					@Override
					public boolean onExit() {
						// leaves the trafficRule
						ExtendedMotorizedVehicle.this.setVehicleState(VehicleStateEnum.DRIVING);
						ExtendedMotorizedVehicle.this.setVelocity(vel);

						if (ExtendedMotorizedVehicle.this.getPreviousEdge() != null) {
							// log.debug("Unregister car " + this.getName() + " from edge: " +
							// this.getPreviousEdge().getId());
							ExtendedMotorizedVehicle.this.getTrafficGraphExtensions().unregisterFromEdge(
									ExtendedMotorizedVehicle.this.getPreviousEdge(),
									ExtendedMotorizedVehicle.this.getPreviousNode(),
									ExtendedMotorizedVehicle.this.getCurrentNode(), ExtendedMotorizedVehicle.this);
						}

						if (VehicleStateEnum.UPDATEABLE_VEHICLES.contains(ExtendedMotorizedVehicle.this.getVehicleState())) {
							if (ExtendedMotorizedVehicle.this.getCurrentEdge() != null) {
								// log.debug("Register car " + this.getName() + " on edge: " +
								// this.getCurrentEdge().getId());
								ExtendedMotorizedVehicle.this.getTrafficGraphExtensions().registerOnEdge(
										ExtendedMotorizedVehicle.this.getCurrentEdge(),
										ExtendedMotorizedVehicle.this.getCurrentNode(),
										ExtendedMotorizedVehicle.this.getNextNode(), ExtendedMotorizedVehicle.this);
							}
						}

						return true;
					}
				});
	}

	@Override
	protected void postUpdate(TrafficNode passedNode) {
		if (this.getVehicleState() != VehicleStateEnum.REACHED_TARGET) {
			if (passedNode != null) {
				if (this.getTrafficGraphExtensions().getPosition(passedNode).equals(this.getPosition())
						&& this.getVehicleState() != VehicleStateEnum.PAUSED) {
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
					log.debug("Unregistering car "
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
		return "ExtendedMotorizedVehicle (" + this.getData().getClass().getSimpleName() + ") [lastRegisteredNode="
				+ lastRegisteredNode + ", super=" + super.toString() + "]";
	}
}
