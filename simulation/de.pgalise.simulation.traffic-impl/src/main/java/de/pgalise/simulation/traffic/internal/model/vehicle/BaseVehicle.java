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

import com.vividsolutions.jts.geom.Coordinate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.Orientation;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import java.util.LinkedList;
import java.util.UUID;
import javax.persistence.Entity;
import javax.vecmath.Vector2d;

/**
 * Superclass for vehicles
 * 
 * @param <E> 
 * @author Mustafa
 * @author Marina
 * @version 1.0 (Nov 1, 2012)
 */
@Entity
public class BaseVehicle<D extends VehicleData> extends AbstractIdentifiable implements Vehicle<D> {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1628469891526892322L;

	/**
	 * Logger
	 */
	private static transient Logger log = LoggerFactory.getLogger(BaseVehicle.class);

	/**
	 * Option to add GPS to the vehicle
	 */
	private boolean hasGPS;

	/**
	 * GPS sensor of the car
	 */
	private transient GpsSensor gpsSensor;

	/**
	 * Current velocity
	 */
	private double velocity = 50d / 360; // 0.138 vu/s = 50 km/h

	/**
	 * Current node
	 */
	private transient TrafficNode currentNode;
	/**
	 * Position
	 */
	private Coordinate position;

	/**
	 * Direction
	 */
	private Vector2d direction;

	/**
	 * Orientation
	 */
	private Orientation orientation;

	/**
	 * Name
	 */
	private String name = "unnamed";

	/**
	 * Current state
	 */
	/*
	 * state is a reserved SQL keyword
	 */
	private VehicleStateEnum vehicleState = VehicleStateEnum.NOT_STARTED;

	/**
	 * Path
	 */
	private transient List<TrafficEdge> edgePath;
	
	private transient List<TrafficNode> nodePath;

	/**
	 * Current edge
	 */
	private transient TrafficEdge currentEdge;

	/**
	 * Previous node
	 */
	private transient TrafficNode prevNode;

	/**
	 * Previous edge
	 */
	private transient TrafficEdge prevEdge;

	/**
	 * Information
	 */
	private D vehicleData;

	private boolean isVirgin = false;

	private transient TrafficGraphExtensions trafficGraphExtensions;

	public BaseVehicle() {
	}

	public BaseVehicle(TrafficGraphExtensions trafficGraphExtensions) {
		this.trafficGraphExtensions = trafficGraphExtensions;
		this.vehicleState = (VehicleStateEnum.NOT_STARTED);
	}

	public BaseVehicle( String name, TrafficGraphExtensions trafficGraphExtensions) {
		this.name = name;
		this.trafficGraphExtensions = trafficGraphExtensions;
		this.vehicleState = (VehicleStateEnum.NOT_STARTED);
	}

	public BaseVehicle(UUID id, String name, TrafficGraphExtensions trafficGraphExtensions) {
		throw new UnsupportedOperationException("clearify the purpose of passing of id");
//		this.name = name;
//		this.trafficGraphExtensions = trafficGraphExtensions;
//		this.state = (State.NOT_STARTED);
	}

	public BaseVehicle(String name, D data, TrafficGraphExtensions trafficGraphExtensions) {
		this.name = name;
		this.vehicleData = data;
		this.trafficGraphExtensions = trafficGraphExtensions;
		this.vehicleState = (VehicleStateEnum.NOT_STARTED);
	}

	public BaseVehicle(UUID id, String name, D data, TrafficGraphExtensions trafficGraphExtensions) {
		throw new UnsupportedOperationException("clearify the purpose of passing of id");
//		this.name = name;
//		this.data = data;
//		this.trafficGraphExtensions = trafficGraphExtensions;
//		this.state = (State.NOT_STARTED);
	}

	@Override
	public TrafficEdge getCurrentEdge() {
		return this.currentEdge;
	}

	@Override
	public TrafficNode getCurrentNode() {
		return this.currentNode;
	}

	@Override
	public D getData() {
		return this.vehicleData;
	}

