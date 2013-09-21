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
 
package de.pgalise.simulation.traffic.event;

import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;

/**
 * Superclass for all traffic events.
 * 
 * @author Timo
 */
public abstract class AbstractTrafficEvent extends AbstractEvent implements TrafficEvent {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -8313844787624266589L;
	private TrafficServerLocal responsibleServer;
	private final long simulationTime;
	private final long elapsedTime;

	/**
	 * Constructor
	 * 
	 * @param eventType
	 *            Event type
	 * @param responsibleServer  
	 */
	public AbstractTrafficEvent(TrafficServerLocal responsibleServer, long simulationTime, long elapsedTime) {
		this.responsibleServer = responsibleServer;
		this.simulationTime = simulationTime;
		this.elapsedTime = elapsedTime;
	}

	@Override
	public long getSimulationTime() {
		return simulationTime;
	}

	@Override
	public long getElapsedTime() {
		return elapsedTime;
	}
	
	@Override
	public void setResponsibleServer(TrafficServerLocal serverId) {
		this.responsibleServer = serverId;
	}
	
	@Override
	public TrafficServerLocal getResponsibleServer() {
		return this.responsibleServer;
	}
}
