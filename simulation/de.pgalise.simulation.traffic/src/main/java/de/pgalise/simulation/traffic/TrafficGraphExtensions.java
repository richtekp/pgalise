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

import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import java.util.Set;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.traffic.graphextension.GraphExtensions;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;

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
	 * Adds a {@link AbstractStaticTrafficSensor} to the passed node.
	 * 
	 * @param node
	 *            the node to attach {@link AbstractStaticTrafficSensor}
	 * @param sensor
	 *            the {@link AbstractStaticTrafficSensor} to attach
	 * @return true if {@link AbstractStaticTrafficSensor} could have been added, otherwise false
	 */
	public boolean addSensor(final TrafficNode node, final StaticTrafficSensor<?> sensor);

	/**
	 * Returns the {@link AbstractStaticTrafficSensor}s from the passed node as an unmodifiable set.
	 * 
	 * @param node
	 *            the node which {@link AbstractStaticTrafficSensor}s shall be returned as an unmodifiable set.
	 * @return an unmodifiable set of the {@link AbstractStaticTrafficSensor}s of the passed node
	 */
	public Set<StaticTrafficSensor<?>> getSensorsAsUnmodifialable(final TrafficNode node);

	/**
	 * Returns the TrafficlightSetof of the passed node. If the passed node hasn't an attached TrafficLightSetof
	 * property it will automatically generated with an null value.
	 * 
	 * @param node
	 *            the node which TrafficlightSetof is asked
	 * @return the TrafficlightSetof of the passed node
	 */
	public TrafficRule getTrafficRule(final TrafficNode node);

	/**
	 * Registers the passed vehicle at the passed node. <br>
	 * In this method each static traffic sensors' 'vehicleOnNodeRegistered'-method of the considered node is invoked.
	 * 
	 * @param node
	 *            the node at which the passed vehicle shall be registered
	 * @param vehicle
	 *            the vehicle that shall register at the passed node
	 */
	public void registerOnNode(final TrafficNode node, final Vehicle<?> vehicle);

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
	public void unregisterFromNode(final TrafficNode node, final Vehicle<?> vehicle);

	/**
	 * Returns the ...
	 * 
	 * @param node
	 * @param vehicleType
	 * @return
	 */
	public Set<Vehicle<?>> getVehiclesOnNode(final TrafficNode node, final VehicleTypeEnum vehicleType);

	/**
	 * Removes the passed {@link AbstractStaticTrafficSensor} from the passed node.
	 * 
	 * @param node
	 *            the node that shall have the passed {@link AbstractStaticTrafficSensor} to be removed
	 * @param sensor
	 *            the {@link AbstractStaticTrafficSensor} that shall have the passed static traffic sensor removed
	 * @return true if the passed {@link AbstractStaticTrafficSensor} could have been removed from the passed node
	 */
	public boolean removeSensor(final TrafficNode node, final StaticTrafficSensor<?> sensor);

	/**
	 * Sets the TrafficlightSetof of the passed node.
	 * 
	 * @param node
	 *            the node which TrafficlightSetof shall be set
	 * @param trafficRule
	 *            the new TrafficlightSetof of the node
	 * @return the passed node for method chaining
	 */
	public TrafficNode setTrafficRule(final TrafficNode node, final TrafficRule trafficRule);

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
	public void updateTrafficRules(EventList<VehicleEvent> simulationEventList);

	/**
	 * @param edge
	 * @param vehicle
	 * @return
	 */
	public boolean addVehicle(final TrafficEdge edge, final Vehicle<?> vehicle);

	/**
	 * @param vehicle
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean removeVehicleFromItsEdge(final Vehicle<?> vehicle);

	/**
	 * @param edge
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Set<Vehicle<?>> getVehiclesAsUnmodifialable(final TrafficEdge edge);

	/**
	 * @param vehicle
	 * @return
	 */
	public TrafficEdge getEdgeFor(final Vehicle<?> vehicle);

	/**
	 * Returns the registered vehicles for a given edge.
	 * 
	 * @param edge
	 *            NavigationEdge to check
	 * @param from
	 *            Start node of the given edge
	 * @param to
	 *            End node of the given edge
	 * @return A list with vehicles registered on the given edge
	 * @throws IllegalArgumentException
	 */
	public Set<Vehicle<?>> getVehiclesFor(TrafficEdge edge, TrafficNode from, TrafficNode to);

	/**
	 * Registers a car on a given edge.
	 * 
	 * @param edge
	 *            NavigationEdge to register the car on
	 * @param from
	 *            Start node of the given edge
	 * @param to
	 *            End node of the given edge
	 * @param v
	 *            Vehicle to register
	 */
	public void registerOnEdge(TrafficEdge edge, TrafficNode from, TrafficNode to, Vehicle<?> v);

	/**
	 * Unregisters a registered vehicle from the passed edge.
	 * 
	 * @param edge
	 *            NavigationEdge to unregister the car from
	 * @param from
	 *            Start node of the given edge
	 * @param to
	 *            End node of the given edge
	 * @param v
	 *            Vehicle to unregister
	 */
	public void unregisterFromEdge(TrafficEdge edge, TrafficNode from, TrafficNode to, Vehicle<?> v);

	public Set<StaticTrafficSensor<?>> getSensors(TrafficNode node);
}
