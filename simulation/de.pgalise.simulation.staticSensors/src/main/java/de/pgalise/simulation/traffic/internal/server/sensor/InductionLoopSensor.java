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

import com.vividsolutions.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import de.pgalise.simulation.traffic.server.sensor.interferer.InductionLoopInterferer;

/**
 * Class to generate an induction loop sensor
 * 
 * @author Marcus
 * @author Lena
 * @version 1.0
 */
public class InductionLoopSensor extends StaticTrafficSensor {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(InductionLoopSensor.class);

	/**
	 * Number of the vehicles (with change of the interferer)
	 */
	private int vehicleCount = 0;

	/**
	 * Number of the vehicles (no change of the interferer)
	 */
	private int realVehicleCount = 0;

	/**
	 * InductionLoop interferer
	 */
	private InductionLoopInterferer interferer;

	/**
	 * Constructor
	 * 
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param interferer
	 *            InductionLoopInterferer
	 */
	public InductionLoopSensor(final Output output, final long sensorId, final Coordinate position, final InductionLoopInterferer interferer) {
		this(output, sensorId, position, 1, interferer);
	}

	/**
	 * Constructor
	 * 
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param updateLimit
	 *            Update limit
	 * @param interferer
	 *            InductionLoopInterferer
	 */
	public InductionLoopSensor(Output output, long sensorId, Coordinate position, int updateLimit, final InductionLoopInterferer interferer) {
		super(output, sensorId, position, updateLimit);
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferer"));
		}
		this.interferer = interferer;
	}

	public InductionLoopInterferer getInterferer() {
		return this.interferer;
	}

	@Override
	public SensorType getSensorType() {
		return SensorType.INDUCTIONLOOP;
	}

	public void setInterferer(final InductionLoopInterferer interferer) throws IllegalArgumentException {
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferer"));
		}
		this.interferer = interferer;
	}

	/**
	 * Transmits the actual count of vehicle and sets it to zero
	 * 
	 * @param eventList
	 *            List of SimulationEvents
	 */
	@Override
	public void transmitUsageData(EventList eventList) {
		if(this.vehicleCount>0) {
			log.debug("Send number of registered vehicles ("+this.vehicleCount+") on sensor "+this.getId());
		}

		// Send data
		this.getOutput().transmitDouble(this.vehicleCount);
		this.getOutput().transmitDouble((double) 0);
		this.getOutput().transmitByte((byte) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);

		// Reset all counters
		this.vehicleCount = 0;
		this.realVehicleCount = 0;
	}

	@Override
	public void logValueToSend(EventList eventList) {
		log.debug("Send number of registered vehicles ("+this.vehicleCount+") on sensor "+this.getId());
	}

	/**
	 * Increases the count of vehicles when a vehicle passes by.
	 * 
	 * @param vehicle
	 *            Vehicle
	 */
	@Override
	public void vehicleOnNodeRegistered(Vehicle<? extends VehicleData> vehicle) {
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicle.getData().getType())) {
			this.realVehicleCount++;

			this.vehicleCount += this.interferer.interfere(vehicle.getData().getLength(), this.realVehicleCount,
					vehicle.getVelocity());

			log.debug(this.getId()+" registered a vehicle");
		}
	}
}
