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
 
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.city.NavigationNode;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Trip class for traffic.
 * 
 * @author Timo
 */
@Entity
public class DefaultTrafficTrip extends AbstractIdentifiable implements TrafficTrip {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -8918889050049103380L;

	/**
	 * Start node
	 */
	@ManyToOne
	private NavigationNode startNode;

	/**
	 * Target node
	 */
	@ManyToOne
	private NavigationNode targetNode;

	/**
	 * Start timestamp
	 */
	private long startTime;

	/**
	 * Default
	 */
	protected DefaultTrafficTrip() {
	}

	/**
	 * Constructor
	 * 
	 * @param startNode
	 *            Start node
	 * @param targetNode
	 *            Target node
	 * @param startTime  
	 */
	public DefaultTrafficTrip(NavigationNode startNode, NavigationNode targetNode, long startTime) {
		this.startNode = startNode;
		this.targetNode = targetNode;
		this.startTime = startTime;
	}

	public NavigationNode getStartNode() {
		return this.startNode;
	}

	public void setStartNode(NavigationNode startNode) {
		this.startNode = startNode;
	}

	public NavigationNode getTargetNode() {
		return this.targetNode;
	}

	public void setTargetNode(NavigationNode targetNode) {
		this.targetNode = targetNode;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}
