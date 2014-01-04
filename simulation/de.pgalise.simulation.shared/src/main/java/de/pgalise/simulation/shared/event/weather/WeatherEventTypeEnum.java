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

/**
 * Enum for strategies to modify weather data
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 30, 2012)
 */
public enum WeatherEventTypeEnum implements WeatherEventType {
	CITYCLIMATE(0,ChangeWeatherEvent.class, 0,60), 
	COLDDAY(2,ChangeWeatherEvent.class, 0,60), 
	HOTDAY(3,ChangeWeatherEvent.class, 0,60), 
	RAINDAY(4,ChangeWeatherEvent.class, 0,60), 
	REFERENCECITY(1,ChangeWeatherEvent.class, 0,60), 
	STORMDAY(5,ChangeWeatherEvent.class, 0,60),
	/**
	 * Prepares a new day
	 */
	@Deprecated
	NEW_DAY_EVENT(6, NewDayEvent.class, 0,60);

	/**
	 * maps sensor type ids integers to {@link SensorType}
	 */
	private static Map<Integer, WeatherEventType> WEATHER_EVENT_IDS;

	/**
	 * Day event types
	 */
	public static final EnumSet<WeatherEventTypeEnum> DAY_EVENTS = EnumSet.of(HOTDAY, COLDDAY, RAINDAY, STORMDAY);

	/**
	 * Simulation event types
	 */
	public static final EnumSet<WeatherEventTypeEnum> SIMULATION_EVENTS = EnumSet.of(CITYCLIMATE, REFERENCECITY);

	/**
	 * Constructor
	 * 
	 * @param eventID
	 *            Weather event ID
	 */
	WeatherEventTypeEnum(int eventID, Class<?> implementingClass, int minValue, int maxValue) {
		this.eventID = eventID;
		this.implementingClass = implementingClass;
		this.minValue = minValue;
		this.maxValue = maxValue;
		WeatherEventTypeEnum.getWeatherEventEnumIds().put(eventID, this);
	}

	public int getEventID() {
		return eventID;
	}

	/**
	 * the integer sensor type id
	 */
	private final int eventID;
	
	private Class<?> implementingClass;
	
	private float minValue, maxValue;

	/**
	 * @return
	 */
	public int getEventId() {
		return this.eventID;
	}

	/**
	 * @return
	 */
	public static Map<Integer, WeatherEventType> getWeatherEventEnumIdsAsUnmodifiable() {
		return Collections.unmodifiableMap(WeatherEventTypeEnum.getWeatherEventEnumIds());
	}

	/**
	 * @return
	 */
	private static Map<Integer, WeatherEventType> getWeatherEventEnumIds() {
		if (WeatherEventTypeEnum.WEATHER_EVENT_IDS == null) {
			WeatherEventTypeEnum.WEATHER_EVENT_IDS = new HashMap<>();
		}
		return WeatherEventTypeEnum.WEATHER_EVENT_IDS;
	}

	@Override
	public Class<?> getImplementationClass() {
		return implementingClass;
	}

	@Override
	public float getMaxValue() {
		return maxValue;
	}

	@Override
	public float getMinValue() {
		return minValue;
	}
}