	@Override
	public Vector2d getDirection() {
		return this.direction;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public TrafficEdge getNextEdge() {
		return this._getNextEdge();
	}

	@Override
	public TrafficNode getNextNode() {
		return this._getNextNode();
	}

	@Override
	public List<TrafficEdge> getPath() {
		return this.edgePath;
	}

	@Override
	public Coordinate getPosition() {
		return this.position;
	}

	@Override
	public TrafficEdge getPreviousEdge() {
		try {
			int index = this.edgePath.indexOf(this.currentEdge);
			if (index >= 0) {
				this.prevEdge = this.edgePath.get(index - 1);
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return this.prevEdge;
	}

	@Override
	public TrafficNode getPreviousNode() {
		try {
			int index = this.edgePath.indexOf(this.currentNode);
			if (index >= 0) {
				this.prevNode = this.nodePath.get(this.nodePath.indexOf(this.currentNode) - 1);
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return this.prevNode;
	}

	@Override
	public VehicleStateEnum getVehicleState() {
		return this.vehicleState;
	}

	@Override
	public double getVelocity() {
		return this.velocity;
	}

	@Override
	public boolean hasGPS() {
		return this.hasGPS;
	}

	@Override
	public void setCurrentEdge(TrafficEdge edge) {
		this.currentEdge = edge;
	}

	@Override
	public void setCurrentNode(TrafficNode currentNode) {
		this.currentNode = currentNode;
	}

	@Override
	public void setData(D data) {
		this.vehicleData = data;
	}

	@Override
	public void setDirection(Vector2d direction) {
		this.direction = direction;
	}

	@Override
	public void setHasGPS(boolean hasGPS) {
		this.hasGPS = hasGPS;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
//	private static <X extends VehicleData> List<TrafficNode<X>> creaeteNodePath(List<TrafficEdge<X>> edgePath) {
//		List<TrafficNode<X>> retValue = new LinkedList<>();
//		retValue.add(edgePath.get(0).getSource());
//		for(TrafficEdge<X> edge : edgePath) {
//			retValue.add(edge.getTarget());
//		}
//		return retValue;
//	}
	private static List<TrafficNode> creaeteNodePath(List<TrafficEdge> edgePath) {
		List<TrafficNode> retValue = new LinkedList<>();
		retValue.add(edgePath.get(0).getSource());
		for(TrafficEdge edge : edgePath) {
			retValue.add(edge.getTarget());
		}
		return retValue;
	}

	@Override
	public void setPath(List<TrafficEdge> path) {
		if (path.size() < 1) {
			throw new IllegalArgumentException("A path needs to consist of at least one edge");
		}
		this.edgePath = path;
		this.nodePath = BaseVehicle.creaeteNodePath( path);
		this.currentNode = this.nodePath.get(0);
		this.position = this.getTrafficGraphExtensions().getPosition(this.currentNode);
		this.currentEdge = path.get(0);
		// calculate direction from currentNode to nextNode
		this.direction = this.getDirection(this.getTrafficGraphExtensions().getPosition(this.currentNode), this
				.getTrafficGraphExtensions().getPosition(this._getNextNode()));
		this.orientation = this.getOrientation(this.direction);
	}

	@Override
	public void setPosition(Coordinate position) {
		this.position = position;
	}

	@Override
	public void setVehicleState(VehicleStateEnum state) {
		// if (state != this.state)
		// logger.debug("Changed state of vehicle " + this.getName() + " from " + this.state + " to " + state);
		if (state == VehicleStateEnum.NOT_STARTED) {
			this.isVirgin = true;
		}
		this.vehicleState = state;
	}

	@Override
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	@Override
	public void update(long elapsedTime) {
		this._preUpdate(elapsedTime);
		if (!(VehicleStateEnum.UPDATEABLE_VEHICLES.contains(this.vehicleState))) {
			return;
		}
		if (this.vehicleState == VehicleStateEnum.STOPPED || this.vehicleState == VehicleStateEnum.IN_TRAFFIC_RULE) {
			this.velocity = 0;
		} else if (this.vehicleState != VehicleStateEnum.PAUSED) {
			this.vehicleState = VehicleStateEnum.DRIVING;
		}

		// logger.debug(String.format("Vehicle '%s' position and velocity before update: %s, %s", this.name,
		// this.position,
		// this.velocity));
		this.position = this.update(elapsedTime, this.position, this.direction);

		TrafficNode passedNode = this.currentNode;

		// has reached node but not last node
		this.handleReachedNode();

		passedNode = (passedNode != this.currentNode || this.isVirgin) ? this.currentNode : null;
		if (this.isVirgin) {
			this._passedNode(passedNode);
		}
		// if(passedNode!=null)
		// log.info("postUpdate on "+this.getName()+", passedNode: "+passedNode.getId());
		// else
		// log.info("postUpdate on "+this.getName()+", passedNode: null");
		this._postUpdate(passedNode);
		// logger.debug(String.format("Vehicle '%s' position and velocity after update: %s, %s", this.name,
		// this.position,
		// this.velocity));
		isVirgin = false;
	}

	/**
	 * Helper method to get the next edge. It enables to secure the functionality of this vehicle regardless if
	 * {@link #getNextEdge()} get overridden in subclasses.
	 * 
	 * @return
	 */
	private TrafficEdge _getNextEdge() {
		TrafficEdge nextEdge = null;
		try {
			int index = this.edgePath.indexOf(this.currentEdge);
			if (index >= 0) {
				nextEdge = this.edgePath.get(index + 1);
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return nextEdge;
	}

	/**
	 * Helper method to get the next node. It enables to secure the functionality of this vehicle regardless if
	 * {@link #getNextNode()} get overridden in subclasses.
	 * 
	 * @return
	 */
	private TrafficNode _getNextNode() {
		TrafficNode nextNode = null;
		try {
			int index = this.nodePath.indexOf(this.currentNode);
			if (index >= 0) {
				nextNode = this.nodePath.get(this.nodePath.indexOf(this.currentNode) + 1);
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return nextNode;
	}

	protected Vector2d getDirection(Coordinate a, Coordinate b) {
		Vector2d dir = new Vector2d(b.x, b.y);
		Vector2d aVector = new Vector2d(a.x, a.y);
		dir.sub(aVector);
		dir.normalize();
		return dir;
	}

	/**
	 * Returns a direction enum for the coordinates of the private transport vehicle.
	 * 
	 * @param direction 
	 * @return Direction enum
	 */
	protected Orientation getOrientation(Vector2d direction) {
		return Orientation.getOrientation(direction);
	}

	protected void handleReachedNode() {
		boolean reachedNextNode = this.hasReachedNextNode(this.orientation, this.position);
		if (reachedNextNode && (this._getNextNode() != this.nodePath.get(this.nodePath.size() - 1))) {
			// new direction and orientation
			this.direction = this.getDirection(
					this.getTrafficGraphExtensions().getPosition(this._getNextNode()),
					this.getTrafficGraphExtensions().getPosition(
							this.nodePath.get(this.nodePath.indexOf(this._getNextNode()) + 1)));
			this.orientation = this.getOrientation(this.direction);

			// calculate new position on the path
			Vector2d positionVector = new Vector2d(this.position.x, this.position.y);
			Coordinate nextNodePosition = this.getTrafficGraphExtensions().getPosition(this._getNextNode());
			Vector2d nextNodeVector = new Vector2d(nextNodePosition.x, nextNodePosition.y);
			positionVector.sub(nextNodeVector);
			double scale = positionVector
					.length();
			// log.debug("Drüber gefahren: "+scale);
			// log.debug(String.format("Edge (%s, %s): ", getNextNode().getId(),
			// path.get(path.indexOf(getNextNode()) + 1).getId()));
			// log.debug("DIR: "+NodeExtensions.getInstance().getPosition(path.get(path.indexOf(getNextNode())
			// + 1)).
			// sub(NodeExtensions.getInstance().getPosition(getNextNode())));
			this.direction.scale(scale);
			nextNodeVector
					.add(this.direction);
			this.position = this.getTrafficGraphExtensions().getPosition(this._getNextNode());

			// log.debug(String.format("Vehicle \"%s\" passed by intermediate node \"%s\" (index: %s)", this.name,
			// this._getNextNode().getId(), this.getIndex(this._getNextNode())));

			this.prevNode = this.currentNode;
			this.currentNode = this._getNextNode();
			this.prevEdge = this.currentEdge;
			this.currentEdge = this._getNextEdge();

			this._passedNode(this.currentNode);

			// log.debug("Vehicle's "+name+" position: "+this.position);
			if (this.hasReachedNextNode(this.orientation, this.position)) {
				this.handleReachedNode();
			}
		}
		// arrived at targets position
		else if (reachedNextNode && (this._getNextNode() == this.nodePath.get(this.nodePath.size() - 1))) {
			this.position = this.getTrafficGraphExtensions().getPosition(this._getNextNode());

			log.debug(String.format("Vehicle \"%s\" arrived at target node \"%s\"", this.name, this.currentNode.getId()));

			this.prevNode = this.currentNode;
			this.currentNode = this._getNextNode();
			setVehicleState(VehicleStateEnum.REACHED_TARGET);
			this.prevEdge = this.currentEdge;
			this.currentEdge = null;

			this._passedNode(this.currentNode);
		}
	}

	/**
	 * Checks whether or not the next node is reached.
	 * 
	 * @param orientation
	 * @param position
	 * @return true, if the next node is reached
	 */
	protected boolean hasReachedNextNode(Orientation orientation, Coordinate position) {
		return Orientation.isBeyond(orientation, position,
				this.getTrafficGraphExtensions().getPosition(this._getNextNode()));
	}

	private void _passedNode(TrafficNode passedNode) {
		// if(this.state!=State.PAUSED)
		passedNode(passedNode);
	}

	private void _postUpdate(TrafficNode lastPassedNode) {
		// if(this.state!=State.PAUSED)
		postUpdate(lastPassedNode);
	}

	private void _preUpdate(long elapsedTime) {
		// if(this.state!=State.PAUSED)
		preUpdate(elapsedTime);
	}

	/**
	 * This method will be invoked when ever a node has been passed.
	 * 
	 * @param passedNode
	 */
	protected void passedNode(TrafficNode passedNode) {
	}

	/**
	 * This method will be invoked after this vehicle was updated.
	 * 
	 * @param lastPassedNode
	 *            Passed Node on the last update otherwise null
	 */
	protected void postUpdate(TrafficNode lastPassedNode) {
	}

	/**
	 * This method will be invoked before this vehicle is updated.
	 * 
	 * @param elapsedTime
	 *            elapsed time in ms since this vehicle has been updated the last time
	 */
	protected void preUpdate(long elapsedTime) {
	}

	protected Coordinate update(long elapsedTime, Coordinate pos, Vector2d dir) {
		// log.debug("elapsedTime == 0 " + (elapsedTime == 0) + ", velocity == 0 " + (this.velocity == 0));
		if (elapsedTime == 0 || this.velocity == 0) {
			return pos;
		}
		// log.debug("Before calc: elapsedTime: " + elapsedTime + ", velocity: " + this.velocity + ", position: " + pos
		// + ", direction: " + dir);
		double distance = this.velocity * (double) (elapsedTime / 1000);
		// log.debug("Distance: " + distance + ", Add to vector: " + (dir.scale(distance)));
		dir.scale(distance);
		Vector2d posVector = new Vector2d(pos.x, pos.y);
		posVector.add(dir);
		// log.debug("After calc: elapsedTime: " + elapsedTime + ", velocity: " + this.velocity + ", position: " + pos +
		// ", direction: " + dir);
		return new Coordinate(posVector.x, posVector.y);
	}

	@Override
	public TrafficGraphExtensions getTrafficGraphExtensions() {
		return this.trafficGraphExtensions;
	}

	@Override
	public void setTrafficGraphExtensions(TrafficGraphExtensions trafficGraphExtensions) {
		this.trafficGraphExtensions = trafficGraphExtensions;
	}

	@Override
	public int getIndex(TrafficNode node) {
		for (int i = 0; i < this.edgePath.size(); i++) {
			TrafficNode n = this.nodePath.get(i);
			if (n.getId().equals(node.getId())) {
				return i;
			}
		}
		return -1;
	}

	public void setNodePath(
		List<TrafficNode> nodePath) {
		this.nodePath = nodePath;
	}

	@Override
	public List<TrafficNode> getNodePath() {
		return nodePath;
	}
}
