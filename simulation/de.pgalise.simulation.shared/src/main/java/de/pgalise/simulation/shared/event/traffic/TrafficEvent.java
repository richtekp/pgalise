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
 
package de.pgalise.simulation.shared.event.traffic;

import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;

/**
 * Superclass for all traffic events.
 * 
 * @author Timo
 */
public abstract class TrafficEvent extends SimulationEvent {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -8313844787624266589L;
	private int responsibleServer;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the event
	 * @param eventType
	 *            Event type
	 */
	public TrafficEvent(Long id, SimulationEventTypeEnum eventType) {
		super(id, eventType);
	}

	public TrafficEvent(SimulationEventTypeEnum eventType) {
		super(eventType);
	}
	
	public void setResponsibleServer(int serverId) {
		this.responsibleServer = serverId;
	}
	
	public int getResponsibleServer() {
		return this.responsibleServer;
	}
}
