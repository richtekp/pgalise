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

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.GPSSensorData;
import de.pgalise.simulation.sensorFramework.AbstractSensor;
import de.pgalise.simulation.traffic.TrafficSensorTypeEnum;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import javax.faces.bean.ManagedBean;

/**
 * Class to generate a GPS sensor
 *
 * @author Marcus
 * @author Mischa
 * @version 1.1
 */
@ManagedBean
public class GpsSensor extends AbstractSensor<TrafficEvent<?>, GPSSensorData> {

	/**
	 * Search signal time for GPS connection
	 */
	private static final int SEARCH_SIGNAL_TIME = 45;

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(GpsSensor.class);
	private static final long serialVersionUID = 1L;

	/**
	 * GPS interferer
	 */
	private GpsInterferer interferer;

	/**
	 * Vehicle
	 */
	private Vehicle<? extends VehicleData> vehicle;

	/**
	 * Option that shows if the sensor has a signal
	 */
	private boolean hasSignal = false;

	/**
	 * Counter for the search signal
	 */
	private int searchSignalCounter = 0;

	public GpsSensor() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param vehicle According vehicle
	 * @param updateLimit Update limit
	 * @param interferer GPS interferer
	 */
	public GpsSensor(final Output output,
		final Vehicle<? extends VehicleData> vehicle,
		final int updateLimit,
		final GpsInterferer interferer) {

		super(output,
			TrafficSensorTypeEnum.GPS,
			updateLimit,
			new GPSSensorData());
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
	 * @param vehicle According vehicle
	 * @param interferer GPS interferer
	 */
	public GpsSensor(Output output,
		Vehicle<? extends VehicleData> vehicle,
		final GpsInterferer interferer) {
		this(output,
			vehicle,
			1,
			interferer);
	}

	public GpsInterferer getInterferer() {
		return this.interferer;
	}

	public Vehicle<? extends VehicleData> getVehicle() {
		return this.vehicle;
	}

	public boolean isHasSignal() {
		return this.hasSignal;
	}

	public void setHasSignal(boolean hasSignal) {
		this.hasSignal = hasSignal;
	}

	@Override
	public void setActivated(final boolean activated) {
		super.setActivated(activated);
		if (!this.isActivated()) {
			this.hasSignal = true;
			this.searchSignalCounter = 0;
		}
	}

	public void setInterferer(final GpsInterferer interferer) throws IllegalArgumentException {
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"interferer"));
		}
		this.interferer = interferer;
	}

	public void setVehicle(Vehicle<? extends VehicleData> vehicle) {
		if (vehicle == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"vehicle"));
		}
		this.vehicle = vehicle;
	}

	/**
	 * GPS data are transmitted by sending 2 double values depending on the State
	 * of the car.
	 *
	 * @param eventList List of SimulationEvents
	 */
	@Override
	public void transmitUsageData(EventList eventList) {
		// log.debug("Transmitting sensor data for sensor "+this.getSensorId());
		if (++this.searchSignalCounter >= GpsSensor.SEARCH_SIGNAL_TIME) {
			this.hasSignal = true;
		}

		/* Send data if the vehicle is driving */
		// Use interferer
		final Coordinate interferedPos = this.interferer.interfere(this.vehicle.
			getPosition(),
			this.vehicle.getPosition(),
			eventList.getTimestamp());

		// convert to geo location
		final Coordinate geoLocation = interferedPos;

		log.debug(
			"Send position of sensor " + this.getId() + ": " + geoLocation.getX() + ", "
			+ geoLocation.getY());

		// Send data
		this.getOutput().transmitDouble(geoLocation.getX());
		this.getOutput().transmitDouble(geoLocation.getY());
		this.getOutput().transmitByte((byte) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
	}

	/**
	 * Transmits the data if the car is not null and when the vehicle has a
	 * position.
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
			this.getSensorData().setPosition(this.vehicle.getPosition());

			if ((VehicleStateEnum.UPDATEABLE_VEHICLES.contains(vehicle.
				getVehicleState()) && vehicle.getVehicleState() != VehicleStateEnum.NOT_STARTED)
				|| vehicle.getVehicleState() == VehicleStateEnum.IN_TRAFFIC_RULE) {
				super.transmitData(eventList);
			}
		}
	}

	@Override
	public void logValueToSend(EventList eventList) {
		if (this.vehicle.getVehicleState() == VehicleStateEnum.DRIVING) {

			// log
			// GpsSensor.log.debug("Send: x: " + this.vehicle.getPosition().getX() + " y:"
			// + this.vehicle.getPosition().getY());
			// Use interferer
			final Coordinate interferedPos = this.interferer.interfere(this.vehicle.
				getPosition(),
				this.vehicle.getPosition(),
				eventList.getTimestamp());

			// convert to geolocation
			final Coordinate geoLocation = interferedPos;

			GpsSensor.log.debug("Send lat: " + geoLocation.getX() + " long: "
				+ geoLocation.getY() + " of vehicle " + vehicle.getId());
		}
	}
}
