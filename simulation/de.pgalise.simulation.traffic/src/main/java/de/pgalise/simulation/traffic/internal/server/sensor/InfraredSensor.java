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
package de.pgalise.simulation.traffic.internal.server.sensor;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.sensorFramework.AbstractSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.TrafficSensorTypeEnum;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;

/**
 * Class to generate an infrared sensor
 *
 * @author Marcus
 * @author Andreas
 * @version 1.0
 */
public class InfraredSensor extends AbstractSensor<TrafficEvent<?>, InfraredSensorData> implements TrafficSensor<InfraredSensorData> {

	/**
	 * Logger
	 */
	private static Logger log = LoggerFactory.getLogger(InfraredSensor.class);

	/**
	 * InfraredInterferer interferer
	 */
	private InfraredInterferer interferer;

	/**
	 * According vehicle
	 */
	private Vehicle<? extends BusData> vehicle;

	private boolean sendData;

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param vehicle According vehicle
	 * @param position Position of the sensor
	 * @param updateLimit Update limit
	 * @param interferer InfraredInterferer
	 */
	public InfraredSensor(Long id,
		final Output output,
		final Vehicle<? extends BusData> vehicle,
		final JaxRSCoordinate position,
		final int updateLimit,
		final InfraredInterferer interferer) {
		super(id,
			output,
			updateLimit,
			new InfraredSensorData());
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"interferer"));
		}

		this.interferer = interferer;
	}

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param vehicle According vehicle
	 * @param position Position of the sensor
	 * @param interferer InfraredInterferer
	 */
	public InfraredSensor(Long id,
		final Output output,
		final Vehicle<? extends BusData> vehicle,
		final JaxRSCoordinate position,
		final InfraredInterferer interferer) {
		this(id,
			output,
			vehicle,
			position,
			1,
			interferer);
	}

	public InfraredInterferer getInterferer() {
		return this.interferer;
	}

	public void setInterferer(final InfraredInterferer interferer) throws IllegalArgumentException {
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"interferer"));
		}
		this.interferer = interferer;
	}

	/**
	 * Transmits the Amount of Passengers traveling in the bus
	 *
	 * @param eventList
	 */
	@Override
	public void transmitUsageData(EventList<TrafficEvent<?>> eventList) {

		// Get random passengers
		int passengers = this.vehicle.getData().getCurrentPassengerCount();
		int passengersToSend = 0;

		// Interferer
		for (int i = 0; i < passengers; i++) {
			passengersToSend += this.interferer.interfere(passengersToSend);
		}

		// log
		log.debug(String.format(
			"Send amount of passengers (%s) by sensor %s (vehicle: %s)",
			passengersToSend,
			getId(),
			vehicle.getId()));

		// Send
		this.getOutput().transmitDouble((double) passengersToSend);
		this.getOutput().transmitDouble((double) 0);
		this.getOutput().transmitByte((byte) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
	}

	@Override
	public void logValueToSend(EventList eventList) {
		if (this.vehicle.getVehicleState() == VehicleStateEnum.DRIVING) {
			// Get random passengers
			int passengers = this.vehicle.getData().getCurrentPassengerCount();
			int passengersToSend = 0;

			// Interferer
			for (int i = 0; i < passengers; i++) {
				passengersToSend += this.interferer.interfere(passengersToSend);
			}

			log.debug(
				"Send amount of passengers (" + passengersToSend + ") by sensor " + this.
				getId()
				+ " (vehicle: " + vehicle.getId() + ")");
		} else {
			log.warn("No infrared sensor data will be send, vehicle " + vehicle.
				getId() + " is not driving");
		}
	}

	/**
	 * Transmits the data if the bus is not null, the bus is driving and passed a
	 * busstation
	 *
	 * @param eventList List of SimulationEvents
	 * @exception IllegalStateException if the getVehicle returns null
	 */
	@Override
	protected void transmitData(EventList eventList) {
		if (this.vehicle == null) {
			throw new IllegalStateException(ExceptionMessages.getMessageForNotNull(
				"vehicle"));
		}

		if (this.vehicle.getPosition() != null) {

			if (VehicleStateEnum.UPDATEABLE_VEHICLES.contains(this.vehicle.
				getVehicleState()) && this.vehicle.getVehicleState() != VehicleStateEnum.NOT_STARTED) {
				if (sendData) {
					sendData = false;
					super.transmitData(eventList);
				}
			}
		}
	}

	public Vehicle<? extends BusData> getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle<? extends BusData> vehicle) {
		this.vehicle = vehicle;
	}

	public void sendDataOnNextUpdate() {
		sendData = true;
	}

	@Override
	public SensorType getSensorType() {
		return TrafficSensorTypeEnum.INFRARED;
	}
}
