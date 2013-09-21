/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.event.energy;

import de.pgalise.simulation.shared.event.EventTypeEnum;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
import de.pgalise.simulation.shared.event.weather.NewDayEvent;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public enum EnergyEventTypeEnum implements EnergyEventType {


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
	private static Map<Integer, EnergyEventTypeEnum> SIMULATION_EVENT_TYPE_IDS;

	/**
	 * Returns simulation event type ids.
	 * 
	 * @return simulation event type ids.
	 */
	private static Map<Integer, EnergyEventTypeEnum> getSimulationEventTypeIds() {
		if (EnergyEventTypeEnum.SIMULATION_EVENT_TYPE_IDS == null) {
			EnergyEventTypeEnum.SIMULATION_EVENT_TYPE_IDS = new HashMap<>();
		}
		return EnergyEventTypeEnum.SIMULATION_EVENT_TYPE_IDS;
	}

	public static Map<Integer, EnergyEventTypeEnum> getSimulationEventTypeMapAsUnmodifiable() {
		return Collections.unmodifiableMap(EnergyEventTypeEnum.SIMULATION_EVENT_TYPE_IDS);
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
	private EnergyEventTypeEnum(final int simulationEventID, final Class<?> implementationClass)
			throws IllegalArgumentException, IllegalStateException {
		if (simulationEventID < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("simulationEventID", true));
		}
		if (EnergyEventTypeEnum.getSimulationEventTypeIds().containsKey(simulationEventID)) {
			throw new IllegalStateException("Argument 'simulationEventID' = " + simulationEventID
					+ " is already registered");
		}
		EnergyEventTypeEnum.getSimulationEventTypeIds().put(this.simulationEventID = simulationEventID, this);
		this.implementationClass = implementationClass;
	}

	public int getSimulationEventID() {
		return simulationEventID;
	}

	@Override
	public Class<?> getImplementationClass() {
		return implementationClass;
	}
}
