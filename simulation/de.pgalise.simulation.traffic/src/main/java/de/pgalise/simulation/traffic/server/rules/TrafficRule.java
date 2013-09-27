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
 
package de.pgalise.simulation.traffic.server.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.SimulationComponent;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.TrafficGraph;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * Interface for traffic rules
 * 
 * @author Marcus
 */
public abstract class TrafficRule extends AbstractIdentifiable implements SimulationComponent<Event> {
	private static final Logger log = LoggerFactory
			.getLogger(TrafficRule.class);

	private final NavigationNode node;
	private TrafficGraph<?> graph;

	public TrafficRule(final NavigationNode node,TrafficGraph<?> graph) throws RuntimeException {
		if (node == null) {
			throw new IllegalArgumentException(
					ExceptionMessages.getMessageForNotNull("node"));
		}
		this.checkNode(node);
		this.node = node;
		this.graph = graph;
	}

	protected abstract void checkNode(final NavigationNode node) throws RuntimeException;

	/**
	 * Registers a {@link Vehicle} with the passed arguments. The arguments
	 * specify from which {@link Edge} the {@link Vehicle} is coming from and to
	 * which {@link Edge} it plans to go. Additionally a
	 * {@link TrafficRuleCallback} is passed which methods will be invoked when
	 * the {@link Vehicle} can enter the {@link Node} and also when it can leave
	 * the {@link Node}.
	 * 
	 * @param vehicle
	 *            the {@link Vehicle} to get through the traffic rule
	 * @param from
	 *            the {@link Edge} where the {@link Vehicle} is coming from
	 * @param to
	 *            the {@link Edge} where the {@link Vehicle} plans to go
	 * @param callback
	 *            A {@link TrafficRuleCallback} which methods are invoked on
	 *            defined events. In these methods the passed {@link Vehicle}
	 *            can change its position.
	 * @throws IllegalArgumentException
	 *             if any of the passed arguments is 'null'
	 * @throws IllegalStateException
	 *             some implementations will throw it if the passed arguments
	 *             are in conflict with the concrete {@link TrafficRule} (i.e.
	 *             'from' and 'to' are the same {@link Edge} what implies that
	 *             the {@link Vehicle} wants to turn around and turning around
	 *             is forbidden on the concrete {@link TrafficRule}).
	 */
	public abstract void register(final Vehicle<? extends VehicleData> vehicle,
			final NavigationEdge<?,?> from, final NavigationEdge<?,?> to, final TrafficRuleCallback callback)
			throws IllegalArgumentException, IllegalStateException;

	/**
	 * Registers a {@link Vehicle} with the passed arguments. The arguments
	 * specify from which {@link Edge} the {@link Vehicle} is coming from and to
	 * which {@link Edge} it plans to go. Additionally a
	 * {@link TrafficRuleCallback} is passed which methods will be invoked when
	 * the {@link Vehicle} can enter the {@link Node} and also when it can leave
	 * the {@link Node}.
	 * 
	 * @param vehicle
	 *            the {@link Vehicle} to get through the traffic rule
	 * @param from
	 *            the {@link Edge} where the {@link Vehicle} is coming from
	 * @param to
	 *            the {@link Edge} where the {@link Vehicle} plans to go
	 * @param callback
	 *            A {@link TrafficRuleCallback} which methods are invoked on
	 *            defined events. In these methods the passed {@link Vehicle}
	 *            can change its position.
	 * @throws IllegalArgumentException
	 *             if any of the passed arguments is 'null'
	 * @throws IllegalStateException
	 *             some implementations will throw it if the passed arguments
	 *             are in conflict with the concrete {@link TrafficRule} (i.e.
	 *             'from' and 'to' are the same {@link Edge} what implies that
	 *             the {@link Vehicle} wants to turn around and turning around
	 *             is forbidden on the concrete {@link TrafficRule}).
	 */
	public void register(final Vehicle<? extends VehicleData> vehicle,
			final NavigationNode from, final NavigationNode to, final TrafficRuleCallback callback)
			throws IllegalArgumentException, IllegalStateException {
		if (from == null) {
			throw new IllegalArgumentException(
					"Parameter 'from' must not be null");
		}
		if (to == null) {
			throw new IllegalArgumentException(
					"Parameter 'to' must not be null");
		}

		log.debug("from:"+ from.getId());
		log.debug("to:"+ to.getId());
		log.debug("this.node:"+ this.node.getId());
		
		NavigationEdge<?,?> edgeFrom = null;
		log.debug("#Edges conntected to node 'from': "
				+ graph.edgesOf(from).size());
		for (final NavigationEdge<?,?> edge : graph.edgesOf(from)) {
			log.debug("Node conntected with edge: "+edge.getOpposite(from).getId());
			if (edge.getOpposite(from).getId().equals(this.getNode().getId())) {
				edgeFrom = edge;
			}
		}
		if (edgeFrom == null) {
			throw new IllegalStateException(
					"Node 'from' isn't linked with this "
							+ this.getClass().getName()
							+ "'s node trough an edge.");
		}

		NavigationEdge<?,?> edgeTo = null;
		for (final NavigationEdge<?,?> edge : graph.edgesOf(to)) {
			if (edge.getOpposite(to).getId().equals(this.getNode().getId())) {
				edgeTo = edge;
			}
		}
		if (edgeTo == null) {
			throw new IllegalStateException("Node 'to' isn't linked with this "
					+ this.getClass().getName() + "'s node trough an edge.");
		}
		this.register(vehicle, edgeFrom, edgeTo, callback);
	}

	/**
	 * Returns the {@link Node} on which this {@link TrafficRule} is applied.
	 * 
	 * @return the {@link Node} on which this {@link TrafficRule} is applied
	 */
	public NavigationNode getNode() {
		return this.node;
	}

	public void setGraph(
		TrafficGraph<?> graph) {
		this.graph = graph;
	}

	public TrafficGraph<?> getGraph() {
		return graph;
	}
}
