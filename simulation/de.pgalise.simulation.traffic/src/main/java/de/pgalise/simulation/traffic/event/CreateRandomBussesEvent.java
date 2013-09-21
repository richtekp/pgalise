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
 
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.event;

import de.pgalise.simulation.shared.event.EventType;
import java.util.List;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventTypeEnum;

/**
 * The create random busses event will create random busses
 * for the given bus linves.
 * @author Lena
 */
public class CreateRandomBussesEvent extends AbstractTrafficEvent {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -6389011067355327482L;

	/**
	 * List with bus lines
	 */
	private List<String> busLines;

	/**
	 * Agency name
	 */
	private String agencyName;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the event
	 * @param busIDList
	 *            List with bus lines
	 * @param agencyName
	 *            Agency name
	 */
	public CreateRandomBussesEvent(TrafficServerLocal responsibleServer, long timestamp, long elapsedTime, List<String> busLines, String agencyName) {
		super(responsibleServer, timestamp, elapsedTime);
		this.busLines = busLines;
		this.agencyName = agencyName;
	}

	public List<String> getBusLines() {
		return this.busLines;
	}

	public void setBusLines(List<String> busLines) {
		this.busLines = busLines;
	}

	/**
	 * @return the agencyName
	 */
	public String getAgencyName() {
		return agencyName;
	}

	/**
	 * @param agencyName
	 *            the agencyName to set
	 */
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((agencyName == null) ? 0 : agencyName.hashCode());
		result = prime * result + ((busLines == null) ? 0 : busLines.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CreateRandomBussesEvent other = (CreateRandomBussesEvent) obj;
		if (agencyName == null) {
			if (other.agencyName != null) {
				return false;
			}
		} else if (!agencyName.equals(other.agencyName)) {
			return false;
		}
		if (busLines == null) {
			if (other.busLines != null) {
				return false;
			}
		} else if (!busLines.equals(other.busLines)) {
			return false;
		}
		return true;
	}

	@Override
	public EventType getType() {
		return TrafficEventTypeEnum.CREATE_RANDOM_BUSSES_EVENT;
	}
}
