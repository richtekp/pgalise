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

import java.io.Serializable;

/**
 * Helper for weather events
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 11, 2012)
 */
public class WeatherEventHelper implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 2360717547724733452L;

	/**
	 * Weather event
	 */
	private WeatherEventEnum event;

	/**
	 * Timestamp of the event
	 */
	private long timestamp;

	/**
	 * Specified value
	 */
	private float value;

	/**
	 * Specified duration
	 */
	private float duration;

	/**
	 * Constructor
	 * 
	 * @param event
	 *            Weather event
	 * @param timestamp
	 *            Timestamp of the event
	 * @param duration
	 *            Duration
	 * @param value
	 *            Value
	 */
	public WeatherEventHelper(WeatherEventEnum event, long timestamp, float value, float duration) {
		this.event = event;
		this.timestamp = timestamp;
		this.value = value;
		this.duration = duration;
	}

	public WeatherEventEnum getEvent() {
		return this.event;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public float getValue() {
		return this.value;
	}

	public void setEvent(WeatherEventEnum event) {
		this.event = event;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

}
