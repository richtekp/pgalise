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
 
package de.pgalise.simulation.traffic;

import java.util.List;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.graphextension.GraphExtensions;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;

/**
 * ...
 * 
 * @author Marcus
 */
public interface TrafficGraphExtensions extends GraphExtensions {

	/**
	 * Sets the RandomSeedService
	 * 
	 * @param rss
	 *            RandomSeedService
	 */
	public void setRandomSeedService(RandomSeedService rss);

	/**
	 * Return the random seed service
	 * 
	 * @return RandomSeedService
	 */
	public RandomSeedService getRandomSeedService();

	/**
	 * Adds a {@link StaticTrafficSensor} to the passed node.
	 * 
	 * @param node
	 *            the node to attach {@link StaticTrafficSensor}
	 * @param sensor
	 *            the {@link StaticTrafficSensor} to attach
	 * @return true if {@link StaticTrafficSensor} could have been added, otherwise false
	 */
	public boolean addSensor(final Node node, final Sensor sensor);

	/**
	 * Returns the {@link StaticTrafficSensor}s from the passed node as an unmodifiable set.
	 * 
	 * @param node
	 *            the node which {@link StaticTrafficSensor}s shall be returned as an unmodifiable set.
	 * @return an unmodifiable set of the {@link StaticTrafficSensor}s of the passed node
	 */
	public Set<Sensor> getSensorsAsUnmodifialable(final Node node);

	/**
	 * Returns the TrafficlightSetof of the passed node. If the passed node hasn't an attached TrafficLightSetof
	 * property it will automatically generated with an null value.
	 * 
	 * @param node
	 *            the node which TrafficlightSetof is asked
	 * @return the TrafficlightSetof of the passed node
	 */
	public TrafficRule getTrafficRule(final Node node);

	/**
	 * Registers the passed vehicle at the passed node. <br>
	 * In this method each static traffic sensors' 'vehicleOnNodeRegistered'-method of the considered node is invoked.
	 * 
	 * @param node
	 *            the node at which the passed vehicle shall be registered
	 * @param vehicle
	 *            the vehicle that shall register at the passed node
	 */
	public void registerOnNode(final Node node, final Vehicle<? extends VehicleData> vehicle);

	/**
	 * Unregisters the passed vehicle at the passed node. <br>
	 * In this method each static traffic sensors' 'vehicleOnNodeRegistered'-method of the considered node is invoked.
	 * 
	 * @param node
	 *            the node from which the passed vehicle shall be unregistered
	 * @param vehicle
	 *            the vehicle that shall unregister from the passed node
	 * @throws IllegalArgumentException
	 *             if argument 'node' or argument 'vehicle' is 'null'
	 */
	public void unregisterFromNode(final Node node, final Vehicle<? extends VehicleData> vehicle);

	/**
	 * Returns the ...
	 * 
	 * @param node
	 * @param vehicleType
	 * @return
	 */
	public List<Vehicle<? extends VehicleData>> getVehiclesOnNode(final Node node, final VehicleTypeEnum vehicleType);

	/**
	 * Removes the passed {@link StaticTrafficSensor} from the passed node.
	 * 
	 * @param node
	 *            the node that shall have the passed {@link StaticTrafficSensor} to be removed
	 * @param sensor
	 *            the {@link StaticTrafficSensor} that shall have the passed static traffic sensor removed
	 * @return true if the passed {@link StaticTrafficSensor} could have been removed from the passed node
	 */
	public boolean removeSensor(final Node node, final Sensor sensor);

	/**
	 * Sets the TrafficlightSetof of the passed node.
	 * 
	 * @param node
	 *            the node which TrafficlightSetof shall be set
	 * @param trafficRule
	 *            the new TrafficlightSetof of the node
	 * @return the passed node for method chaining
	 */
	public Node setTrafficRule(final Node node, final TrafficRule trafficRule);

	/**
	 * Returns the {@link TrafficRule}s as an unmodifiable {@link Set}.
	 * 
	 * @return the {@link TrafficRule}s as an unmodifiable {@link Set}
	 */
	public Set<TrafficRule> getTrafficRulesAsUnmodifiableSet();

	/**
	 * Updates all TrafficRules.
	 * 
	 * @param simulationEventList
	 *            the {@link SimulationEventList}
	 */
	public void updateTrafficRules(EventList simulationEventList);

	/**
	 * @param edge
	 * @param vehicle
	 * @return
	 */
	public boolean addVehicle(final Edge edge, final Vehicle<? extends VehicleData> vehicle);

	/**
	 * @param edge
	 * @param vehicle
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean removeVehicleFromItsEdge(final Vehicle<? extends VehicleData> vehicle);

	/**
	 * @param edge
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Set<Vehicle<? extends VehicleData>> getVehiclesAsUnmodifialable(final Edge edge);

	/**
	 * @param vehicle
	 * @return
	 */
	public Edge getEdgeFor(final Vehicle<? extends VehicleData> vehicle);

	/**
	 * Returns the registered vehicles for a given edge.
	 * 
	 * @param edge
	 *            Edge to check
	 * @param from
	 *            Start node of the given edge
	 * @param to
	 *            End node of the given edge
	 * @return A list with vehicles registered on the given edge
	 * @throws IllegalArgumentException
	 */
	public List<Vehicle<? extends VehicleData>> getVehiclesFor(Edge edge, Node from, Node to);

	/**
	 * Registers a car on a given edge.
	 * 
	 * @param edge
	 *            Edge to register the car on
	 * @param from
	 *            Start node of the given edge
	 * @param to
	 *            End node of the given edge
	 * @param v
	 *            Vehicle to register
	 */
	public void registerOnEdge(Edge edge, Node from, Node to, Vehicle<? extends VehicleData> v);

	/**
	 * Unregisters a registered vehicle from the passed edge.
	 * 
	 * @param edge
	 *            Edge to unregister the car from
	 * @param from
	 *            Start node of the given edge
	 * @param to
	 *            End node of the given edge
	 * @param v
	 *            Vehicle to unregister
	 */
	public void unregisterFromEdge(Edge edge, Node from, Node to, Vehicle<? extends VehicleData> v);

	public Set<Sensor> getSensors(Node node);
}
