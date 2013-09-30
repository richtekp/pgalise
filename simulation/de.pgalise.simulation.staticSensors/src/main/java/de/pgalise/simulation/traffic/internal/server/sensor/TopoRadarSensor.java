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
import java.util.LinkedList;
import java.util.List;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import de.pgalise.simulation.traffic.server.sensor.interferer.TopoRadarInterferer;

/**
 * Represents a topo radar sensor
 * 
 * @author Marcus
 * @author Mischa
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 28, 2012)
 */
public class TopoRadarSensor<N extends TrafficNode<N,E>, E extends TrafficEdge<N,E>> extends StaticTrafficSensor<N,E> {

	/**
	 * The last registered vehicles
	 */
	private List<Vehicle<? extends VehicleData,N,E>> registeredVehicles;

	/**
	 * InfraredInterferer interferer
	 */
	private TopoRadarInterferer interferer;

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
	 *            TopoRadarInterferer
	 */
	public TopoRadarSensor(TrafficNode node, final Output output, final Coordinate position, final int updateLimit,
			final TopoRadarInterferer interferer) throws IllegalArgumentException {
		super(node, output, position, updateLimit);
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferer"));
		}
		this.interferer = interferer;
		this.registeredVehicles = new LinkedList<>();
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
	 * @param interferer
	 *            TopoRadarInterferer
	 */
	public TopoRadarSensor(TrafficNode node, final Output output, final Coordinate position,
			final TopoRadarInterferer interferer) throws IllegalArgumentException {
		this(node, output, position, 1, interferer);

	}

	public TopoRadarInterferer getInterferer() {
		return this.interferer;
	}

	@Override
	public SensorTypeEnum getSensorType() {
		return SensorTypeEnum.TOPORADAR;
	}

	public void setInterferer(final TopoRadarInterferer interferer) throws IllegalArgumentException {
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferer"));
		}
		this.interferer = interferer;
	}

	@Override
	public void transmitUsageData(EventList eventList) {
		if (!this.registeredVehicles.isEmpty()) {
			// Get the first vehicle in list
			VehicleData data = this.registeredVehicles.get(0).getData();

			// Interfere values
			int[] values = this.interferer.interfere(data.getAxleCount(), data.getLength(), data.getWheelbase1(),
					data.getWheelbase2(), eventList.getTimestamp());

			// Send data
			this.getOutput().transmitDouble((double) 0);
			this.getOutput().transmitDouble((double) 0);
			this.getOutput().transmitByte((byte) values[0]);
			this.getOutput().transmitShort((short) values[1]);
			this.getOutput().transmitShort((short) values[2]);
			this.getOutput().transmitShort((short) values[3]);

			// Remove vehicle
			this.registeredVehicles.remove(0);
		}
	}

	@Override
	public void vehicleOnNodeRegistered(Vehicle<? extends VehicleData,N,E> vehicle) {
		if (VehicleTypeEnum.MOTORIZED_VEHICLES.contains(vehicle.getData().getType())) {
			this.registeredVehicles.add(vehicle);
		}
	}

	/**
	 * Transmits the data if the car is not null and when the vehicle is driving.
	 * 
	 * @param eventList
	 *            List of SimulationEvents
	 * @exception IllegalStateException
	 *                if the getVehicle returns null
	 */
	@Override
	protected void transmitData(EventList eventList) {
		while (!this.registeredVehicles.isEmpty()) {
			super.transmitData(eventList);
		}
	}
}
