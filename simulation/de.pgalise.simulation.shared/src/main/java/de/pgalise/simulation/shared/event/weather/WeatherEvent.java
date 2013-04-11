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

import java.util.UUID;

import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;

/**
 * Weather event super class.
 * 
 * @author Timo
 * @version 1.0
 */
public abstract class WeatherEvent extends SimulationEvent {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -4700226520167357639L;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the event
	 * @param eventType
	 *            Event type
	 */
	public WeatherEvent(UUID id, SimulationEventTypeEnum eventType) {
		super(id, eventType);
	}
}
