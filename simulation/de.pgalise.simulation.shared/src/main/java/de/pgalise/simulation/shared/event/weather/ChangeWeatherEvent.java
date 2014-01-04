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

import javax.faces.bean.ManagedBean;

/**
 * Event for modifying the current weather
 *
 * @author Andreas Rehfeldt
 * @version 1.1 (Aug 30, 2012)
 */
@ManagedBean
public class ChangeWeatherEvent extends ValueWeatherEvent {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 2452503673933039973L;
	private WeatherEventType eventType;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param eventType
	 * @param value Specified value
	 * @param timestamp
	 * @param duration Specified duration
	 */
	public ChangeWeatherEvent(Long id, WeatherEventType eventType,
		float value,
		long timestamp,
		long duration) {
		super(id,
			timestamp,
			duration,
			value);
		this.eventType = eventType;
	}

	@Override
	public WeatherEventType getType() {
		return eventType;
	}

}
