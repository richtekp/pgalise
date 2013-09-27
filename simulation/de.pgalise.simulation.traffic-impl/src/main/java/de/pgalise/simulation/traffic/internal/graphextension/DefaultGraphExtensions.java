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
 
package de.pgalise.simulation.traffic.internal.graphextension;

import de.pgalise.simulation.traffic.graphextension.GraphExtensions;
import com.vividsolutions.jts.geom.Coordinate;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.graphextension.GraphExtensions;
import javax.vecmath.Vector2d;

/**
 * Extension class which methods interact with edge's attribute hashmap
 * 
 * @author Marcus
 */
public class DefaultGraphExtensions implements GraphExtensions {

	/**
	 * constant value which holds the key for the position
	 */
	public final static String POSITION = "position";

	/**
	 * constant value which holds the key for the length
	 */
	private final static String LENGTH = "length";

	/**
	 * constant value which holds the key for the maximum speed
	 */
	private final static String MAX_SPEED = "maxSpeed";

	/**
	 * constant value which holds the key for the street name
	 */
	private final static String STREET_NAME = "streetName";

	/**
	 * constant value which holds the key for the vector
	 */
	private final static String VECTOR = "vector";

	/**
	 * constant value which holds the key for the is-car-street flag
	 */
	private final static String IS_CAR_STREET = "isCarStreet";

	/**
	 * constant value which holds the key for the is-bicycle-street flag
	 */
	private final static String IS_BICYCLE_STREET = "isBicyleStreet";

	/**
	 * constant value which holds the key for the gross vehicle weight
	 */
	private final static String GROSS_VEHICLE_WEIGHT = "grossVehicleWeight";

	/**
	 * constant value which holds the key for the is-priority-road
	 */
	private final static String IS_PRIORITY_ROAD = "isPriorityRoad";
	
	private TrafficGraph<?> graph;

	public DefaultGraphExtensions(
		TrafficGraph<?> graph) {
		this.graph = graph;
	}

	public TrafficGraph<?> getGraph() {
		return graph;
	}

	/**
	 * Calculates the length of the passed edge and returns it.
	 * 
	 * @param edge
	 *            the edge which length has to be calculated
	 * @return the length of the passed edge
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	public Double calculateLength(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (!this.hasPosition(edge.getSource()) || !this.hasPosition(edge.getTarget())) {
			return null;
		}
		return Math
				.sqrt(Math.pow(this.getPosition(edge.getSource()).x - this.getPosition(edge.getTarget()).x, 2)
						+ (Math.pow(
								this.getPosition(edge.getSource()).y - this.getPosition(edge.getTarget()).y, 2)));
	}

	/**
	 * Calculates the vector of the passed edge and returns it.
	 * 
	 * @param edge
	 *            the edge which vector has to be calculated
	 * @return the vector of the passed edge
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	private Vector2d calculateVector(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (!this.hasPosition(edge.getSource()) || !this.hasPosition(edge.getTarget())) {
			return null;
		}
		return getVectorBetween(edge.getSource(), edge.getTarget());
	}

	/**
	 * Checks whether a passed node is null. If so this method throws an exception.
	 * 
	 * @param node
	 *            the node that has to be checked
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	protected static void checkNode(final TrafficNode node) throws IllegalArgumentException {
		if (node == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("node"));
		}
	}

	/**
	 * Checks whether a passed edge is null. If so this method throws an exception.
	 * 
	 * @param edge
	 *            the edge that has to be checked
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	protected static void checkEdge(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		if (edge == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("edge"));
		}
	}

	/**
	 * Returns the position of the passed node argument. If the node hasn't an attached position property this method
	 * will return null.
	 * 
	 * @param node
	 *            the node which position is asked
	 * @return the position of the passed node argument
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	@Override
	public Coordinate getPosition(final TrafficNode node) throws IllegalArgumentException {
		DefaultGraphExtensions.checkNode(node);
		return node.getGeoLocation();
	}

	/**
	 * Returns the vector between the two nodes as difference.
	 * 
	 * @param from
	 *            first node
	 * @param to
	 *            second node
	 * @return the vector between the two nodes as difference
	 * @exception IllegalArgumentException
	 *                if node1 or node2 is null
	 * @exception IllegalStateException
	 *                if node1 or node2 have no position data attached
	 */
	@Override
	public Vector2d getVectorBetween(final TrafficNode from, final TrafficNode to) throws IllegalArgumentException,
			IllegalStateException {
		DefaultGraphExtensions.checkNode(from);
		DefaultGraphExtensions.checkNode(to);
		if (!this.hasPosition(from)) {
			throw new IllegalStateException("Argument 'node1' has no position data attached.");
		}
		if (!this.hasPosition(to)) {
			throw new IllegalStateException("Argument 'node2' has no position data attached.");
		}
		Coordinate fromPosition = this.getPosition(from);
		Vector2d result = new Vector2d(fromPosition.x, fromPosition.y);
		Coordinate toPosition = this.getPosition(to);
		result.sub(new Vector2d(toPosition.x, toPosition.y));
		return result;
	}

