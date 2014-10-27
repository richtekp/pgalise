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

import de.pgalise.simulation.shared.entity.Identifiable;
import java.util.LinkedList;
import java.util.List;

/**
 * A list with {@link SimulationEvent}. This is useful, because the timestamp do
 * not need to be sent every time. It's also possible, that there is no
 * {@link SimulationEvent} in this list. In this case, the instance indicates
 * only the simulation time progress.
 *
 * @param <T>
 * @author Timo
 */
public class EventList<T extends Event> extends Identifiable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -94476170854232153L;

	/**
	 * List with events
	 */
	private List<T> eventList;

	/**
	 * Timestamp
	 */
	private long timestamp;

	/**
	 * Constructor
	 *
	 * @param eventList list with events
	 * @param timestamp current timestamp
	 * @param id ID of the event
	 */
	public EventList(Long id,
		List<T> eventList,
		long timestamp) {
		super(id);
		if (eventList != null) {
			this.eventList = eventList;
		} else {
			this.eventList = new LinkedList<>();
		}
		this.timestamp = timestamp;
	}

	public List<T> getEventList() {
		return this.eventList;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setEventList(List<T> eventList) {
		this.eventList = eventList;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventList == null) ? 0 : eventList.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		EventList<?> other = (EventList) obj;
		if (eventList == null) {
			if (other.eventList != null) {
				return false;
			}
		} else if (!eventList.equals(other.eventList)) {
			return false;
		}
		if (timestamp != other.timestamp) {
			return false;
		}
		return true;
	}
}
