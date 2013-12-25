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
 
package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.traffic.TrafficEdge;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import java.util.List;
import javax.annotation.ManagedBean;
import de.pgalise.simulation.shared.city.Vector2d;

/**
 * Model for traffic entities that move along the traffic graph (e.g. bicycles or cars).
 * 
 * @author Mustafa
 * @author Marina
 * @version 1.0 (Nov 12, 2012)
 * @param <D>
 */
public interface Vehicle<D extends VehicleData> extends Identifiable {

	/**
	 * @return the velocity
	 */
	public double getVelocity();

	/**
	 * Sets the velocity if this vehicle's status is not STOPPED.
	 * 
	 * @param velocity
	 *            the velocity to set
	 */
	public void setVelocity(double velocity);

	/**
	 * @return the currentNode
	 */
	public TrafficNode getCurrentNode();

	/**
	 * @param currentNode
	 *            the currentNode to set
	 */
	public void setCurrentNode(TrafficNode currentNode);

	/**
	 * @return the nextNode
	 */
	public TrafficNode getNextNode();

	public TrafficNode getPreviousNode();

	/**
	 * @return the path
	 */
	public List<TrafficEdge> getPath();
	
	public List<TrafficNode> getNodePath();

	/**
	 * Sets the path of this vehicle. Simultaneously the currentNode and nextNode property will be set to the first and
	 * second node of the path.
	 * 
	 * @param path
	 *            the path to set
	 */
	public void setPath(List<TrafficEdge> path);

	/**
	 * @return the position
	 */
	public Coordinate getPosition();

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Coordinate position);

	/**
	 * @return the direction
	 */
	public Vector2d getDirection();

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(Vector2d direction);

	/**
	 * @return the name or 'unnamed' if no name is specified
	 */
	public String getName();

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name);

	/**
	 * Sets the state of this vehicle.
	 * 
	 * @param state
	 *            state to set
	 */
	public void setVehicleState(VehicleStateEnum state);

	/**
	 * @return the state
	 */
	public VehicleStateEnum getVehicleState();

	/**
	 * Updates this vehicle's position, direction, orientation and status. If this vehicle reaches its target its status
	 * changes from DRIVING to REACHED_TARGET. Initial status is NOT_STARTED; will change to DRIVING when #update is
	 * called for the first time with parameter elapsedTime > 0. Update has no effect when this vehicle's status is
	 * STOPPED or RECHED_TARGET or NOT_STARTED.
	 * 
	 * @param elapsedTime
	 *            elapsed time in ms since this vehicle has been updated the last time
	 */
	public void update(long elapsedTime);

	// /**
	// * Will be invoked whenever an event occurs. Subclasses can overwrite this
	// * method to handle these events.
	// *
	// * @param event
	// * occurred event
	// */
	// public void handleEvent(Event event);

	/**
	 * Returns current edge.
	 * 
	 * @return current edge
	 */
	public TrafficEdge getCurrentEdge();

	/**
	 * Sets the current edge.
	 * 
	 * 
	 * @param edge 
	 */
	public void setCurrentEdge(TrafficEdge edge);

	/**
	 * Returns the next edge.
	 * 
	 * @return edge after the current edge
	 */
	public TrafficEdge getNextEdge();

	/**
	 * Return the previous edge.
	 * 
	 * @return edge before the current edge
	 */
	public TrafficEdge getPreviousEdge();

	/**
	 * Returns the vehicle data
	 * 
	 * @return VehicleData
	 */
	public D getData();

	/**
	 * Sets the vehicle data
	 * 
	 * @param data
	 *            Vehicle data to set
	 */
	public void setData(D data);
	
	public TrafficGraphExtensions getTrafficGraphExtensions();

	public void setTrafficGraphExtensions(TrafficGraphExtensions trafficGraphExtensions);
	
	/**
	 * 
	 * @param node
	 * @return index of the passed node in the path otherwise -1
	 */
	public int getIndex(TrafficNode node);
	
	GpsSensor getGpsSensor();

}
