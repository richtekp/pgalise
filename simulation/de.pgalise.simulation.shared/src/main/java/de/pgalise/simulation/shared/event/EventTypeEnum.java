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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.pgalise.simulation.shared.event.energy.ChangeEnergyConsumptionEvent;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
import de.pgalise.simulation.shared.event.weather.NewDayEvent;
import de.pgalise.simulation.shared.exception.ExceptionMessages;

/**
 * Enum for simulation events. This is useful to achieve good GSON deserialization. Literal and classes are saved for
 * better use in operation client side code and gson deserialization.
 * 
 * @author Timo
 */
@SuppressWarnings("deprecation")
public enum EventTypeEnum implements EventType {


	/**
	 * Changes the current weahter conditions
	 */
	CHANGE_WEATHER_EVENT(5, ChangeWeatherEvent.class),
	/**
	 * Prepares a new day
	 */
	@Deprecated
	NEW_DAY_EVENT(6, NewDayEvent.class),

	/**
	 * Influences the energy consumption
	 */
	PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT(12, ChangeEnergyConsumptionEvent.class);

	/**
	 * ID of the event
	 */
	private final int simulationEventID;

	/**
	 * class which implements this event.
	 */
	private final Class<?> implementationClass;

	/**
	 * maps simulation event type ids integers to {@link SimulationEventTypeEnum}
	 */
	private static Map<Integer, EventTypeEnum> SIMULATION_EVENT_TYPE_IDS;

	/**
	 * Returns simulation event type ids.
	 * 
	 * @return simulation event type ids.
	 */
	private static Map<Integer, EventTypeEnum> getSimulationEventTypeIds() {
		if (EventTypeEnum.SIMULATION_EVENT_TYPE_IDS == null) {
			EventTypeEnum.SIMULATION_EVENT_TYPE_IDS = new HashMap<>();
		}
		return EventTypeEnum.SIMULATION_EVENT_TYPE_IDS;
	}

	public static Map<Integer, EventTypeEnum> getSimulationEventTypeMapAsUnmodifiable() {
		return Collections.unmodifiableMap(EventTypeEnum.SIMULATION_EVENT_TYPE_IDS);
	}

	/**
	 * Constructors
	 * 
	 * @param simulationEventID
	 *            ID of the event
	 * @param implementationClass
	 *            class which implements this event.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private EventTypeEnum(final int simulationEventID, final Class<?> implementationClass)
			throws IllegalArgumentException, IllegalStateException {
		if (simulationEventID < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("simulationEventID", true));
		}
		if (EventTypeEnum.getSimulationEventTypeIds().containsKey(simulationEventID)) {
			throw new IllegalStateException("Argument 'simulationEventID' = " + simulationEventID
					+ " is already registered");
		}
		EventTypeEnum.getSimulationEventTypeIds().put(this.simulationEventID = simulationEventID, this);
		this.implementationClass = implementationClass;
	}

	public int getSimulationEventID() {
		return simulationEventID;
	}

	public Class<?> getImplementationClass() {
		return implementationClass;
	}
}
