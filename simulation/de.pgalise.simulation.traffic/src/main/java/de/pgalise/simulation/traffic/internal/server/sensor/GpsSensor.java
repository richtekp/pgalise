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
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.sensorFramework.AbstractSensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;

/**
 * Class to generate a GPS sensor
 * 
 * @author Marcus
 * @author Mischa
 * @version 1.1
 */
public class GpsSensor extends AbstractSensor<TrafficEvent> {

	/**
	 * Search signal time for GPS connection
	 */
	private static final int SEARCH_SIGNAL_TIME = 45;

	/**
	 * Logger
	 */
	private static Logger log = LoggerFactory.getLogger(GpsSensor.class);
	private static final long serialVersionUID = 1L;

	/**
	 * GPS interferer
	 */
	private GpsInterferer interferer;

	/**
	 * Sensor type
	 */
	private SensorType type;

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
	
	/**
	 * Constructor
	 * 
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param vehicle
	 *            According vehicle
	 * @param sensor
	 *            Type of the sensor
	 * @param mapper
	 *            GPS mapper of the graph
	 * @param updateLimit
	 *            Update limit
	 * @param interferer
	 *            GPS interferer
	 */
	public GpsSensor(final Output output, final Vehicle<? extends VehicleData> vehicle,
			final int updateLimit, final SensorType sensor, Coordinate position, final GpsInterferer interferer) {

		super(output, position, updateLimit);
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferer"));
		}
		this.interferer = interferer;
		this.setSensorType(sensor);
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
	 * @param sensor
	 *            Type of the sensor
	 * @param position 
	 * @param interferer
	 *            GPS interferer
	 */
	public GpsSensor(Output output, Vehicle<? extends VehicleData> vehicle, SensorTypeEnum sensor,
			final Coordinate position, final GpsInterferer interferer) {
		this(output, vehicle, 1, sensor, position, interferer);
	}

	/**
	 * Checks the type of vehicle and sets the corresponding type
	 */
	public void checkAndSetType() {
		if (this.getVehicle() != null) {
			/*
			 * INFO: You have to add new traffic vehicles in that section
			 */

			switch (this.vehicle.getData().getType()) {
				case TRUCK:
					this.setSensorType(SensorTypeEnum.GPS_TRUCK);
					break;
				case CAR:
					this.setSensorType(SensorTypeEnum.GPS_CAR);
					break;
				case BUS:
					this.setSensorType(SensorTypeEnum.GPS_BUS);
					break;
				case MOTORCYCLE:
					this.setSensorType(SensorTypeEnum.GPS_MOTORCYCLE);
					break;
				case BIKE:
					this.setSensorType(SensorTypeEnum.GPS_BIKE);
				default:
					break;
			}
		}
	}

	public GpsInterferer getInterferer() {
		return this.interferer;
	}

	@Override
	public SensorType getSensorType() {
		return this.type;
	}

	public Vehicle<? extends VehicleData> getVehicle() {
		return this.vehicle;
	}

	public boolean hasSignal() {
		return this.hasSignal;
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
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferer"));
		}
		this.interferer = interferer;
	}

	public void setSensorType(SensorType sensor) {
		this.type = sensor;
	}

	public void setVehicle(Vehicle<? extends VehicleData> vehicle) {
		if (vehicle == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}
		this.vehicle = vehicle;
		this.checkAndSetType();
	}

	/**
	 * GPS data are transmitted by sending 2 double values depending on the State of the car.
	 * 
	 * @param eventList
	 *            List of SimulationEvents
	 */
	@Override
	public void transmitUsageData(EventList eventList) {
		// log.debug("Transmitting sensor data for sensor "+this.getSensorId());
		if (++this.searchSignalCounter >= GpsSensor.SEARCH_SIGNAL_TIME) {
			this.hasSignal = true;
		}

		/* Send data if the vehicle is driving */

		// Use interferer
		final Coordinate interferedPos = this.interferer.interfere(this.vehicle.getPosition(),
				this.vehicle.getPosition(), eventList.getTimestamp());

		// convert to geo location
		final Coordinate geoLocation = interferedPos;

		log.debug("Send position of sensor " + this.getId()+ ": " + geoLocation.x + ", "
				+ geoLocation.y);

		// Send data
		this.getOutput().transmitDouble(geoLocation.x);
		this.getOutput().transmitDouble(geoLocation.y);
		this.getOutput().transmitByte((byte) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
	}

	/**
	 * Transmits the data if the car is not null and when the vehicle has a position.
	 * 
	 * @param eventList
	 *            List of SimulationEvents
	 * @exception IllegalStateException
	 *                if the getVehicle returns null
	 */
	@Override
	protected void transmitData(EventList eventList) {
		if (this.vehicle == null) {
			throw new IllegalStateException(ExceptionMessages.getMessageForNotNull("vehicle"));
		}

		if (this.vehicle.getPosition() != null) {
			this.setPosition(this.vehicle.getPosition());

			if ((VehicleStateEnum.UPDATEABLE_VEHICLES.contains(vehicle.getVehicleState()) && vehicle.getVehicleState() != VehicleStateEnum.NOT_STARTED)
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
			final Coordinate interferedPos = this.interferer.interfere(this.vehicle.getPosition(),
					this.vehicle.getPosition(), eventList.getTimestamp());

			// convert to geolocation
			final Coordinate geoLocation = interferedPos;

			GpsSensor.log.debug("Send lat: " + geoLocation.x + " long: "
					+ geoLocation.y + " of vehicle " + vehicle.getId());
		}
	}
}
