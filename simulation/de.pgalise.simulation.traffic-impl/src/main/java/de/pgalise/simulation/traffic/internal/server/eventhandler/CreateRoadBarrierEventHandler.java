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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;
import de.pgalise.simulation.shared.event.traffic.DeleteVehiclesEvent;
import de.pgalise.simulation.shared.event.traffic.RoadBarrierTrafficEvent;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.model.RoadBarrier;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.server.scheduler.Item;

/**
 * The event handler removes vehicles with the given List of UUIDs from the server. The class are used by the
 * {@link DeleteVehiclesEvent}.
 * 
 * @author Andreas
 * @version 1.0
 */
public class CreateRoadBarrierEventHandler implements TrafficEventHandler {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CreateRoadBarrierEventHandler.class);

	/**
	 * Simulation event type
	 */
	private static final SimulationEventTypeEnum type = SimulationEventTypeEnum.ROAD_BARRIER_TRAFFIC_EVENT;

	/**
	 * Constructor
	 */
	public CreateRoadBarrierEventHandler() {

	}

	/**
	 * Init the handler
	 */
	@Override
	public void init(TrafficServerLocal server) {
		this.server = server;
	}

	/**
	 * Traffic server
	 */
	private TrafficServerLocal server;

	@Override
	public SimulationEventTypeEnum getType() {
		return CreateRoadBarrierEventHandler.type;
	}

	/**
	 * Handle blocked road and calculate new path for vehicles if their path contains the given node
	 * 
	 * @param vehicle
	 *            Vehicle
	 * @param node
	 *            Node that is blocked by {@link RoadBarrier}
	 */
	private void calculateNewWay(Vehicle<? extends VehicleData> vehicle, Node node) {
		if (VehicleTypeEnum.VEHICLES_FOR_INDIVIDUAL_TRAFFIC.contains(vehicle.getData().getType())
				&& vehicle.getPath().contains(node)
				&& (!vehicle.getNextNode().equals(node) || !vehicle.getCurrentNode().equals(node))) {

			/*
			 * Calculate new path for the vehicles for individual traffic
			 */
			
//			log.debug("RoadBarrier: Calculate new path for " + vehicle.getData().getType() + " " + vehicle.getId());

//			Node start = vehicle.getCurrentNode();
			Node start = vehicle.getNextNode();

			// Check destination
			Node destination;
			int t = 0;
			do {
				t++;
				destination = vehicle.getPath().getNodePath().get(vehicle.getPath().getNodeCount() - t);
			} while (destination.equals(node));
			
			Path path = new Path();
			path = this.server.getShortestPath(vehicle.getCurrentNode(), vehicle.getNextNode());

			Path newPath = this.server.getShortestPath(start, destination);
			
			for (Edge e : newPath.getEdgePath()) {
				path.add(e);
			}

			// set new path
			vehicle.setPath(path);

			// Log
//			log.debug("ROAD_BARRIER_TRAFFIC_EVENT set new " + vehicle.getData().getType() + " path");
		} else if (vehicle.getData().getType().equals(VehicleTypeEnum.BUS) && vehicle.getPath().contains(node)
				&& (!vehicle.getNextNode().equals(node) || !vehicle.getCurrentNode().equals(node))) {
			/*
			 * For busses
			 */
			
//			log.debug("RoadBarrier: Calculate new path for " + vehicle.getData().getType() + " " + vehicle.getId());

			BusData bus = ((BusData) vehicle.getData());

			// Node is bus stop
			Map<String, Node> busStops = bus.getBusStops();
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
			Node start = vehicle.getNextNode();

			// Check destination
			int nextIndex = bus.getLastBusStop() + 1;
			if (nextIndex < bus.getBusStopOrder().size()) {
				Node destination = busStops.get(bus.getBusStopOrder().get(nextIndex));

				Path path = new Path();
				path = this.server.getShortestPath(vehicle.getCurrentNode(), vehicle.getNextNode());
				
				// Path to the next bus stop
				Path newPath = this.server.getShortestPath(start, destination);

				// Path of the remaining bus stops
				List<String> remainingStops = bus.getBusStopOrder().subList(nextIndex,
						(bus.getBusStopOrder().size() - 1));
				Path pathRemainingStops = this.server.getBusRoute(remainingStops);
				if (pathRemainingStops != null) {
					for (Edge e : pathRemainingStops.getEdgePath()) {
						newPath.add(e);
					}
				}
				
				for (Edge e : newPath.getEdgePath()) {
					path.add(e);
				}

				// set new path
				vehicle.setPath(path);

				// Log
//				log.debug("ROAD_BARRIER_TRAFFIC_EVENT set new bus path");
			}
		}
	}

	/**
	 * Graph
	 */
	private Graph graph;

	@Override
	public void handleEvent(SimulationEvent event) {
		RoadBarrierTrafficEvent e = (RoadBarrierTrafficEvent) event;
		log.info("Processing ROAD_BARRIER_TRAFFIC_EVENT: startID=" + e.getNodeID()
				+ " ; startTime=" + e.getRoadBarrierStartTimestamp() + " ; endTime=" + e.getRoadBarrierEndTimestamp());

		// Set graph
		this.graph = this.server.getGraph();

		// Blocked node
		Node blockedNode = this.graph.getNode(e.getNodeID());

		// Change the node on the graph
		Set<Edge> edges = this.getAllEdges(blockedNode);
		if (!edges.isEmpty()) {

			// Sets the RoadBarrier to the server
			this.server.addNewRoadBarrier(new RoadBarrier(blockedNode, edges, e.getRoadBarrierStartTimestamp(), e
					.getRoadBarrierEndTimestamp()));

			// Remove from graph
			this.removeEdgesFromGraph(edges);

			// New Path for all vehicles with this edge
			List<Item> vehicleItems = this.server.getScheduler().getExpiredItems(e.getRoadBarrierEndTimestamp());
			for (Item item : vehicleItems) {
				Vehicle<? extends VehicleData> vehicle = item.getVehicle();
				this.calculateNewWay(vehicle, blockedNode);
			}
		}
	}

	/**
	 * Get all edges (entering and leaving) from the node
	 * 
	 * @param node
	 *            Node
	 * @return Set<Edge>
	 */
	private Set<Edge> getAllEdges(Node node) {
		Set<Edge> edges = new HashSet<>();

		edges.addAll(node.getEnteringEdgeSet());
		edges.addAll(node.getLeavingEdgeSet());

		return edges;
	}

	/**
	 * Remove all edges from graph
	 * 
	 * @param edges
	 *            List of edges to remove
	 */
	private void removeEdgesFromGraph(Set<Edge> edges) {
		for (Edge edge : edges) {
			this.graph.removeEdge(edge);
		}

		// log
//		log.debug("ROAD_BARRIER_TRAFFIC_EVENT removed edges: " + edges.size());
	}
}
