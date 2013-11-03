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

import de.pgalise.simulation.service.SimulationComponent;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * Interface for traffic rules
 * 
 * @author Marcus
 */
public abstract class TrafficRule implements SimulationComponent {
	private static final Logger log = LoggerFactory
			.getLogger(TrafficRule.class);

	void checkNode(final NavigationNode node) throws RuntimeException;

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
	public abstract void register(final V vehicle,
			final E from, final E to, final TrafficRuleCallback callback)
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
	public void register(final V vehicle,
			final N from, final N to, final TrafficRuleCallback callback)
			throws IllegalArgumentException, IllegalStateException ;

	/**
	 * Returns the {@link Node} on which this {@link TrafficRule} is applied.
	 * 
	 * @return the {@link Node} on which this {@link TrafficRule} is applied
	 */
	public N getNode() ;

	public void setGraph(
		TrafficGraph<N,E,D,V>  graph);

	public TrafficGraph<N,E,D,V>  getGraph() ;
	}
}
