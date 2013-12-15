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

import de.pgalise.simulation.shared.event.EventType;
import java.util.List;

import de.pgalise.simulation.shared.event.EventTypeEnum;

/**
 * Event for generating new weather data. Should be thrown by 23:59 of the day.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 30, 2012)
 */
@Deprecated
public class NewDayEvent extends WeatherEvent {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 4490969355241939710L;

	/**
	 * List with Strategies to modify weather data
	 */
	private final List<WeatherEventTypeEnum> strategyList;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the event
	 * @param list
	 *            List with Strategies to modify weather data
	 */
	public NewDayEvent(List<WeatherEventTypeEnum> list, long timestamp) {
		super(WeatherEventTypeEnum.NEW_DAY_EVENT,
			timestamp,
			0, -1);
		this.strategyList = list;
	}

	public List<WeatherEventTypeEnum> getStrategyList() {
		return this.strategyList;
	}
}
