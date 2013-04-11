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

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import de.pgalise.simulation.shared.sensor.SensorType;

/**
 * Enum for strategies to modify weather data
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 30, 2012)
 */
public enum WeatherEventEnum {
	CITYCLIMATE(0), COLDDAY(2), HOTDAY(3), RAINDAY(4), REFERENCECITY(1), STORMDAY(5);

	/**
	 * maps sensor type ids integers to {@link SensorType}
	 */
	private static Map<Integer, WeatherEventEnum> WEATHER_EVENT_IDS;

	/**
	 * Day event types
	 */
	public static final EnumSet<WeatherEventEnum> DAY_EVENTS = EnumSet.of(HOTDAY, COLDDAY, RAINDAY, STORMDAY);

	/**
	 * Simulation event types
	 */
	public static final EnumSet<WeatherEventEnum> SIMULATION_EVENTS = EnumSet.of(CITYCLIMATE, REFERENCECITY);

	/**
	 * Constructor
	 * 
	 * @param eventID
	 *            Weather event ID
	 */
	WeatherEventEnum(int eventID) {
		this.eventID = eventID;
		WeatherEventEnum.getWeatherEventEnumIds().put(eventID, this);
	}

	public int getEventID() {
		return eventID;
	}

	/**
	 * the integer sensor type id
	 */
	private final int eventID;

	/**
	 * @return
	 */
	public int getEventId() {
		return this.eventID;
	}

	/**
	 * @return
	 */
	public static Map<Integer, WeatherEventEnum> getWeatherEventEnumIdsAsUnmodifiable() {
		return Collections.unmodifiableMap(WeatherEventEnum.getWeatherEventEnumIds());
	}

	/**
	 * @return
	 */
	private static Map<Integer, WeatherEventEnum> getWeatherEventEnumIds() {
		if (WeatherEventEnum.WEATHER_EVENT_IDS == null) {
			WeatherEventEnum.WEATHER_EVENT_IDS = new HashMap<>();
		}
		return WeatherEventEnum.WEATHER_EVENT_IDS;
	}
}
