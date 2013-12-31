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

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * Trip class for traffic.
 * 
 * @author Timo
 */
@Entity
public class TrafficTrip extends AbstractIdentifiable {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -8918889050049103380L;

	/**
	 * Start node
	 */
	@ManyToOne
	private TrafficNode startNode;

	/**
	 * Target node
	 */
	@ManyToOne
	private TrafficNode targetNode;

	/**
	 * Start timestamp
	 */
	private long startTime;
	
	@Transient
	private Vehicle<?> vehicle;

	/**
	 * Default
	 */
	protected TrafficTrip() {
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
	public TrafficTrip(TrafficNode startNode, TrafficNode targetNode, long startTime) {
		this.startNode = startNode;
		this.targetNode = targetNode;
		this.startTime = startTime;
	}

	public TrafficNode getStartNode() {
		return this.startNode;
	}

	public void setStartNode(TrafficNode startNode) {
		this.startNode = startNode;
	}

	public TrafficNode getTargetNode() {
		return this.targetNode;
	}

	public void setTargetNode(TrafficNode targetNode) {
		this.targetNode = targetNode;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setVehicle(
		Vehicle<?> vehicle) {
		this.vehicle = vehicle;
	}

	public Vehicle<?> getVehicle() {
		return vehicle;
	}
}
