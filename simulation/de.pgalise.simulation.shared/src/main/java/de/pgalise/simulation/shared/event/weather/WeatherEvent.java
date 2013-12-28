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
package de.pgalise.simulation.shared.event.weather;

import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.shared.event.EventType;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 * Weather type super class.
 *
 * @author Timo
 * @version 1.0
 */
@ManagedBean
@ApplicationScoped
public abstract class WeatherEvent extends AbstractEvent {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -4700226520167357639L;

	/**
	 * Weather type
	 */
	private WeatherEventType type;

	/**
	 * Timestamp of the type
	 */
	private long timestamp;

	/**
	 * Specified duration
	 */
	private long duration;

	protected WeatherEvent() {
		super();
	}

	public WeatherEvent(
		Long id,
		WeatherEventType type,
		long timestamp,
		long duration) {
		super(id);
		this.type = type;
		this.timestamp = timestamp;
		this.duration = duration;
	}

	@Override
	public WeatherEventType getType() {
		return this.type;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setType(WeatherEventType type) {
		this.type = type;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
}
