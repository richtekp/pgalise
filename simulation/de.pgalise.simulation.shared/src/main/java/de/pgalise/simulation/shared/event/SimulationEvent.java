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
 
package de.pgalise.simulation.shared.event;

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;

/**
 * Superclass for every simulation event. It contains a uniqe
 * {@link UUID} and a value of {@link SimulationEventTypeEnum}, this is
 * useful for serializing and deserializing. This class can not be 
 * an interface nor be abstract, because google guice can not handle this.
 * 
 * @author Timo
 */
public class SimulationEvent extends AbstractIdentifiable implements Event<SimulationEventTypeEnum> {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -7362721454716905390L;

	/**
	 * Event type
	 */
	private SimulationEventTypeEnum eventType;

	/**
	 * Constructor
	 * 
	 * @param eventType
	 *            Event type
	 */
	public SimulationEvent(SimulationEventTypeEnum eventType) {
		this.eventType = eventType;
	}

	public SimulationEvent(Long id, SimulationEventTypeEnum eventType) {
		super(id);
		this.eventType = eventType;
	}

	@Override
	public SimulationEventTypeEnum getType() {
		return eventType;
	}

	public void setType(SimulationEventTypeEnum eventType) {
		this.eventType = eventType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SimulationEvent other = (SimulationEvent) obj;
		if (eventType != other.eventType) {
			return false;
		}
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
}
