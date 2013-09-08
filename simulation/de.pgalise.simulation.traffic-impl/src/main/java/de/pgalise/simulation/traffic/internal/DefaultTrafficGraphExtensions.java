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
 
package de.pgalise.simulation.traffic.internal;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.graphextension.DefaultGraphExtensions;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.server.rules.LeftYieldsToRight;
import de.pgalise.simulation.traffic.internal.server.rules.PriorityRoad;
import de.pgalise.simulation.traffic.internal.server.rules.Roundabout;
import de.pgalise.simulation.traffic.internal.server.rules.StraightForwardRule;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;

/**
 * Extension class which extends EdgeExtensions by traffic related aspects
 * 
 * @author Marcus, Mustafa, Marina
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.traffic.TrafficGraphExtensions")
@Local(TrafficGraphExtensions.class)
public class DefaultTrafficGraphExtensions extends DefaultGraphExtensions implements TrafficGraphExtensions {
	private static final Logger log = LoggerFactory.getLogger(DefaultTrafficGraphExtensions.class);

	/**
	 * constant value which holds the key for the sensor set
	 */
	private final static String SENSORS = "sensors";

	/**
	 * constant value which holds the key for the TrafficlightSetof
	 */
	private final static String TRAFFICLIGHT_RULE = "trafficRule";

	/**
	 * constant value which holds the key for the vehicles set
	 */
	private final static String VEHICLES = "vehicles";

	/**
	 * Set with traffic rules
	 */
	private final Set<TrafficRule> trafficRules = new HashSet<>();

	private RandomSeedService rss;

	private long currentSimTime;

	/**
	 * 
	 */
	private final Map<Vehicle<?>, Edge> vehicleToEdges = new HashMap<>();

	/**
	 * Default constructor
	 */
	public DefaultTrafficGraphExtensions() {

	}

	/**
	 * Constructor
	 * 
	 * @param rss
	 *            RandomSeedService
	 */
	public DefaultTrafficGraphExtensions(RandomSeedService rss) {
		this.rss = rss;
	}

	public void setRandomSeedService(RandomSeedService rss) {
		this.rss = rss;
	}

	public RandomSeedService getRandomSeedService() {
		return rss;
	}

	/**
	 * Returns an instance of the best matching {@link TrafficRule} for the passed {@link Node}
	 * 
	 * @param node
	 *            the {@link Node} where to find the {@link TrafficRule}
	 * @return the best {@link TrafficRule} for the passed {@link Node}. {@link PriorityRoad}, {@link LeftYieldsToRight}
	 *         or {@link Roundabout} can be returned
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	private TrafficRule getBestTrafficRule(final Node node) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);

		if (node.getEdgeSet().size() == 2) {
			final TrafficRule trafficRule = new StraightForwardRule(node);
			this.trafficRules.add(trafficRule);
			return trafficRule;
		}

		final TrafficRule trafficRule = new Roundabout(node, this.rss, this, this.currentSimTime);
		this.trafficRules.add(trafficRule);
		return trafficRule;

		// int priorityRoadCounter = 0;
		// for (final Edge edge : node) {
		// final Boolean value = this.isPriorityRoad(edge);
		// if ((value != null) && (value == true)) {
		// priorityRoadCounter++;
		// }
		// }
		// if (priorityRoadCounter == 2) {
		// TrafficRule trafficRule = new PriorityRoad(node, null, this);
		// this.trafficRules.add(trafficRule);
		// this.setTrafficRule(node, trafficRule);
		// return trafficRule;
		// }
		// if ((node.getEdgeSet().size() == 3) || (node.getEdgeSet().size() == 4)) {
		// // return new LeftYieldsToRight(node,
		// // DefaultRandomSeedService.getInstance(0));
		// // return new TrafficLightSetof(1, node);
		// }
		// TrafficRule trafficRule = new Roundabout(node, rss, this, 0);
		// this.trafficRules.add(trafficRule);
		// this.setTrafficRule(node, trafficRule);
		// return trafficRule;
	}

	/**
	 * Adds a {@link StaticTrafficSensor} to the passed node.
	 * 
	 * @param node
	 *            the node to attach {@link StaticTrafficSensor}
	 * @param sensor
	 *            the {@link StaticTrafficSensor} to attach
	 * @return true if {@link StaticTrafficSensor} could have been added, otherwise false
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	public boolean addSensor(final Node node, final Sensor sensor) throws IllegalArgumentException {
		if (node == null)
			log.error("Can't add sensor " + sensor.getId()+ " to an empty node");
		DefaultTrafficGraphExtensions.checkNode(node);
		if (sensor == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensor"));
		}
		return this.getSensors(node).add(sensor);
	}

	/**
	 * Returns the {@link StaticTrafficSensor}s from the passed node as an unmodifiable set.
	 * 
	 * @param node
	 *            the node which {@link StaticTrafficSensor}s shall be returned as an unmodifiable set.
	 * @return an unmodifiable set of the {@link StaticTrafficSensor}s of the passed node
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	public Set<Sensor> getSensorsAsUnmodifialable(final Node node) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		return Collections.unmodifiableSet(this.getSensors(node));
	}

	/**
	 * Returns the TrafficlightSetof of the passed node. If the passed node hasn't an attached TrafficLightSetof
	 * property it will automatically generated with an null value.
	 * 
	 * @param node
	 *            the node which TrafficlightSetof is asked
	 * @return the TrafficlightSetof of the passed node
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	public synchronized TrafficRule getTrafficRule(final Node node) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		if (!node.hasAttribute(DefaultTrafficGraphExtensions.TRAFFICLIGHT_RULE)) {
			this.setTrafficRule(node, this.getBestTrafficRule(node));
		}
		return node.getAttribute(DefaultTrafficGraphExtensions.TRAFFICLIGHT_RULE);
	}

	/**
	 * Registers the passed vehicle at the passed node. <br>
	 * In this method each static traffic sensors' 'vehicleOnNodeRegistered'-method of the considered node is invoked.
	 * 
	 * @param node
	 *            the node at which the passed vehicle shall be registered
	 * @param vehicle
	 *            the vehicle that shall register at the passed node
	 * @throws IllegalArgumentException
	 *             if argument 'node' or argument 'vehicle' is 'null'
	 */
	@Override
	public void registerOnNode(final Node node, final Vehicle<? extends VehicleData> vehicle)
			throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		if (vehicle == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}

		String mapName = null;
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicle.getData().getType())) {
			mapName = "cars";
		} else {
			mapName = "bicycles";
		}

		List<Vehicle<? extends VehicleData>> vehicles = node.getAttribute(mapName + "_" + node.getId());

		if (vehicles == null) {
			vehicles = new ArrayList<>();
			node.setAttribute(mapName + "_" + node.getId(), vehicles);
		}
		if (!vehicles.contains(vehicle))
			vehicles.add(vehicle);
	}

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
	public void unregisterFromNode(final Node node, final Vehicle<? extends VehicleData> vehicle)
			throws IllegalArgumentException {
		String mapName = null;
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicle.getData().getType())) {
			mapName = "cars";
		} else {
			mapName = "bicycles";
		}

		List<Vehicle<? extends VehicleData>> vehicles = node.getAttribute(mapName + "_" + node.getId());
		if (vehicles != null) {
			vehicles.remove(vehicle);
		}
	}

	public List<Vehicle<? extends VehicleData>> getVehiclesOnNode(final Node node, final VehicleTypeEnum vehicleType) {
		List<Vehicle<? extends VehicleData>> vehicles = new ArrayList<>();
		String mapName = null;
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicleType)) {
			mapName = "cars";
		} else {
			mapName = "bicycles";
		}

		if (node.getAttribute(mapName + "_" + node.getId()) != null)
			vehicles = node.getAttribute(mapName + "_" + node.getId());

		return vehicles;
	}

	/**
	 * Removes the passed {@link StaticTrafficSensor} from the passed node.
	 * 
	 * @param node
	 *            the node that shall have the passed {@link StaticTrafficSensor} to be removed
	 * @param sensor
	 *            the {@link StaticTrafficSensor} that shall have the passed static traffic sensor removed
	 * @return true if the passed {@link StaticTrafficSensor} could have been removed from the passed node
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	public boolean removeSensor(final Node node, final Sensor sensor) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		if (sensor == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensor"));
		}
		return this.getSensors(node).remove(sensor);
	}

	/**
	 * Sets the TrafficlightSetof of the passed node.
	 * 
	 * @param node
	 *            the node which TrafficlightSetof shall be set
	 * @param trafficRule
	 *            the new TrafficlightSetof of the node
	 * @return the passed node for method chaining
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	public synchronized Node setTrafficRule(final Node node, final TrafficRule trafficRule)
			throws IllegalArgumentException {
		DefaultGraphExtensions.checkNode(node);
		if (trafficRule == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("trafficRule"));
		}
		if (node != trafficRule.getNode()) {
			throw new IllegalStateException("The node for the TrafficRule is not the same as argument 'node'.");
		}
		this.trafficRules.remove(node.getAttribute(DefaultTrafficGraphExtensions.TRAFFICLIGHT_RULE));
		this.trafficRules.add(trafficRule);
		node.setAttribute(DefaultTrafficGraphExtensions.TRAFFICLIGHT_RULE, trafficRule);
		return node;
	}

	/**
	 * Returns the {@link TrafficRule}s as an unmodifiable {@link Set}.
	 * 
	 * @return the {@link TrafficRule}s as an unmodifiable {@link Set}
	 */
	public Set<TrafficRule> getTrafficRulesAsUnmodifiableSet() {
		return Collections.unmodifiableSet(this.trafficRules);
	}

	/**
	 * Updates all TrafficRules.
	 * 
	 * @param simulationEventList
	 *            the {@link SimulationEventList}
	 */
	public void updateTrafficRules(SimulationEventList simulationEventList) {
		this.currentSimTime = simulationEventList.getTimestamp();
		for (final TrafficRule trafficRule : this.trafficRules) {
			trafficRule.update(simulationEventList);
		}
	}

	/**
	 * Creates an empty {@link StaticTrafficSensor} set for the passed node
	 * 
	 * @param node
	 *            the node for which an {@link StaticTrafficSensor} set shall be created
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	private void createSensorSet(final Node node) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		node.setAttribute(DefaultTrafficGraphExtensions.SENSORS, new HashSet<StaticTrafficSensor>());
	}

	/**
	 * Returns the reference to the passed node's {@link StaticTrafficSensor} set.
	 * 
	 * @param node
	 *            the node which reference to its {@link StaticTrafficSensor} set shall be returned
	 * @return the passed node's reference of its {@link StaticTrafficSensor} set.
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	public Set<Sensor> getSensors(final Node node) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		if (!node.hasAttribute(DefaultTrafficGraphExtensions.SENSORS)) {
			this.createSensorSet(node);
		}
		return node.getAttribute(DefaultTrafficGraphExtensions.SENSORS);
	}

	/**
	 * @param edge
	 * @param vehicle
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean addVehicle(final Edge edge, final Vehicle<? extends VehicleData> vehicle)
			throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (vehicle == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}
		if (this.vehicleToEdges.containsKey(vehicle) && (this.vehicleToEdges.get(vehicle) != edge)) {
			throw new IllegalStateException(
					"Argument 'vehicle' is already added to an other edge. Return it first in order to add it here.");
		}
		this.vehicleToEdges.put(vehicle, edge);
		return this.getVehicles(edge).add(vehicle);
	}

	/**
	 * @param edge
	 * @param vehicle
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean removeVehicleFromItsEdge(final Vehicle<? extends VehicleData> vehicle)
			throws IllegalArgumentException {
		if (vehicle == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}
		final Edge edge = this.vehicleToEdges.remove(vehicle);
		if (edge == null) {
			return false;
		}
		return this.getVehicles(edge).remove(vehicle);
	}

	/**
	 * @param edge
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Set<Vehicle<? extends VehicleData>> getVehiclesAsUnmodifialable(final Edge edge)
			throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return Collections.unmodifiableSet(this.getVehicles(edge));
	}

	/**
	 * @param vehicle
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Edge getEdgeFor(final Vehicle<? extends VehicleData> vehicle) throws IllegalArgumentException {
		if (vehicle == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}
		return this.vehicleToEdges.get(vehicle);
	}

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
	public List<Vehicle<? extends VehicleData>> getVehiclesFor(Edge edge, Node from, Node to)
			throws IllegalArgumentException {
		List<Vehicle<? extends VehicleData>> registeredCars = edge.getAttribute("cars_" + from.getId() + "_"
				+ to.getId());
		if (registeredCars == null) {
			registeredCars = new ArrayList<>();
		} else {
			registeredCars = new ArrayList<>(registeredCars);
		}
		return registeredCars;
	}

	/**
	 * @param edge
	 * @return
	 * @throws IllegalArgumentException
	 */
	private Set<Vehicle<? extends VehicleData>> getVehicles(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (!edge.hasAttribute(DefaultTrafficGraphExtensions.VEHICLES)) {
			this.createVehicleSet(edge);
		}
		return edge.getAttribute(DefaultTrafficGraphExtensions.VEHICLES);
	}

	/**
	 * @param edge
	 * @throws IllegalArgumentException
	 */
	private void createVehicleSet(final Edge edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setAttribute(DefaultTrafficGraphExtensions.VEHICLES, new HashSet<Vehicle<? extends VehicleData>>());
	}

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
	public void registerOnEdge(Edge edge, Node from, Node to, Vehicle<? extends VehicleData> v) {
		String mapName = null;
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(v.getData().getType())) {
			mapName = "cars";
		} else {
			mapName = "bicycles";
		}

		List<Vehicle<? extends VehicleData>> vehicles = edge.getAttribute(mapName + "_" + from.getId() + "_"
				+ to.getId());

		if (vehicles == null) {
			vehicles = new ArrayList<>();
			edge.setAttribute(mapName + "_" + from.getId() + "_" + to.getId(), vehicles);
		}
		vehicles.add(v);
	}

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
	public void unregisterFromEdge(Edge edge, Node from, Node to, Vehicle<? extends VehicleData> v) {
		String mapName = null;
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(v.getData().getType())) {
			mapName = "cars";
		} else {
			mapName = "bicycles";
		}

		List<Vehicle<? extends VehicleData>> vehicles = edge.getAttribute(mapName + "_" + from.getId() + "_"
				+ to.getId());
		if (vehicles != null) {
			vehicles.remove(v);
		}
	}

	@Override
	public void reset() {
		this.vehicleToEdges.clear();
		this.trafficRules.clear();
	}
}
