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
 
package de.pgalise.simulation.traffic.internal.server.rules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleCallback;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleData;

/**
 * Class for Priority road realization
 * 
 * @param <D> 
 * @author Marcus
 */
public class PriorityRoad<D extends VehicleData> extends AbstractTrafficRule<D> {
	private static final long serialVersionUID = 1L;

	/**
	 * TrafficGraphExtensions used for further calculations
	 */
	private final TrafficGraphExtensions trafficEdgeExtensions;

	/**
	 * time in millis a car should stay in priority road while passing
	 */
	private final int millisInNode;

	/**
	 * random number generator for choosing letting vehicles in
	 */
	private final Random random;

	/**
	 * used to determine the priority vehicles that have the give way should wait before entering the PriorityRoad
	 */
	private final double giveAwayPriority;

	/**
	 * holds all edges of the node on which the PriorityRoad is set on
	 */
	private Map<TrafficEdge , Boolean> edges;

	/**
	 * holds all TrafficRuleData elements (Vehicle, Callbacks, etc.) being currently in the PriorityRoad
	 */
	private Map<TrafficEdge , TrafficRuleData> inNode;

	/**
	 * holds all TrafficRuleData elements (Vehicle, Callbacks, etc.) being currently waiting to get in the PriorityRoad
	 */
	private Map<TrafficEdge , Queue<TrafficRuleData>> outNode;

	/**
	 * Creates an PriorityRoad
	 * @param node the node on which the PriorityRoad shall be applied
	 * @param graph 
	 * @param randomSeedService for generating random numbers used for internal logic
	 * @param trafficEdgeExtensions 
	 * @param millisInNode the time in millis a Vehicle is in the PriorityRoad while passing (must be greater than 0)
	 * @param giveAwayPriority the priority a vehicle that has the give way is willing to wait (must not be between 0 and 1)
	 * @throws IllegalArgumentException if at least one of the passed arguments is 'null' or millisInNode or giveAwayPriority isn't set correctly 
	 * @throws IllegalStateException if the number of edges is not 3 or 4 or if not exactly 2 edges are marked as priority roads
	 */
	public PriorityRoad(final TrafficNode node, TrafficGraph graph, final RandomSeedService randomSeedService,
			final TrafficGraphExtensions trafficEdgeExtensions, final int millisInNode, final double giveAwayPriority)
			throws IllegalArgumentException, IllegalStateException {
		super(node, graph,null);
		if (randomSeedService == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("randomSeedService"));
		}
		if (trafficEdgeExtensions == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("trafficEdgeExtensions"));
		}
		if (millisInNode <= 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("millisInNode", false));
		}
		if ((giveAwayPriority <= 0) || (giveAwayPriority >= 1)) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForMustBetween("giveAwayPriority", 0, 1,
					false, false));
		}
		this.random = new Random(randomSeedService.getSeed(PriorityRoad.class.getName()));
		this.trafficEdgeExtensions = trafficEdgeExtensions;
		this.millisInNode = millisInNode;
		this.giveAwayPriority = giveAwayPriority;
	}

	public PriorityRoad(final TrafficNode node, TrafficGraph graph, final RandomSeedService randomSeedService,
			final TrafficGraphExtensions trafficEdgeExtensions) throws IllegalArgumentException, IllegalStateException {
		this(node, graph, randomSeedService, trafficEdgeExtensions, 2000, 0.15);
	}

	/**
	 * Checks whether the rule can be applied on the node.
	 * 
	 * @param node
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException  
	 */
	@Override
	public void checkNode(final TrafficNode node) throws IllegalArgumentException, IllegalStateException {
		final int edgeSize = getGraph().edgesOf(node).size();
		if ((edgeSize < 3) || (edgeSize > 4)) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForMustBetween("node.getEdgeSet().size()",
					3, 5));
		}
		this.edges = new HashMap<>();
		this.inNode = new HashMap<>();
		this.outNode = new HashMap<>();
		int priorityRoadCounter = 0;
		for (final TrafficEdge  edge : getGraph().edgesOf(node)) {
			Boolean isPriorityRoad = this.trafficEdgeExtensions.isPriorityRoad(edge);
			if ((isPriorityRoad != null) && isPriorityRoad) {
				priorityRoadCounter++;
			} else {
				isPriorityRoad = false;
			}
			this.inNode.put(edge, null);
			this.outNode.put(edge, new LinkedList<TrafficRuleData>());
			this.edges.put(edge, isPriorityRoad);
		}
		if (priorityRoadCounter != 2) {
			throw new IllegalStateException("Node '" + node + "' has more or less than 2 priority roads.");
		}
	}

	/**
	 * Registered the vehicle on this PriorityRoad.
	 */
	@Override
	public void register(final Vehicle<?> vehicle, final TrafficEdge  from, final TrafficEdge  to, final TrafficRuleCallback callback)
			throws IllegalArgumentException {
		if (from == to) {
			throw new IllegalArgumentException("Turning around is not allowed here.");
		}

		final TrafficRuleData trafficRuleData = new TrafficRuleData(vehicle.getData(), from, to, callback);

		if (this.edges.get(trafficRuleData.getFrom()) == true) {
			if (this.inNode.get(trafficRuleData.getFrom()) == null) {
				this.inNode.put(trafficRuleData.getFrom(), trafficRuleData);
				if (!trafficRuleData.getCallback().onEnter()) {
					// vehicle WANTS to wait
					this.outNode.get(trafficRuleData.getFrom()).offer(trafficRuleData);
				}
			} else {
				// vehicle has to wait
				this.outNode.get(trafficRuleData.getFrom()).offer(trafficRuleData);
			}
		} else {
			// vehicle has to wait, because it comes from a non priority road
			this.outNode.get(trafficRuleData.getFrom()).offer(trafficRuleData);
		}
	}

	/**
	 * Updates this PrioritzyRoad.
	 */
	@Override
	public void update(final EventList<VehicleEvent> simulationEventList) {
		// Try to wipe out
		for (final TrafficEdge  edge : this.inNode.keySet()) {
			final TrafficRuleData trafficRuleData = this.inNode.get(edge);
			if (trafficRuleData != null) {
				trafficRuleData.getCallback().onExit();
				this.inNode.put(edge, null);
			}
		}

		// Try to let in
		final int priorityCarsWaiting = this.getPriorityCarsWaiting();
		if (priorityCarsWaiting == 0) {
			for (final TrafficEdge  edge : this.outNode.keySet()) {
				final TrafficRuleData trafficRuleData = this.outNode.get(edge).peek();
				if (trafficRuleData != null) {
					if (trafficRuleData.getCallback().onEnter()) {
						this.inNode.put(edge, trafficRuleData);
					}
				}
			}
		} else {
			for (final TrafficEdge  edge : this.outNode.keySet()) {
				if (this.edges.get(edge) == true) {
					final TrafficRuleData trafficRuleData = this.outNode.get(edge).peek();
					if (trafficRuleData != null) {
						if (trafficRuleData.getCallback().onEnter()) {
							this.inNode.put(edge, trafficRuleData);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns the number of cars waiting to get in this PriorityRoad.
	 * @return the number of cars waiting to get in this PriorityRoad
	 */
	private int getPriorityCarsWaiting() {
		int result = 0;
		for (final TrafficEdge  edge : this.outNode.keySet()) {
			if (this.edges.get(edge) == true) {
				Queue<TrafficRuleData> queue = this.outNode.get(edge);
				result += queue.size();
			}
		}
		return result;
	}
}
