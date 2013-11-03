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

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.internal.DefaultTrafficEdge;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.DefaultTrafficNode;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleCallback;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleData;
import java.util.ArrayList;

/**
 * {@link TrafficRule} used for {@link Node}s that have just two edges. Hence a vehicle can pass <br>
 * the TrafficNode immediately
 * 
 * @author Marcus
 */
public class StraightForwardRule extends TrafficRule {
	/**
	 * contains for both {@link Edge}s the {@link Vehicle}s that are in there
	 */
	private final Map<Edge, TrafficRuleData> vehiclesInTrafficNode = new HashMap<>(2);

	/**
	 * contains for both {@link Edge}s the {@link Vehicle} that are waiting for getting in
	 */
	private final Map<Edge, Queue<TrafficRuleData>> vehiclesWaiting = new HashMap<>();

	/**
	 * Creates a {@link StraightForwardRule} for the passed {@link Node}.
	 * 
	 * @param node
	 *            the {@link Node} on which the {@link StraightForwardRule} will be applied
	 * @throws IllegalArgumentException
	 *             if argument 'node' is null
	 * @throws IllegalStateException
	 *             if argument 'node' hasn't exactly two edges
	 */
	public StraightForwardRule(final TrafficNode node) throws IllegalArgumentException, IllegalStateException {
		super(node);
		this.vehiclesInNode.put(this.getEdge1(), null);
		this.vehiclesInNode.put(this.getEdge2(), null);

		this.vehiclesWaiting.put(this.getEdge1(), new LinkedList<TrafficRuleData>());
		this.vehiclesWaiting.put(this.getEdge2(), new LinkedList<TrafficRuleData>());
	}

	@Override
	protected void checkNode(final TrafficNode node) throws IllegalStateException {
		if (node.getEdgeSet().size() != 2) {
			throw new IllegalStateException("Argument 'node' must have exactly 2 edges. This has "
					+ node.getEdgeSet().size() + ".");
		}
	}

	/**
	 * Registers a {@link Vehicle} and lets it in when no other vehicle is on this side of the road.
	 * 
	 * @param vehicle
	 *            the vehicle to register at this {@link StraightForwardRule}
	 * @param from
	 *            the {@link Edge} the {@link Vehicle} is coming from
	 * @param to
	 *            the {@link Edge} the {@link Vehicle} wants to follow
	 * @param callback
	 *            the {@link TrafficRuleCallback} that methods are executed on certain events
	 * @throws IllegalArgumentException
	 *             if at least one of the passed arguments is 'null'
	 * @throws UnsupportedOperationException
	 *             if argument 'from' references the same {@link Edge} as argument 'to' or if {@link Edge} 'from' or
	 *             {@link Edge} 'to' aren't linked with this {@link StraightForwardRule}'s {@link Node}
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@Override
	public void register(final Vehicle vehicle, final TrafficEdge from, final TrafficEdge to, final TrafficRuleCallback callback)
			throws IllegalArgumentException, UnsupportedOperationException {
		// if (from == to) {
		// throw new UnsupportedOperationException("Argument 'from' must not be the same TrafficEdge as argument 'to'.");
		// }
		if (!this.vehiclesInNode.containsKey(from)) {
			throw new UnsupportedOperationException("Edge 'from' isn't linked with this StraightForwardRule's node.");
		}
		if (!this.vehiclesInNode.containsKey(to)) {
			throw new UnsupportedOperationException("Edge 'to' isn't linked with this StraightForwardRule's node.");
		}
		final TrafficRuleData trafficRuleData = new TrafficRuleData(vehicle, from, to, callback);

		if (true || (this.vehiclesInNode.get(from) == null)) {
			// Let vehicle in
			this.vehiclesInNode.put(from, trafficRuleData);
			callback.onEnter();
			callback.onExit();
		} else {
			// Put vehicle in waiting
			if (this.vehiclesWaiting.get(from).offer(trafficRuleData)) {
				throw new RuntimeException("Vehicle couldn't be offered to waiting queue.");
			}
		}
	}

	/**
	 * No special logic is executed here. {@link Vehicle}s are just brought trough.
	 * 
	 * @param simulationEventList
	 *            the {@link SimulationEventList}
	 */
	@Override
	public void update(final EventList simulationEventList) {
		/*
		 * Let vehicles out
		 */
		final TrafficRuleData inNodeTrafficRuleData1 = this.vehiclesInNode.get(this.getEdge1());
		final TrafficRuleData inNodeTrafficRuleData2 = this.vehiclesInNode.get(this.getEdge2());
		if (inNodeTrafficRuleData1 != null) {
			this.vehiclesInNode.put(inNodeTrafficRuleData1.getFrom(), null);
			inNodeTrafficRuleData1.getCallback().onExit();
		}
		if (inNodeTrafficRuleData2 != null) {
			this.vehiclesInNode.put(inNodeTrafficRuleData2.getFrom(), null);
			inNodeTrafficRuleData2.getCallback().onExit();
		}

		/*
		 * Let vehicles in from the waiting list
		 */
		final TrafficRuleData waitingTrafficRuleData1 = this.vehiclesWaiting.get(this.getEdge1()).poll();
		final TrafficRuleData waitingTrafficRuleData2 = this.vehiclesWaiting.get(this.getEdge2()).poll();
		if (waitingTrafficRuleData1 != null) {
			this.vehiclesInNode.put(waitingTrafficRuleData1.getFrom(), waitingTrafficRuleData1);
			waitingTrafficRuleData1.getCallback().onEnter();
		}
		if (waitingTrafficRuleData2 != null) {
			this.vehiclesInNode.put(waitingTrafficRuleData2.getFrom(), waitingTrafficRuleData2);
			waitingTrafficRuleData2.getCallback().onEnter();
		}
	}

	/**
	 * Returns the first {@link Edge} of the {@link Node} on which the {@link StraightForwardRule} is applied.
	 * 
	 * @return the first {@link Edge} of the {@link Node} on which the {@link StraightForwardRule} is applied
	 */
	private TrafficEdge getEdge1() {
		return this.getNode().getEdge(0);
	}

	/**
	 * Returns the second {@link Edge} of the {@link Node} on which the {@link StraightForwardRule} is applied.
	 * 
	 * @return the second {@link Edge} of the {@link Node} on which the {@link StraightForwardRule} is applied
	 */
	private TrafficEdge getEdge2() {
		return this.getNode().getEdge(1);
	}
}
