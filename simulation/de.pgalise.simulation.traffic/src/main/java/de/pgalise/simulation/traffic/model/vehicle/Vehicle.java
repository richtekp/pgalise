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

import java.io.Serializable;
import java.util.EnumSet;
import java.util.UUID;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import javax.vecmath.Vector2d;

/**
 * Model for traffic entities that move along the traffic graph (e.g. bicycles or cars).
 * 
 * @author Mustafa
 * @author Marina
 * @version 1.0 (Nov 12, 2012)
 */
public interface Vehicle<E extends VehicleData> extends Serializable {

	/**
	 * Status
	 * 
	 * @author Mustafa
	 * @version 1.0 (Dec 27, 2012)
	 */
	public enum State {
		/**
		 * Initial status, vehicle is waiting for departure.
		 */
		NOT_STARTED,
		/**
		 * Vehicle is currently driving.
		 */
		DRIVING,
		/**
		 * Vehicle has arrived at its target.
		 */
		REACHED_TARGET,
		/**
		 * Vehicle is on his way but has to wait for example on a traffic junction.
		 */
		/**
		 * Paused vehicles are not allowed to register on any nodes.
		 */
		PAUSED,
		
		
		STOPPED,
		/**
		 * Vehicle is trapped in a traffic rule of Marcus.
		 */
		IN_TRAFFIC_RULE;
		/**
		 * Vehicles having this status are supposed to be updated when receiving an update event
		 */
		public static final EnumSet<State> UPDATEABLE_VEHICLES = EnumSet.of(State.NOT_STARTED, State.DRIVING, State.STOPPED, State.PAUSED);
	}

	/**
	 * @return the hasGPS
	 */
	public boolean hasGPS();

	/**
	 * @param hasGPS
	 *            the hasGPS to set
	 */
	public void setHasGPS(boolean hasGPS);

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
	public Node getCurrentNode();

	/**
	 * @param currentNode
	 *            the currentNode to set
	 */
	public void setCurrentNode(Node currentNode);

	/**
	 * @return the nextNode
	 */
	public Node getNextNode();

	public Node getPreviousNode();

	/**
	 * @return the path
	 */
	public Path getPath();

	/**
	 * Sets the path of this vehicle. Simultaneously the currentNode and nextNode property will be set to the first and
	 * second node of the path.
	 * 
	 * @param path
	 *            the path to set
	 */
	public void setPath(Path path);

	/**
	 * @return the position
	 */
	public Vector2d getPosition();

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Vector2d position);

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
	public void setState(State state);

	/**
	 * @return the state
	 */
	public State getState();

	/**
	 * @return the id
	 */
	public UUID getId();

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(UUID id);

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
	public Edge getCurrentEdge();

	/**
	 * Sets the current edge.
	 * 
	 * @param current
	 *            edge to set
	 */
	public void setCurrentEdge(Edge edge);

	/**
	 * Returns the next edge.
	 * 
	 * @return edge after the current edge
	 */
	public Edge getNextEdge();

	/**
	 * Return the previous edge.
	 * 
	 * @return edge before the current edge
	 */
	public Edge getPreviousEdge();

	/**
	 * Returns the vehicle data
	 * 
	 * @return VehicleData
	 */
	public E getData();

	/**
	 * Sets the vehicle data
	 * 
	 * @param data
	 *            Vehicle data to set
	 */
	public void setData(E data);
	
	public TrafficGraphExtensions getTrafficGraphExtensions();

	public void setTrafficGraphExtensions(TrafficGraphExtensions trafficGraphExtensions);
	
	/**
	 * 
	 * @param node
	 * @return index of the passed node in the path otherwise -1
	 */
	public int getIndex(Node node);

}
