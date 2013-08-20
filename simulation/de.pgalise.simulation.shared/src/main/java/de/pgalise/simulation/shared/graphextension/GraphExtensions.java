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

import javax.vecmath.Vector2d;

/**
 * The TrafficGraphExtensions is an interface
 * that provide common function to operate on the traffic graph (e.g. get the posisition of a node).
 * 
 * @author Marcus
 * @version 1.0 (Feb 17, 2013)
 */
public interface GraphExtensions {

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
	public Vector2d getPosition(final Node node);

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
	 */
	public Vector2d getVectorBetween(final Node from, final Node to);

	/**
	 * Checks whether the passed node has an attached position.
	 * 
	 * @param node
	 *            the node that has to be checked
	 * @return true if the node has an attached position property, otherwise false
	 */
	public boolean hasPosition(final Node node);

	/**
	 * Sets the position of the passed node argument. Additionally the new lengths and vectors for each edge of the
	 * passed node are calculated.
	 * 
	 * @param node
	 *            the node which positions shall be set
	 * @param position
	 *            the new position of the node
	 * @return the passed node for method chaining
	 */
	public Node setPosition(final Node node, final Vector2d position);

	/**
	 * Returns the length between two nodes.
	 * @param node1
	 * @param node2
	 * @return
	 */
	public Double getLengthBetween(final Node node1, final Node node2);
	
	/**
	 * Returns the length of the passed edge.
	 * 
	 * @param edge
	 *            the edge which length is asked
	 * @return the length of the passed edge or null if at least one of the edge's node has no position
	 */
	public Double getLength(final Edge edge);

	/**
	 * Returns the maximal speed of the passed edge.
	 * 
	 * @param edge
	 *            the edge which maximal speed is asked (vector units per hour)
	 * @return the maximal speed of the passed edge or null if maxSpeed havn't been set yet
	 */
	public Double getMaxSpeed(final Edge edge);

	/**
	 * Returns the street name of the passed edge.
	 * 
	 * @param edge
	 *            the edge which street name is asked
	 * @return the street name of the passed edge or null if streetName havn't been set yet
	 */
	public String getStreetName(final Edge edge);

	/**
	 * Returns the Vector of the passed edge.
	 * 
	 * @param edge
	 *            the edge which vector is asked.
	 * @return the vector of the passed edge or null if at least one of the edge's node has no position
	 */
	public Vector2d getVector(final Edge edge);

	/**
	 * Checks whether a passed edge has attached the length property.
	 * 
	 * @param edge
	 *            the edge that has to be checked
	 * @return true if the passed edge has attached the length property, otherwise false
	 */
	public boolean hasLength(final Edge edge);

	/**
	 * Checks whether a passed edge has attached the maxSpeed property.
	 * 
	 * @param edge
	 *            the edge that has to be checked
	 * @return true if the passed edge has attached the maxSpeed property,
	 */
	public boolean hasMaxSpeed(final Edge edge);

	/**
	 * Checks whether a passed edge has attached the streetName property.
	 * 
	 * @param edge
	 *            the edge that has to be checked
	 * @return true if the passed edge has attached the streetName property, otherwise false
	 */
	public boolean hasStreetName(final Edge edge);

	/**
	 * Checks whether a passed edge has attached the vector property.
	 * 
	 * @param edge
	 *            the edge that has to be checked
	 * @return true if the passed edge has attached the vector property, otherwise false
	 */
	public boolean hasVector(final Edge edge);

	/**
	 * Sets the maximum speed of the passed edge.
	 * 
	 * @param edge
	 *            the edge which maximum speed has to be set
	 * @param maxSpeed
	 *            the new maximum speed of the edge
	 */
	public Edge setMaxSpeed(final Edge edge, final Double maxSpeed);

	/**
	 * Sets the street name of the passed edge.
	 * 
	 * @param edge
	 *            the edge which street name has to be set
	 * @param length
	 *            the new street name of the edge
	 */
	public Edge setStreetName(final Edge edge, final String streetName);

	/**
	 * Determines whether the passed edge is a street for cars.
	 * 
	 * @param edge
	 *            the considered edge
	 * @param isCarStreet
	 *            flag whether the passed edge is a street for cars or not
	 * @return the passed edge for method chaining
	 */
	public Edge setCarStreet(final Edge edge, final Boolean isCarStreet);

	/**
	 * @param edge
	 *            the considered edge
	 * @param isBicycleStreet
	 * @return the passed edge for method chaining
	 */
	public Edge setBicycleStreet(final Edge edge, final Boolean isBicycleStreet);

	/**
	 * @param edge
	 *            the considered edge
	 * @param grossVehicleWeight
	 * @return the passed edge for method chaining
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null;
	 */
	public Edge setGrossVehicleWeight(final Edge edge, final Integer grossVehicleWeight);

	/**
	 * @param edge
	 *            the considered edge
	 * @param isPriorityRoad
	 * @return the passed edge for method chaining
	 */
	public Edge setPriorityRoad(final Edge edge, final Boolean isPriorityRoad);

	/**
	 * @param edge
	 *            the considered edge
	 * @return true if the passed edge is a street for cars and false if cars are not allowed to use this street. 'null'
	 *         if it isn't determined.
	 */
	public Boolean isCarStreet(final Edge edge);

	/**
	 * @param edge
	 *            the considered edge
	 * @return true if the passed edge is a street for bicycles and false if bicycles are not allowed to use this
	 *         street. 'null' if it isn't determined.
	 */
	public Boolean getIsBicycleStreet(final Edge edge);

	/**
	 * @param edge
	 *            the considered edge
	 * @return the
	 */
	public Integer getGrossVehicleWeight(final Edge edge);

	/**
	 * @param edge
	 *            the considered edge
	 * @return
	 */
	public Boolean isPriorityRoad(final Edge edge);

	/**
	 * ...
	 */
	public void reset();
}
