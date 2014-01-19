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

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.traffic.VehicleType;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.server.rules.LeftYieldsToRight;
import de.pgalise.simulation.traffic.internal.server.rules.PriorityRoad;
import de.pgalise.simulation.traffic.internal.server.rules.Roundabout;
import de.pgalise.simulation.traffic.internal.server.rules.StraightForwardRule;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.sensor.AbstractStaticTrafficSensor;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension class which extends EdgeExtensions by traffic related aspects
 *
 * @author Marcus, Mustafa, Marina
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.traffic.TrafficGraphExtensions")
@Local(TrafficGraphExtensions.class)
public class DefaultTrafficGraphExtensions extends DefaultGraphExtensions
  implements TrafficGraphExtensions {

  private static final Logger log = LoggerFactory.getLogger(
    DefaultTrafficGraphExtensions.class);

  /**
   * Set with traffic rules
   */
  private final Set<TrafficRule> trafficRules = new HashSet<>(1);

  private RandomSeedService rss;

  private long currentSimTime;

  public DefaultTrafficGraphExtensions() {
  }

  /**
   *
   */
  private final Map<Vehicle<?>, TrafficEdge> vehicleToEdges = new HashMap<>();

  public DefaultTrafficGraphExtensions(
    TrafficGraph graph) {
    super(graph);
  }

  public DefaultTrafficGraphExtensions(RandomSeedService rss,
    TrafficGraph graph) {
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
   * Returns an instance of the best matching {@link TrafficRule} for the passed
   * {@link Node}
   *
   * @param node the {@link Node} where to find the {@link TrafficRule}
   * @return the best {@link TrafficRule} for the passed {@link Node}.
   * {@link PriorityRoad}, {@link LeftYieldsToRight} or {@link Roundabout} can
   * be returned
   * @throws IllegalArgumentException if argument 'node' is 'null'
   */
  private TrafficRule getBestTrafficRule(final TrafficNode node) throws IllegalArgumentException {
    DefaultTrafficGraphExtensions.checkNode(node);

    if (getGraph().edgesOf(node).size() == 2) {
      final TrafficRule trafficRule = new StraightForwardRule(node,
        getGraph());
      this.trafficRules.add(trafficRule);
      return trafficRule;
    }

    final TrafficRule trafficRule = new Roundabout(node,
      getGraph(),
      this.rss,
      this,
      this.currentSimTime);
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
   * Adds a {@link AbstractStaticTrafficSensor} to the passed node.
   *
   * @param node the node to attach {@link AbstractStaticTrafficSensor}
   * @param sensor the {@link AbstractStaticTrafficSensor} to attach
   * @return true if {@link AbstractStaticTrafficSensor} could have been added,
   * otherwise false
   * @throws IllegalArgumentException if argument 'node' is 'null'
   */
  @Override
  public boolean addSensor(final TrafficNode node,
    final StaticTrafficSensor sensor) throws IllegalArgumentException {
    if (node == null) {
      log.error("Can't add sensor " + sensor.getId() + " to an empty node");
    }
    DefaultTrafficGraphExtensions.checkNode(node);
    if (sensor == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "sensor"));
    }
    return this.getSensors(node).add(sensor);
  }

  /**
   * Returns the {@link AbstractStaticTrafficSensor}s from the passed node as an
   * unmodifiable set.
   *
   * @param node the node which {@link AbstractStaticTrafficSensor}s shall be
   * returned as an unmodifiable set.
   * @return an unmodifiable set of the {@link AbstractStaticTrafficSensor}s of
   * the passed node
   * @throws IllegalArgumentException if argument 'node' is 'null'
   */
  @Override
  public Set<StaticTrafficSensor<?>> getSensorsAsUnmodifialable(
    final TrafficNode node) throws IllegalArgumentException {
    DefaultTrafficGraphExtensions.checkNode(node);
    return Collections.unmodifiableSet(this.getSensors(node));
  }

  /**
   * Returns the TrafficlightSetof of the passed node. If the passed node hasn't
   * an attached TrafficLightSetof property it will automatically generated with
   * an null value.
   *
   * @param node the node which TrafficlightSetof is asked
   * @return the TrafficlightSetof of the passed node
   * @throws IllegalArgumentException if argument 'node' is 'null'
   */
  @Override
  public synchronized TrafficRule getTrafficRule(final TrafficNode node) throws IllegalArgumentException {
    DefaultTrafficGraphExtensions.checkNode(node);
    if (node.getTrafficRule() == null) {
      this.setTrafficRule(node,
        this.getBestTrafficRule(node));
    }
    return node.getTrafficRule();
  }

  /**
   * Registers the passed vehicle at the passed node. <br>
   * In this method each static traffic sensors'
   * 'vehicleOnNodeRegistered'-method of the considered node is invoked.
   *
   * @param node the node at which the passed vehicle shall be registered
   * @param vehicle the vehicle that shall register at the passed node
   * @throws IllegalArgumentException if argument 'node' or argument 'vehicle'
   * is 'null'
   */
  @Override
  public void registerOnNode(final TrafficNode node,
    final Vehicle<?> vehicle)
    throws IllegalArgumentException {
    DefaultTrafficGraphExtensions.checkNode(node);
    if (vehicle == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "vehicle"));
    }

    String mapName;
    if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicle.getData().getType())) {
      mapName = "cars";
    } else {
      mapName = "bicycles";
    }

    Set<Vehicle<?>> vehicles = node.getVehicles();
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
   * In this method each static traffic sensors'
   * 'vehicleOnNodeRegistered'-method of the considered node is invoked.
   *
   * @param node the node from which the passed vehicle shall be unregistered
   * @param vehicle the vehicle that shall unregister from the passed node
   * @throws IllegalArgumentException if argument 'node' or argument 'vehicle'
   * is 'null'
   */
  @Override
  public void unregisterFromNode(final TrafficNode node,
    final Vehicle<?> vehicle)
    throws IllegalArgumentException {
    String mapName;
    if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicle.getData().getType())) {
      mapName = "cars";
    } else {
      mapName = "bicycles";
    }

    Set<Vehicle<?>> vehicles = node.getVehicles();
    if (vehicles != null) {
      vehicles.remove(vehicle);
    }
  }

  @Override
  public Set<Vehicle<?>> getVehiclesOnNode(final TrafficNode node,
    final VehicleType vehicleType) {
    Set<Vehicle<?>> vehicles = new HashSet<>(16);
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
   * Removes the passed {@link AbstractStaticTrafficSensor} from the passed
   * node.
   *
   * @param node the node that shall have the passed
   * {@link AbstractStaticTrafficSensor} to be removed
   * @param sensor the {@link AbstractStaticTrafficSensor} that shall have the
   * passed static traffic sensor removed
   * @return true if the passed {@link AbstractStaticTrafficSensor} could have
   * been removed from the passed node
   * @throws IllegalArgumentException if argument 'node' is 'null'
   */
  @Override
  public boolean removeSensor(final TrafficNode node,
    final StaticTrafficSensor sensor) throws IllegalArgumentException {
    DefaultTrafficGraphExtensions.checkNode(node);
    if (sensor == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "sensor"));
    }
    return this.getSensors(node).remove(sensor);
  }

  /**
   * Sets the TrafficlightSetof of the passed node.
   *
   * @param node the node which TrafficlightSetof shall be set
   * @param trafficRule the new TrafficlightSetof of the node
   * @return the passed node for method chaining
   * @throws IllegalArgumentException if argument 'node' is 'null'
   */
  @Override
  public synchronized TrafficNode setTrafficRule(final TrafficNode node,
    final TrafficRule trafficRule)
    throws IllegalArgumentException {
    DefaultGraphExtensions.checkNode(node);
    if (trafficRule == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "trafficRule"));
    }
    if (node != trafficRule.getNode()) {
      throw new IllegalStateException(
        "The node for the TrafficRule is not the same as argument 'node'.");
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
  public Set<TrafficRule> getTrafficRulesAsUnmodifiableSet() {
    return Collections.unmodifiableSet(this.trafficRules);
  }

  /**
   * Updates all TrafficRules.
   *
   * @param simulationEventList the {@link SimulationEventList}
   */
  @Override
  public void updateTrafficRules(EventList<VehicleEvent> simulationEventList) {
    this.currentSimTime = simulationEventList.getTimestamp();
    for (final TrafficRule trafficRule : this.trafficRules) {
      trafficRule.update(simulationEventList);
    }
  }

  /**
   * Creates an empty {@link AbstractStaticTrafficSensor} set for the passed
   * node
   *
   * @param node the node for which an {@link AbstractStaticTrafficSensor} set
   * shall be created
   * @throws IllegalArgumentException if argument 'node' is 'null'
   */
  private void createSensorSet(final TrafficNode node) throws IllegalArgumentException {
    DefaultTrafficGraphExtensions.checkNode(node);
    node.setSensors(new HashSet<StaticTrafficSensor<?>>(16));
  }

  /**
   * Returns the reference to the passed node's
   * {@link AbstractStaticTrafficSensor} set.
   *
   * @param node the node which reference to its
   * {@link AbstractStaticTrafficSensor} set shall be returned
   * @return the passed node's reference of its
   * {@link AbstractStaticTrafficSensor} set.
   * @throws IllegalArgumentException if argument 'node' is 'null'
   */
  @Override
  public Set<StaticTrafficSensor<?>> getSensors(final TrafficNode node) throws IllegalArgumentException {
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
  public boolean addVehicle(final TrafficEdge edge,
    final Vehicle<?> vehicle) throws IllegalArgumentException {
    DefaultGraphExtensions.checkEdge(edge);
    if (vehicle == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "vehicle"));
    }
    if (this.vehicleToEdges.containsKey(vehicle) && (this.vehicleToEdges.get(
      vehicle) != edge)) {
      throw new IllegalStateException(
        "Argument 'vehicle' is already added to an other edge. Return it first in order to add it here.");
    }
    this.vehicleToEdges.put(vehicle,
      edge);
    return this.getVehicles(edge).add(vehicle);
  }

  /**
   * @param vehicle
   * @return
   * @throws IllegalArgumentException
   */
  @Override
  public boolean removeVehicleFromItsEdge(final Vehicle<?> vehicle)
    throws IllegalArgumentException {
    if (vehicle == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "vehicle"));
    }
    final TrafficEdge edge = this.vehicleToEdges.remove(vehicle);
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
  public Set<Vehicle<?>> getVehiclesAsUnmodifialable(final TrafficEdge edge)
    throws IllegalArgumentException {
    this.checkEdge(edge);
    return Collections.unmodifiableSet(this.getVehicles(edge));
  }

  /**
   * @param vehicle
   * @return
   * @throws IllegalArgumentException
   */
  @Override
  public TrafficEdge getEdgeFor(final Vehicle<?> vehicle) throws IllegalArgumentException {
    if (vehicle == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "vehicle"));
    }
    return this.vehicleToEdges.get(vehicle);
  }

  /**
   * Returns the registered vehicles for a given edge.
   *
   * @param edge Edge to check
   * @param from Start node of the given edge
   * @param to End node of the given edge
   * @return A list with vehicles registered on the given edge
   * @throws IllegalArgumentException
   */
  @Override
  public Set<Vehicle<?>> getVehiclesFor(TrafficEdge edge,
    TrafficNode from,
    TrafficNode to)
    throws IllegalArgumentException {
    Set<Vehicle<?>> registeredCars = edge.getVehicles();
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
  private Set<Vehicle<?>> getVehicles(final TrafficEdge edge) throws IllegalArgumentException {
    this.checkEdge(edge);
    if (edge.getVehicles() == null) {
      this.createVehicleSet(edge);
    }
    return edge.getVehicles();
  }

  /**
   * @param edge
   * @throws IllegalArgumentException
   */
  private void createVehicleSet(final TrafficEdge edge) throws IllegalArgumentException {
    DefaultGraphExtensions.checkEdge(edge);
    edge.setVehicles(new HashSet<Vehicle<?>>());
  }

  /**
   * Registers a car on a given edge.
   *
   * @param edge Edge to register the car on
   * @param from Start node of the given edge
   * @param to End node of the given edge
   * @param v Vehicle to register
   */
  @Override
  public void registerOnEdge(TrafficEdge edge,
    TrafficNode from,
    TrafficNode to,
    Vehicle<?> v) {
    String mapName;
    if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(v.getData().getType())) {
      mapName = "cars";
    } else {
      mapName = "bicycles";
    }
    Set<Vehicle<?>> vehicles = edge.getVehicles();
    if (vehicles == null) {
      edge.setVehicles(new HashSet<Vehicle<?>>(10));
    }
    edge.getVehicles().add(v);
  }

  /**
   * Unregisters a registered vehicle from the passed edge.
   *
   * @param edge Edge to unregister the car from
   * @param from Start node of the given edge
   * @param to End node of the given edge
   * @param v Vehicle to unregister
   */
  @Override
  public void unregisterFromEdge(TrafficEdge edge,
    TrafficNode from,
    TrafficNode to,
    Vehicle<?> v) {
    edge.getVehicles().remove(v);
  }

  @Override
  public void reset() {
    this.vehicleToEdges.clear();
    this.trafficRules.clear();
  }
}