	/**
	 * Checks whether the passed node has an attached position.
	 * 
	 * @param node
	 *            the node that has to be checked
	 * @return true if the node has an attached position property, otherwise false
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	@Override
	public boolean hasPosition(final TrafficNode node) throws IllegalArgumentException {
		return this.getPosition(node) != null;
	}

	/**
	 * Sets the position of the passed node argument. Additionally the new lengths and vectors for each edge of the
	 * passed node are calculated.
	 * 
	 * @param node
	 *            the node which positions shall be set
	 * @param position
	 *            the new position of the node
	 * @return the passed node for method chaining
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	@Override
	public TrafficNode setPosition(final TrafficNode node, final Coordinate position) throws IllegalArgumentException {
		DefaultGraphExtensions.checkNode(node);
		if (node.getGeoLocation() != null) {
			throw new IllegalStateException("Argument \"node\" has already position property attached.");
		}
		if (position == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("node"));
		}
		if ((position.x < 0) || (position.y < 0)) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("position", true));
		}
		node.setGeoLocation(position);
		return node;
	}

	/**
	 * Sets the lengths of the passed edge.
	 * 
	 * @param edge
	 *            the edge which length has to be set
	 * @param length
	 *            the new length of the passed edge
	 * @return the passed edge for method chaining
	 * @throws IllegalArgumentException
	 *             if argument 'edge' or argument 'length' is 'null' or length is not positive
	 */
	private static TrafficEdge<?,?> invalidateLength(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.invalidateLength();
		return edge;
	}

