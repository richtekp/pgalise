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
 
package de.pgalise.simulation.shared.graphextension;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
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

	/**
	 * Calculates the length of the passed edge and returns it.
	 * 
	 * @param edge
	 *            the edge which length has to be calculated
	 * @return the length of the passed edge
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	public final Double calculateLength(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (!this.hasPosition(edge.getNode0()) || !this.hasPosition(edge.getNode1())) {
			return null;
		}
		return Math
				.sqrt(Math.pow(this.getPosition(edge.getNode0()).x - this.getPosition(edge.getNode1()).x, 2)
						+ (Math.pow(
								this.getPosition(edge.getNode0()).y - this.getPosition(edge.getNode1()).y, 2)));
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
	private final Vector2d calculateVector(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (!this.hasPosition(edge.getNode0()) || !this.hasPosition(edge.getNode1())) {
			return null;
		}
		Vector2d result = this.getPosition(edge.getNode0());
		result.sub(this.getPosition(edge.getNode1()));
		return result;
	}

	/**
	 * Checks whether a passed node is null. If so this method throws an exception.
	 * 
	 * @param node
	 *            the node that has to be checked
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	protected static void checkNode(final Node node) throws IllegalArgumentException {
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
	protected static void checkEdge(final Edge edge) throws IllegalArgumentException {
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
	public final Vector2d getPosition(final Node node) throws IllegalArgumentException {
		DefaultGraphExtensions.checkNode(node);
		return node.getAttribute(DefaultGraphExtensions.POSITION);
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
	public final Vector2d getVectorBetween(final Node from, final Node to) throws IllegalArgumentException,
			IllegalStateException {
		DefaultGraphExtensions.checkNode(from);
		DefaultGraphExtensions.checkNode(to);
		if (!this.hasPosition(from)) {
			throw new IllegalStateException("Argument 'node1' has no position data attached.");
		}
		if (!this.hasPosition(to)) {
			throw new IllegalStateException("Argument 'node2' has no position data attached.");
		}
		Vector2d result = this.getPosition(from);
		result.sub(this.getPosition(to));
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
	public final boolean hasPosition(final Node node) throws IllegalArgumentException {
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
	public final Node setPosition(final Node node, final Vector2d position) throws IllegalArgumentException {
		DefaultGraphExtensions.checkNode(node);
		if (node.hasAttribute(DefaultGraphExtensions.POSITION)) {
			throw new IllegalStateException("Argument \"node\" has already position property attached.");
		}
		if (position == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("node"));
		}
		if ((position.x < 0) || (position.y < 0)) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("position", true));
		}
		node.setAttribute(DefaultGraphExtensions.POSITION, position);
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
	private static Edge setLength(final Edge edge, final Double length) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (length != null && length <= 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("edge", false));
		}
		edge.setAttribute(DefaultGraphExtensions.LENGTH, length);
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
	private static Edge setVector(final Edge edge, final Vector2d vector) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setAttribute(DefaultGraphExtensions.VECTOR, vector);
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
	public final Double getLength(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (edge.getAttribute(DefaultGraphExtensions.LENGTH) == null) {
			DefaultGraphExtensions.setLength(edge, this.calculateLength(edge));
		}
		return edge.getAttribute(DefaultGraphExtensions.LENGTH);
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
	public final Double getMaxSpeed(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (edge.getAttribute(DefaultGraphExtensions.MAX_SPEED) == null) {
			this.setMaxSpeed(edge, 0.0);
		}
		return edge.getAttribute(DefaultGraphExtensions.MAX_SPEED);
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
	public final String getStreetName(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getAttribute(DefaultGraphExtensions.STREET_NAME);
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
	public final Vector2d getVector(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (edge.getAttribute(DefaultGraphExtensions.VECTOR) == null) {
			DefaultGraphExtensions.setVector(edge, this.calculateVector(edge));
		}
		return edge.getAttribute(DefaultGraphExtensions.VECTOR);
	}

	/**
	 * Checks whether a passed edge has attached the length property.
	 * 
	 * @param edge
	 *            the edge that has to be checked
	 * @return true if the passed edge has attached the length property, otherwise false
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	public final boolean hasLength(final Edge edge) throws IllegalArgumentException {
		return this.getLength(edge) != null;
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
	public final boolean hasMaxSpeed(final Edge edge) throws IllegalArgumentException {
		return this.getMaxSpeed(edge) != null;
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
	public final boolean hasStreetName(final Edge edge) throws IllegalArgumentException {
		return this.getStreetName(edge) != null;
	}

	/**
	 * Checks whether a passed edge has attached the vector property.
	 * 
	 * @param edge
	 *            the edge that has to be checked
	 * @return true if the passed edge has attached the vector property, otherwise false
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 */
	public final boolean hasVector(final Edge edge) throws IllegalArgumentException {
		return this.getVector(edge) != null;
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
	public final Edge setMaxSpeed(final Edge edge, final Double maxSpeed) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (maxSpeed != null && maxSpeed < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("edge", true));
		}

		edge.setAttribute(DefaultGraphExtensions.MAX_SPEED, maxSpeed);
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
	public final Edge setStreetName(final Edge edge, final String streetName) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setAttribute(DefaultGraphExtensions.STREET_NAME, streetName);
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
	public final Edge setCarStreet(final Edge edge, final Boolean isCarStreet) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setAttribute(DefaultGraphExtensions.IS_CAR_STREET, isCarStreet);
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
	public final Edge setBicycleStreet(final Edge edge, final Boolean isBicycleStreet) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setAttribute(DefaultGraphExtensions.IS_BICYCLE_STREET, isBicycleStreet);
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
	public final Edge setGrossVehicleWeight(final Edge edge, final Integer grossVehicleWeight)
			throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (grossVehicleWeight != null && grossVehicleWeight <= 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("grossVehicleWeight", false));
		}

		edge.setAttribute(DefaultGraphExtensions.GROSS_VEHICLE_WEIGHT, grossVehicleWeight);
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
	public final Edge setPriorityRoad(final Edge edge, final Boolean isPriorityRoad) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setAttribute(DefaultGraphExtensions.IS_PRIORITY_ROAD, isPriorityRoad);
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
	public final Boolean isCarStreet(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getAttribute(DefaultGraphExtensions.IS_CAR_STREET);
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @return true if the passed edge is a street for bicycles and false if bicycles are not allowed to use this
	 *         street. 'null' if it isn't determined.
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	public final Boolean getIsBicycleStreet(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getAttribute(DefaultGraphExtensions.IS_BICYCLE_STREET);
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @return the
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	public final Integer getGrossVehicleWeight(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getAttribute(DefaultGraphExtensions.GROSS_VEHICLE_WEIGHT);
	}

	/**
	 * @param edge
	 *            the considered edge
	 * @return
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	public final Boolean isPriorityRoad(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return edge.getAttribute(DefaultGraphExtensions.IS_PRIORITY_ROAD);
	}

	@Override
	public void reset() {
	}

	@Override
	public Double getLengthBetween(Node node1, Node node2) {
		checkNode(node1);
		checkNode(node2);
		return this.getLength(node1.getEdgeBetween(node2));
	}
}
