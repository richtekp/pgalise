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

import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;

/**
 * Event for modifying the current weather
 * 
 * @author Andreas Rehfeldt
 * @version 1.1 (Aug 30, 2012)
 */
public class ChangeWeatherEvent extends WeatherEvent {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 2452503673933039973L;

	/**
	 * Timestamp of the max/min
	 */
	private long timestamp;

	/**
	 * Weather event to modify weather data
	 */
	private final WeatherEventEnum event;

	/**
	 * Specified value
	 */
	private Float value;

	/**
	 * Specified duration
	 */
	private Float duration;

	/**
	 * Constructor
	 * 
	 * @param strategy
	 *            Strategy to modify weather data
	 * @param eventTime 
	 * @param value
	 *            Specified value
	 * @param duration
	 *            Specified duration
	 */
	public ChangeWeatherEvent(WeatherEventEnum strategy, Float value, long eventTime, Float duration) {
		super(SimulationEventTypeEnum.CHANGE_WEATHER_EVENT);
		this.event = strategy;
		this.value = value;
		this.timestamp = eventTime;
		this.duration = duration;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public Float getDuration() {
		return duration;
	}

	public void setDuration(Float duration) {
		this.duration = duration;
	}

	public WeatherEventEnum getEvent() {
		return event;
	}

}
