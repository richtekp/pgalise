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
import de.pgalise.simulation.shared.event.traffic.AttractionTrafficEvent;
import de.pgalise.simulation.shared.event.traffic.CreateBussesEvent;
import de.pgalise.simulation.shared.event.traffic.CreateRandomBussesEvent;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehiclesEvent;
import de.pgalise.simulation.shared.event.traffic.CreateVehiclesEvent;
import de.pgalise.simulation.shared.event.traffic.DeleteVehiclesEvent;
import de.pgalise.simulation.shared.event.traffic.RoadBarrierTrafficEvent;
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
public enum SimulationEventTypeEnum implements EventType {
	/**
	 * Creates new random vehicles
	 */
	CREATE_RANDOM_VEHICLES_EVENT(0, CreateRandomVehiclesEvent.class),

	/**
	 * Creates new busses
	 */
	CREATE_BUSSES_EVENT(1, CreateBussesEvent.class),

	/**
	 * Creates random busses
	 */
	CREATE_RANDOM_BUSSES_EVENT(2, CreateRandomBussesEvent.class),

	/**
	 * Create new vehicles
	 */
	CREATE_VEHICLES_EVENT(3, CreateVehiclesEvent.class),

	/**
	 * Deletes vehicles
	 */
	DELETE_VEHICLES_EVENT(4, DeleteVehiclesEvent.class),

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
	PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT(12, ChangeEnergyConsumptionEvent.class),

	/**
	 * Creates an attraction
	 */
	ATTRACTION_TRAFFIC_EVENT(13, AttractionTrafficEvent.class),

	/**
	 * Creates a road barrier
	 */
	ROAD_BARRIER_TRAFFIC_EVENT(14, RoadBarrierTrafficEvent.class);

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
	private static Map<Integer, SimulationEventTypeEnum> SIMULATION_EVENT_TYPE_IDS;

	/**
	 * Returns simulation event type ids.
	 * 
	 * @return simulation event type ids.
	 */
	private static Map<Integer, SimulationEventTypeEnum> getSimulationEventTypeIds() {
		if (SimulationEventTypeEnum.SIMULATION_EVENT_TYPE_IDS == null) {
			SimulationEventTypeEnum.SIMULATION_EVENT_TYPE_IDS = new HashMap<>();
		}
		return SimulationEventTypeEnum.SIMULATION_EVENT_TYPE_IDS;
	}

	public static Map<Integer, SimulationEventTypeEnum> getSimulationEventTypeMapAsUnmodifiable() {
		return Collections.unmodifiableMap(SimulationEventTypeEnum.SIMULATION_EVENT_TYPE_IDS);
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
	private SimulationEventTypeEnum(final int simulationEventID, final Class<?> implementationClass)
			throws IllegalArgumentException, IllegalStateException {
		if (simulationEventID < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("simulationEventID", true));
		}
		if (SimulationEventTypeEnum.getSimulationEventTypeIds().containsKey(simulationEventID)) {
			throw new IllegalStateException("Argument 'simulationEventID' = " + simulationEventID
					+ " is already registered");
		}
		SimulationEventTypeEnum.getSimulationEventTypeIds().put(this.simulationEventID = simulationEventID, this);
		this.implementationClass = implementationClass;
	}

	public int getSimulationEventID() {
		return simulationEventID;
	}

	public Class<?> getImplementationClass() {
		return implementationClass;
	}
}
