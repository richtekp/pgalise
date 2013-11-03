/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.event;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public enum TrafficEventTypeEnum implements EventType {
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
	 * Creates an attraction
	 */
	ATTRACTION_TRAFFIC_EVENT(13, AttractionTrafficEvent.class),
	
	/**
	 * Deletes vehicles
	 */
	DELETE_VEHICLES_EVENT(4, DeleteVehiclesEvent.class),

	/**
	 * Creates a road barrier
	 */
	ROAD_BARRIER_TRAFFIC_EVENT(14, RoadBarrierTrafficEvent.class);

	/**
	 * ID of the event
	 */
	private final int EVENT_ID;

	/**
	 * class which implements this event.
	 */
	private final Class<?> IMPLEMENTATION_CLASS;

	/**
	 * maps simulation event type ids integers to {@link SimulationEventTypeEnum}
	 */
	private static Map<Integer, TrafficEventTypeEnum> SIMULATION_EVENT_TYPE_IDS;

	/**
	 * Returns simulation event type ids.
	 * 
	 * @return simulation event type ids.
	 */
	private static Map<Integer, TrafficEventTypeEnum> getSimulationEventTypeIds() {
		if (TrafficEventTypeEnum.SIMULATION_EVENT_TYPE_IDS == null) {
			TrafficEventTypeEnum.SIMULATION_EVENT_TYPE_IDS = new HashMap<>();
		}
		return TrafficEventTypeEnum.SIMULATION_EVENT_TYPE_IDS;
	}

	public static Map<Integer, TrafficEventTypeEnum> getSimulationEventTypeMapAsUnmodifiable() {
		return Collections.unmodifiableMap(TrafficEventTypeEnum.SIMULATION_EVENT_TYPE_IDS);
	}
	
	@SuppressWarnings("LeakingThisInConstructor")
	private TrafficEventTypeEnum(final int simulationEventID, final Class<?> implementationClass)
			throws IllegalArgumentException, IllegalStateException {
		if (simulationEventID < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("simulationEventID", true));
		}
		if (TrafficEventTypeEnum.getSimulationEventTypeIds().containsKey(simulationEventID)) {
			throw new IllegalStateException("Argument 'simulationEventID' = " + simulationEventID
					+ " is already registered");
		}
		this.EVENT_ID = simulationEventID;
		TrafficEventTypeEnum.getSimulationEventTypeIds().put(simulationEventID, this);
		this.IMPLEMENTATION_CLASS = implementationClass;
	}

	public int getSimulationEventID() {
		return EVENT_ID;
	}

	@Override
	public Class<?> getImplementationClass() {
		return IMPLEMENTATION_CLASS;
	}
}
