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
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.service.IdGenerator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.event.DeleteVehiclesEvent;
import de.pgalise.simulation.traffic.event.RoadBarrierTrafficEvent;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.model.RoadBarrier;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;

/**
 * The event handler removes vehicles with the given List of UUIDs from the
 * server. The class are used by the {@link DeleteVehiclesEvent}.
 *
 * @author Andreas
 * @version 1.0
 * @param <D>
 */
public class CreateRoadBarrierEventHandler<D extends VehicleData> extends AbstractTrafficEventHandler<D, RoadBarrierTrafficEvent<D>> {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(
		CreateRoadBarrierEventHandler.class);

	/**
	 * Simulation event type
	 */
	private static final EventType type = TrafficEventTypeEnum.ROAD_BARRIER_TRAFFIC_EVENT;

	/**
	 * Graph
	 */
	private TrafficGraph graph;
	private IdGenerator idGenerator;

	/**
	 * Constructor
	 */
	public CreateRoadBarrierEventHandler() {

	}

	public CreateRoadBarrierEventHandler(TrafficGraph graph,
		IdGenerator idGenerator,
		TrafficServerLocal<?> server) {
		this.graph = graph;
		this.idGenerator = idGenerator;
		this.server = server;
	}

	/**
	 * Init the handler
	 */
	@Override
	public void init(TrafficServerLocal<RoadBarrierTrafficEvent<D>> server) {
		this.server = server;
	}

	/**
	 * Traffic server
	 */
	private TrafficServerLocal<?> server;

	@Override
	public EventType getTargetEventType() {
		return CreateRoadBarrierEventHandler.type;
	}

	/**
	 * Handle blocked road and calculate new path for vehicles if their path
	 * contains the given node
	 *
	 * @param vehicle Vehicle
	 * @param node Node that is blocked by {@link RoadBarrier}
	 */
	private void calculateNewWay(Vehicle<? extends VehicleData> vehicle,
		NavigationNode node) {
		if (VehicleTypeEnum.VEHICLES_FOR_INDIVIDUAL_TRAFFIC.contains(vehicle.
			getData().getType())
			&& vehicle.getPath().contains(node)
			&& (!vehicle.getNextNode().equals(node) || !vehicle.getCurrentNode().
			equals(node))) {

			/*
			 * Calculate new path for the vehicles for individual traffic
			 */
//			log.debug("RoadBarrier: Calculate new path for " + vehicle.getData().getType() + " " + vehicle.getId());
//			Node start = vehicle.getCurrentNode();
			TrafficNode start = vehicle.getNextNode();

			// Check destination
			TrafficNode destination;
			int t = 0;
			do {
				t++;
				destination = vehicle.getNodePath().
					get(vehicle.getNodePath().size() - t);
			} while (destination.equals(node));

			List<TrafficEdge> path;
			path = this.server.getShortestPath(vehicle.getCurrentNode(),
				vehicle.getNextNode());

			List<TrafficEdge> newPath = this.server.getShortestPath(start,
				destination);

			for (TrafficEdge e : newPath) {
				path.add(e);
			}

			// set new path
			vehicle.setPath(path);

			// Log
//			log.debug("ROAD_BARRIER_TRAFFIC_EVENT set new " + vehicle.getData().getType() + " path");
		} else if (vehicle.getData().getType().equals(VehicleTypeEnum.BUS) && vehicle.
			getNodePath().contains(node)
			&& (!vehicle.getNextNode().equals(node) || !vehicle.getCurrentNode().
			equals(node))) {
			/*
			 * For busses
			 */

//			log.debug("RoadBarrier: Calculate new path for " + vehicle.getData().getType() + " " + vehicle.getId());
			BusData bus = ((BusData) vehicle.getData());

			// Node is bus stop
			Map<BusStop, TrafficNode> busStops = bus.getBusStops();
			if (busStops.get(node.getId()) != null) {
				// Delete bus stop
				busStops.remove(node.getId());
				bus.getBusStopOrder().remove(node.getId());
			}

			/*
			 * Calculate new path for the vehicles for individual traffic
			 */
			// To avoid errors, sets the current node for start
//			Node start = vehicle.getCurrentNode();
			TrafficNode start = vehicle.getNextNode();

			// Check destination
			int nextIndex = bus.getLastBusStop() + 1;
			if (nextIndex < bus.getBusStopOrder().size()) {
				TrafficNode destination = busStops.get(bus.getBusStopOrder().get(
					nextIndex));

				List<TrafficEdge> path;
				path = this.server.getShortestPath(vehicle.getCurrentNode(),
					vehicle.getNextNode());

				// Path to the next bus stop
				List<TrafficEdge> newPath = this.server.getShortestPath(start,
					destination);

				// Path of the remaining bus stops
				List<BusStop> remainingStops = bus.getBusStopOrder().subList(nextIndex,
					(bus.getBusStopOrder().size() - 1));
				List<TrafficEdge> pathRemainingStops = this.server.getBusRoute(
					remainingStops);
				if (pathRemainingStops != null) {
					for (TrafficEdge e : pathRemainingStops) {
						newPath.add(e);
					}
				}

				for (TrafficEdge e : newPath) {
					path.add(e);
				}

				// set new path
				vehicle.setPath(path);

				// Log
//				log.debug("ROAD_BARRIER_TRAFFIC_EVENT set new bus path");
			}
		}
	}

	@Override
	public void handleEvent(RoadBarrierTrafficEvent e) {
		log.info("Processing ROAD_BARRIER_TRAFFIC_EVENT: startID=" + e.getNodeID()
			+ " ; startTime=" + e.getRoadBarrierStartTimestamp() + " ; endTime=" + e.
			getRoadBarrierEndTimestamp());

		// Set graph
		this.graph = this.server.getGraph();

		// Blocked node
		TrafficNode blockedNode = e.getNodeID();

		// Change the node on the graph
		Set<TrafficEdge> edges = this.getAllEdges(blockedNode);
		if (!edges.isEmpty()) {

			// Sets the RoadBarrier to the server
			this.server.addNewRoadBarrier(new RoadBarrier(
				idGenerator.getNextId(),
				blockedNode,
				edges,
				e.getRoadBarrierStartTimestamp(),
				e
				.getRoadBarrierEndTimestamp()));

			// Remove from graph
			this.removeEdgesFromGraph(edges);

			// New Path for all vehicles with this edge
			List<ScheduleItem> vehicleItems = this.server.getScheduler().
				getExpiredItems(e.getRoadBarrierEndTimestamp());
			for (ScheduleItem item : vehicleItems) {
				Vehicle<?> vehicle = item.getVehicle();
				this.calculateNewWay(vehicle,
					blockedNode);
			}
		}
	}

	/**
	 * Get all edges (entering and leaving) from the node
	 *
	 * @param node Node
	 * @return Set<Edge>
	 */
	private Set<TrafficEdge> getAllEdges(TrafficNode node) {
		Set<TrafficEdge> edges = new HashSet<>();

		edges.addAll(graph.incomingEdgesOf(node));
		edges.addAll(graph.outgoingEdgesOf(node));

		return edges;
	}

	/**
	 * Remove all edges from graph
	 *
	 * @param edges List of edges to remove
	 */
	private void removeEdgesFromGraph(Set<TrafficEdge> edges) {
		for (TrafficEdge edge : edges) {
			this.graph.removeEdge(edge);
		}

		// log
//		log.debug("ROAD_BARRIER_TRAFFIC_EVENT removed edges: " + edges.size());
	}
}
