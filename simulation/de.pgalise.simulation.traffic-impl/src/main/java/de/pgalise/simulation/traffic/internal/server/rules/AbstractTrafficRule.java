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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleCallback;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleData;
import java.util.ArrayList;

/**
 * Interface for traffic rules
 * 
 * @param <D> 
 * @author Marcus
 */
public abstract class AbstractTrafficRule<
	D extends VehicleData> extends AbstractIdentifiable implements 
TrafficRule {
	private static final Logger log = LoggerFactory
			.getLogger(AbstractTrafficRule.class);

	private final TrafficNode node;
	private TrafficGraph  graph;
	private TrafficRuleData data;

	public AbstractTrafficRule(final TrafficNode node,TrafficGraph graph, TrafficRuleData data) throws RuntimeException {
		if (node == null) {
			throw new IllegalArgumentException(
					ExceptionMessages.getMessageForNotNull("node"));
		}
		this.checkNode(node);
		this.node = node;
		this.graph = graph;
		this.data = data;
	}

	public void setData(TrafficRuleData data) {
		this.data = data;
	}

	@Override
	public TrafficRuleData getData() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

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
	@Override
	public void register(final Vehicle<?> vehicle,
			final TrafficNode from, final TrafficNode to, final TrafficRuleCallback callback)
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
		
		TrafficEdge edgeFrom = null;
		log.debug("#Edges conntected to node 'from': "
				+ graph.edgesOf(from).size());
		for (final TrafficEdge edge : graph.edgesOf(from)) {
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

		TrafficEdge edgeTo = null;
		for (final TrafficEdge edge : graph.edgesOf(to)) {
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
	@Override
	public TrafficNode getNode() {
		return this.node;
	}

	@Override
	public void setGraph(
		TrafficGraph  graph) {
		this.graph = graph;
	}

	@Override
	public TrafficGraph  getGraph() {
		return graph;
	}

}
