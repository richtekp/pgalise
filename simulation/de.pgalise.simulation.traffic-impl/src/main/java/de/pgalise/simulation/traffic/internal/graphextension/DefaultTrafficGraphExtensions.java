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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.city.DefaultNavigationEdge;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
import de.pgalise.simulation.traffic.internal.DefaultTrafficEdge;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.DefaultTrafficNode;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.internal.server.rules.LeftYieldsToRight;
import de.pgalise.simulation.traffic.internal.server.rules.PriorityRoad;
import de.pgalise.simulation.traffic.internal.server.rules.Roundabout;
import de.pgalise.simulation.traffic.internal.server.rules.StraightForwardRule;
import de.pgalise.simulation.traffic.model.vehicle.MotorizedVehicleData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.internal.server.rules.AbstractTrafficRule;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;

/**
 * Extension class which extends EdgeExtensions by traffic related aspects
 * 
 * @param <D> 
 * @param <N> 
 * @param <E> 
 * @author Marcus, Mustafa, Marina
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.traffic.TrafficGraphExtensions")
@Local(TrafficGraphExtensions.class)
public class DefaultTrafficGraphExtensions<D extends VehicleData> extends DefaultGraphExtensions<D> implements TrafficGraphExtensions<DefaultTrafficNode<D>, DefaultTrafficEdge<D>, D, BaseVehicle<D>> {
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
	private final Set<AbstractTrafficRule<D>> trafficRules = new HashSet<>(1);

	private RandomSeedService rss;

	private long currentSimTime;

	protected DefaultTrafficGraphExtensions() {
	}

	/**
	 * 
	 */
	private final Map<BaseVehicle<D>, DefaultTrafficEdge<D>> vehicleToEdges = new HashMap<>();

	public DefaultTrafficGraphExtensions(
		DefaultTrafficGraph<D> graph) {
		super(graph);
	}

	public DefaultTrafficGraphExtensions(RandomSeedService rss,
		DefaultTrafficGraph<D> graph) {
		super(graph);
		this.rss = rss;
	}

	@Override
	public void setRandomSeedService(RandomSeedService rss) {
		this.rss = rss;
	}

	@Override
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
	private AbstractTrafficRule<D> getBestTrafficRule(final DefaultTrafficNode<D> node) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);

		if (getGraph().edgesOf(node).size() == 2) {
			final AbstractTrafficRule<D> trafficRule = new StraightForwardRule<>(node, getGraph());
			this.trafficRules.add(trafficRule);
			return trafficRule;
		}

		final AbstractTrafficRule<D> trafficRule = new Roundabout<>(node, getGraph(), this.rss, this, this.currentSimTime);
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
	@Override
	public boolean addSensor(final DefaultTrafficNode<D> node, final StaticTrafficSensor<DefaultTrafficNode<D>, DefaultTrafficEdge<D>> sensor) throws IllegalArgumentException {
		if (node == null) {
			log.error("Can't add sensor " + sensor.getId()+ " to an empty node");
		}
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
	@Override
	public Set<StaticTrafficSensor<DefaultTrafficNode<D>, DefaultTrafficEdge<D>>> getSensorsAsUnmodifialable(final DefaultTrafficNode<D> node) throws IllegalArgumentException {
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
	@Override
	public synchronized AbstractTrafficRule<D> getTrafficRule(final DefaultTrafficNode<D> node) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		if (node.getTrafficRule() == null) {
			this.setTrafficRule(node, this.getBestTrafficRule(node));
		}
		return node.getTrafficRule();
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
	public void registerOnNode(final DefaultTrafficNode<D> node, final BaseVehicle<D> vehicle)
			throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		if (vehicle == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}

		String mapName;
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicle.getData().getType())) {
			mapName = "cars";
		} else {
			mapName = "bicycles";
		}

		Set<BaseVehicle<D>> vehicles = node.getVehicles();
		if (vehicles == null) {
			vehicles = new HashSet<>(16);
			node.setVehicles(vehicles);
		}
		if (!vehicles.contains(vehicle)) {
			vehicles.add(vehicle);
		}
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
	@Override
	public void unregisterFromNode(final DefaultTrafficNode<D> node, final BaseVehicle<D> vehicle)
			throws IllegalArgumentException {
		String mapName;
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicle.getData().getType())) {
			mapName = "cars";
		} else {
			mapName = "bicycles";
		}

		Set<BaseVehicle<D>> vehicles = node.getVehicles();
		if (vehicles != null) {
			vehicles.remove(vehicle);
		}
	}

	@Override
	public Set<BaseVehicle<D>> getVehiclesOnNode(final DefaultTrafficNode<D> node, final VehicleTypeEnum vehicleType) {
		Set<BaseVehicle<D>> vehicles = new HashSet<>(16);
		String mapName;
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicleType)) {
			mapName = "cars";
		} else {
			mapName = "bicycles";
		}

		if (node.getVehicles() != null) {
			vehicles = node.getVehicles();
		}

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
	@Override
	public boolean removeSensor(final DefaultTrafficNode<D> node, final StaticTrafficSensor<DefaultTrafficNode<D>, DefaultTrafficEdge<D>> sensor) throws IllegalArgumentException {
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
	@Override
	public synchronized DefaultTrafficNode<D> setTrafficRule(final DefaultTrafficNode<D> node, final AbstractTrafficRule<D> trafficRule)
			throws IllegalArgumentException {
		DefaultGraphExtensions.checkNode(node);
		if (trafficRule == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("trafficRule"));
		}
		if (node != trafficRule.getNode()) {
			throw new IllegalStateException("The node for the TrafficRule is not the same as argument 'node'.");
		}
		this.trafficRules.remove(node.getTrafficRule());
		this.trafficRules.add(trafficRule);
		node.setTrafficRule(trafficRule);
		return node;
	}

	/**
	 * Returns the {@link TrafficRule}s as an unmodifiable {@link Set}.
	 * 
	 * @return the {@link TrafficRule}s as an unmodifiable {@link Set}
	 */
	@Override
	public Set<AbstractTrafficRule<D>> getTrafficRulesAsUnmodifiableSet() {
		return Collections.unmodifiableSet(this.trafficRules);
	}

	/**
	 * Updates all TrafficRules.
	 * 
	 * @param simulationEventList
	 *            the {@link SimulationEventList}
	 */
	@Override
	public void updateTrafficRules(EventList<D> simulationEventList) {
		this.currentSimTime = simulationEventList.getTimestamp();
		for (final AbstractTrafficRule<?> trafficRule : this.trafficRules) {
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
	private void createSensorSet(final DefaultTrafficNode<D> node) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		node.setSensors(new HashSet<StaticTrafficSensor>(16));
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
	@Override
	public Set<StaticTrafficSensor<DefaultTrafficNode<D>, DefaultTrafficEdge<D>>> getSensors(final DefaultTrafficNode<D> node) throws IllegalArgumentException {
		DefaultTrafficGraphExtensions.checkNode(node);
		if (node.getSensors() == null) {
			this.createSensorSet(node);
		}
		return node.getSensors();
	}

	/**
	 * @param edge
	 * @param vehicle
	 * @return
	 * @throws IllegalArgumentException
	 */
	@Override
	public boolean addVehicle(final DefaultTrafficEdge<D> edge, final BaseVehicle<D> vehicle)
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
	 * @param vehicle
	 * @return
	 * @throws IllegalArgumentException
	 */
	@Override
	public boolean removeVehicleFromItsEdge(final BaseVehicle<D> vehicle)
			throws IllegalArgumentException {
		if (vehicle == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}
		final DefaultTrafficEdge<D> edge = this.vehicleToEdges.remove(vehicle);
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
	@Override
	public Set<BaseVehicle<D>> getVehiclesAsUnmodifialable(final DefaultTrafficEdge<D> edge)
			throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		return Collections.unmodifiableSet(this.getVehicles(edge));
	}

	/**
	 * @param vehicle
	 * @return
	 * @throws IllegalArgumentException
	 */
	@Override
	public DefaultTrafficEdge<D> getEdgeFor(final BaseVehicle<D> vehicle) throws IllegalArgumentException {
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
	@Override
	public Set<BaseVehicle<D>> getVehiclesFor(DefaultTrafficEdge<D> edge, DefaultTrafficNode<D> from, DefaultTrafficNode<D> to)
			throws IllegalArgumentException {
		Set<BaseVehicle<D>> registeredCars = edge.getVehicles();
		if (registeredCars == null) {
			registeredCars = new HashSet<>();
		} else {
			registeredCars = new HashSet<>(registeredCars);
		}
		return registeredCars;
	}

	/**
	 * @param edge
	 * @return
	 * @throws IllegalArgumentException
	 */
	private Set<BaseVehicle<D>> getVehicles(final DefaultTrafficEdge<D> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		if (edge.getVehicles() == null) {
			this.createVehicleSet(edge);
		}
		return edge.getVehicles();
	}

	/**
	 * @param edge
	 * @throws IllegalArgumentException
	 */
	private void createVehicleSet(final DefaultTrafficEdge<D> edge) throws IllegalArgumentException {
		DefaultGraphExtensions.checkEdge(edge);
		edge.setVehicles(new HashSet<BaseVehicle<D>>());
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
	@Override
	public void registerOnEdge(DefaultTrafficEdge<D> edge, DefaultTrafficNode<D> from, DefaultTrafficNode<D> to, BaseVehicle<D> v) {
		if(v == null) {
			throw new IllegalArgumentException("v");
		}
		Set<BaseVehicle<D>> vehicles = edge.getVehicles();
		if(vehicles == null) {
			edge.setVehicles(new HashSet<BaseVehicle<D>>(10));
		}
		edge.getVehicles().add(v);
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
	@Override
	public void unregisterFromEdge(DefaultTrafficEdge<D> edge, DefaultTrafficNode<D> from, DefaultTrafficNode<D> to, BaseVehicle<D> v) {
		if(edge.getVehicles() != null) {
			edge.getVehicles().remove(v);
		}
	}

	@Override
	public void reset() {
		this.vehicleToEdges.clear();
		this.trafficRules.clear();
	}
}
