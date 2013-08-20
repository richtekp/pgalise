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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle.State;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;
import javax.vecmath.Vector2d;

/**
 * Class to generate an infrared sensor
 * 
 * @author Marcus
 * @author Andreas
 * @version 1.0
 */
public final class InfraredSensor extends Sensor {
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
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param vehicle
	 *            According vehicle
	 * @param position
	 *            Position of the sensor
	 * @param updateLimit
	 *            Update limit
	 * @param interferer
	 *            InfraredInterferer
	 */
	public InfraredSensor(final Output output, final Object sensorId, final Vehicle<? extends BusData> vehicle,
			final Vector2d position, final int updateLimit, final InfraredInterferer interferer) {
		super(output, sensorId, position, updateLimit);
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferer"));
		}

		this.interferer = interferer;
	}

	/**
	 * Constructor
	 * 
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param vehicle
	 *            According vehicle
	 * @param position
	 *            Position of the sensor
	 * @param interferer
	 *            InfraredInterferer
	 */
	public InfraredSensor(final Output output, final Object sensorId, final Vehicle<? extends BusData> vehicle,
			final Vector2d position, final InfraredInterferer interferer) {
		this(output, sensorId, vehicle, position, 1, interferer);
	}

	public InfraredInterferer getInterferer() {
		return this.interferer;
	}

	@Override
	public SensorType getSensorType() {
		return SensorType.INFRARED;
	}

	public void setInterferer(final InfraredInterferer interferer) throws IllegalArgumentException {
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferer"));
		}
		this.interferer = interferer;
	}

	/**
	 * Transmits the Amount of Passengers traveling in the bus
	 */
	@Override
	public void transmitUsageData(SimulationEventList eventList) {
		if (this.vehicle.getPosition() != null) {
			this.setPosition(this.vehicle.getPosition());
		}

		// Get random passengers
		int passengers = this.vehicle.getData().getCurrentPassengerCount();
		int passengersToSend = 0;

		// Interferer
		for (int i = 0; i < passengers; i++) {
			passengersToSend += this.interferer.interfere(passengersToSend);
		}

		// log
		log.debug("Send amount of passengers (" + passengersToSend + ") by sensor "+this.getSensorId()+
				" (vehicle: " + vehicle.getId()+")");

		// Send
		this.getOutput().transmitDouble((double) passengersToSend);
		this.getOutput().transmitDouble((double) 0);
		this.getOutput().transmitByte((byte) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
	}

	@Override
	public void logValueToSend(SimulationEventList eventList) {
		if (this.vehicle.getState() == State.DRIVING) {
			// Get random passengers
			int passengers = this.vehicle.getData().getCurrentPassengerCount();
			int passengersToSend = 0;

			// Interferer
			for (int i = 0; i < passengers; i++) {
				passengersToSend += this.interferer.interfere(passengersToSend);
			}

			log.debug("Send amount of passengers (" + passengersToSend + ") by sensor "+this.getSensorId()+
					" (vehicle: " + vehicle.getId()+")");
		} else {
			log.warn("No infrared sensor data will be send, vehicle " + vehicle.getId() + " is not driving");
		}
	}

	/**
	 * Transmits the data if the bus is not null, the bus is driving and passed a busstation
	 * 
	 * @param eventList
	 *            List of SimulationEvents
	 * @exception IllegalStateException
	 *                if the getVehicle returns null
	 */
	@Override
	protected void transmitData(SimulationEventList eventList) {
		if (this.vehicle == null) {
			throw new IllegalStateException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}

		if (this.vehicle.getPosition() != null) {
			this.setPosition(this.vehicle.getPosition());

			if (State.UPDATEABLE_VEHICLES.contains(this.vehicle.getState()) && this.vehicle.getState() != State.NOT_STARTED) {
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
}
