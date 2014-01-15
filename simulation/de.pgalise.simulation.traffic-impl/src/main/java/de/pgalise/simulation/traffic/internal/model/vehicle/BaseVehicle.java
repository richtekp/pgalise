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

import de.pgalise.simulation.shared.JaxRSCoordinate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.Orientation;
import de.pgalise.simulation.shared.entity.Identifiable;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import java.util.LinkedList;
import de.pgalise.simulation.shared.JaxbVector2d;
import de.pgalise.simulation.traffic.entity.TrafficTrip;

/**
 * Superclass for vehicles
 * 
 * @param <D> 
 * @author Mustafa
 * @author Marina
 * @version 1.0 (Nov 1, 2012)
 */
public abstract class BaseVehicle<D extends VehicleData> extends Identifiable implements Vehicle<D> {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1628469891526892322L;

	/**
	 * Logger
	 */
	private static transient Logger log = LoggerFactory.getLogger(BaseVehicle.class);

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
	private JaxRSCoordinate position;

	/**
	 * Direction
	 */
	private JaxbVector2d direction;

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
	
	private VehicleStateEnum state;

	private transient TrafficGraphExtensions trafficGraphExtensions;
	
	private TrafficTrip trafficTrip;

	protected BaseVehicle() {
	}

	public BaseVehicle(Long id,TrafficGraphExtensions trafficGraphExtensions) {
		this.trafficGraphExtensions = trafficGraphExtensions;
		this.vehicleState = (VehicleStateEnum.NOT_STARTED);
	}

	public BaseVehicle(Long id, String name, TrafficGraphExtensions trafficGraphExtensions) {
		super(id);
		this.name = name;
		this.trafficGraphExtensions = trafficGraphExtensions;
		this.state = VehicleStateEnum.NOT_STARTED;
	}

	public BaseVehicle(Long id, String name, D data, TrafficGraphExtensions trafficGraphExtensions) {
		super(id);
		this.name = name;
		this.vehicleData = data;
		this.trafficGraphExtensions = trafficGraphExtensions;
		this.state = VehicleStateEnum.NOT_STARTED;
	}

	@Override
	public void setGpsSensor(GpsSensor gpsSensor) {
		this.gpsSensor = gpsSensor;
	}

	@Override
	public GpsSensor getGpsSensor() {
		return gpsSensor;
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
	public JaxbVector2d getDirection() {
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
	public JaxRSCoordinate getPosition() {
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
	public void setDirection(JaxbVector2d direction) {
		this.direction = direction;
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
	public void setPosition(JaxRSCoordinate position) {
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
		this.preUpdate(elapsedTime);
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
			this.passedNode(passedNode);
		}
		// if(passedNode!=null)
		// log.info("postUpdate on "+this.getName()+", passedNode: "+passedNode.getId());
		// else
		// log.info("postUpdate on "+this.getName()+", passedNode: null");
		this.postUpdate(passedNode);
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

	protected JaxbVector2d getDirection(JaxRSCoordinate a, JaxRSCoordinate b) {
		JaxbVector2d dir = new JaxbVector2d(b.getX(), b.getY());
		JaxbVector2d aVector = new JaxbVector2d(a.getX(), a.getY());
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
	protected Orientation getOrientation(JaxbVector2d direction) {
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
			JaxbVector2d positionVector = new JaxbVector2d(this.position.getX(), this.position.getY());
			JaxRSCoordinate nextNodePosition = this.getTrafficGraphExtensions().getPosition(this._getNextNode());
			JaxbVector2d nextNodeVector = new JaxbVector2d(nextNodePosition.getX(), nextNodePosition.getY());
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

			this.passedNode(this.currentNode);

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

			this.passedNode(this.currentNode);
		}
	}

	/**
	 * Checks whether or not the next node is reached.
	 * 
	 * @param orientation
	 * @param position
	 * @return true, if the next node is reached
	 */
	protected boolean hasReachedNextNode(Orientation orientation, JaxRSCoordinate position) {
		return Orientation.isBeyond(orientation, position,
				this.getTrafficGraphExtensions().getPosition(this._getNextNode()));
	}

	protected JaxRSCoordinate update(long elapsedTime, JaxRSCoordinate pos, JaxbVector2d dir) {
		// log.debug("elapsedTime == 0 " + (elapsedTime == 0) + ", velocity == 0 " + (this.velocity == 0));
		if (elapsedTime == 0 || this.velocity == 0) {
			return pos;
		}
		// log.debug("Before calc: elapsedTime: " + elapsedTime + ", velocity: " + this.velocity + ", position: " + pos
		// + ", direction: " + dir);
		double distance = this.velocity * (double) (elapsedTime / 1000);
		// log.debug("Distance: " + distance + ", Add to vector: " + (dir.scale(distance)));
		dir.scale(distance);
		JaxbVector2d posVector = new JaxbVector2d(pos.getX(), pos.getY());
		posVector.add(dir);
		// log.debug("After calc: elapsedTime: " + elapsedTime + ", velocity: " + this.velocity + ", position: " + pos +
		// ", direction: " + dir);
		return new JaxRSCoordinate(posVector.getX(), posVector.getY());
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

	public void setTrafficTrip(TrafficTrip trafficTrip) {
		this.trafficTrip = trafficTrip;
	}

	@Override
	public TrafficTrip getTrafficTrip() {
		return trafficTrip;
	}

	/**
	 * This method will be invoked when ever a node has been passed.
	 * 
	 * @param passedNode
	 */
	protected abstract void passedNode(TrafficNode passedNode) ;

	/**
	 * This method will be invoked after this vehicle was updated.
	 * 
	 * @param lastPassedNode
	 *            Passed Node on the last update otherwise null
	 */
	protected abstract void postUpdate(TrafficNode lastPassedNode);

	/**
	 * This method will be invoked before this vehicle is updated.
	 * 
	 * @param elapsedTime
	 *            elapsed time in ms since this vehicle has been updated the last time
	 */
	protected abstract void preUpdate(long elapsedTime) ;
}