	/**
	 * Sets the vector of the passed edge.
	 * 
	 * @param edge
	 *            the edge which vector has to be set
	 * @param length
	 *            the new vector of the passed edge
	 * @return the passed edge for method chaining
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	private static TrafficEdge<?,?> invalidateVector(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.invalidateVector();
		return edge;
	}

	/**
	 * Returns the length of the passed edge.
	 * 
	 * @param edge
	 *            the edge which length is asked
	 * @return the length of the passed edge or null if at least one of the edge's node has no position
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	@Override
	public Double getLength(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getEdgeLength();
	}

	/**
	 * Returns the maximal speed of the passed edge.
	 * 
	 * @param edge
	 *            the edge which maximal speed is asked (vector units per hour)
	 * @return the maximal speed of the passed edge or null if maxSpeed havn't been set yet
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	@Override
	public Double getMaxSpeed(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getMaxSpeed();
	}

	/**
	 * Returns the street name of the passed edge.
	 * 
	 * @param edge
	 *            the edge which street name is asked
	 * @return the street name of the passed edge or null if streetName havn't been set yet
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	@Override
	public String getStreetName(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getWay().getStreetName();
	}

	/**
	 * Returns the Vector of the passed edge.
	 * 
	 * @param edge
	 *            the edge which vector is asked.
	 * @return the vector of the passed edge or null if at least one of the edge's node has no position
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	@Override
	public Vector2d getVector(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getVector();
	}

	/**
	 * Checks whether a passed edge has attached the maxSpeed property.
	 * 
	 * @param edge
	 *            the edge that has to be checked
	 * @return true if the passed edge has attached the maxSpeed property, otherwise false
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	@Override
	public boolean hasMaxSpeed(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		return this.getMaxSpeed(edge) != -1;
	}

	/**
	 * Checks whether a passed edge has attached the streetName property.
	 * 
	 * @param edge
	 *            the edge that has to be checked
	 * @return true if the passed edge has attached the streetName property, otherwise false
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	@Override
	public boolean hasStreetName(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		return this.getStreetName(edge) != null;
	}

	/**
	 * Sets the maximum speed of the passed edge.
	 * 
	 * @param edge
	 *            the edge which maximum speed has to be set
	 * @param maxSpeed
	 *            the new maximum speed of the edge
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null' or argument 'maxSpeed' is negative
	 */
	@Override
	public TrafficEdge<?,?> setMaxSpeed(final TrafficEdge<?,?> edge, final double maxSpeed) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (maxSpeed < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("edge", true));
		}
		edge.setMaxSpeed(maxSpeed);
		return edge;
	}

	/**
	 * Sets the street name of the passed edge.
	 * 
	 * @param edge
	 *            the edge which street name has to be set
	 * @param length
	 *            the new street name of the edge
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	@Override
	public TrafficEdge<?,?> setStreetName(final TrafficEdge<?,?> edge, final String streetName) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if(edge.getWay() == null) {
			throw new IllegalStateException("way of node %s not set");
		}
		if(edge.getWay().getStreetName() == null) {
			edge.getWay().setStreetName(streetName);
		}else {
			if(edge.getWay().getStreetName() != streetName) {
				throw new IllegalArgumentException(String.format("if streetName of node.way is already set, the streetName can only be identical"));
			}
			edge.getWay().setStreetName(streetName);
		}
		return edge;
	}

	/**
	 * Determines whether the passed edge is a street for cars.
	 * 
	 * @param edge
	 *            the considered edge
	 * @param isCarStreet
	 *            flag whether the passed edge is a street for cars or not
	 * @return the passed edge for method chaining
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	@Override
	public TrafficEdge<?,?> setCarStreet(final TrafficEdge<?,?> edge, final Boolean isCarStreet) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setCarsAllowed(isCarStreet);
		return edge;
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @param isBicycleStreet
	 * @return the passed edge for method chaining
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	@Override
	public TrafficEdge<?,?> setBicycleStreet(final TrafficEdge<?,?> edge, final Boolean isBicycleStreet) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setBicyclesAllowed( isBicycleStreet);
		return edge;
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @param grossVehicleWeight
	 * @return the passed edge for method chaining
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	@Override
	public TrafficEdge<?,?> setGrossVehicleWeight(final TrafficEdge<?,?> edge, final Integer grossVehicleWeight)
			throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (grossVehicleWeight != null && grossVehicleWeight <= 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("grossVehicleWeight", false));
		}

		edge.setGrossVehicleWeight( grossVehicleWeight);
		return edge;
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @param isPriorityRoad
	 * @return the passed edge for method chaining
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	@Override
	public TrafficEdge<?,?> setPriorityRoad(final TrafficEdge<?,?> edge, final Boolean isPriorityRoad) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setPriorityRoad( isPriorityRoad);
		return edge;
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @return true if the passed edge is a street for cars and false if cars are not allowed to use this street. 'null'
	 *         if it isn't determined.
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	@Override
	public Boolean isCarStreet(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.isCarsAllowed();
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @return true if the passed edge is a street for bicycles and false if bicycles are not allowed to use this
	 *         street. 'null' if it isn't determined.
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	@Override
	public Boolean isBicycleStreet(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.isBicyclesAllowed();
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @return the
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	@Override
	public Integer getGrossVehicleWeight(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getGrossVehicleWeight();
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @return
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	@Override
	public Boolean isPriorityRoad(final TrafficEdge<?,?> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.isPriorityRoad();
	}

	@Override
	public void reset() {
	}

	@Override
	public Double getLengthBetween(TrafficNode node1, TrafficNode node2) {
		checkNode(node1);
		checkNode(node2);
		return GeoToolsBootstrapping.distanceHaversineInM(node1.getGeoLocation(),
			node2.getGeoLocation());
	}

}
