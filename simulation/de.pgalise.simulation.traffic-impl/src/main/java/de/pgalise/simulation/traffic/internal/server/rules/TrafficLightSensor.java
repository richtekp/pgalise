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
 
package de.pgalise.simulation.traffic.internal.server.rules;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.traffic.internal.DefaultTrafficEdge;
import de.pgalise.simulation.traffic.internal.DefaultTrafficNode;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;

/**
 * Sensor for the traffic lights
 * 
 * @author Marcus
 */
public class TrafficLightSensor<D extends VehicleData> extends StaticTrafficSensor<DefaultTrafficNode<D>, DefaultTrafficEdge<D>> {
	private static final long serialVersionUID = 1L;

	/**
	 * Traffic light
	 */
	private final TrafficLight trafficLight;

	/**
	 * Constructor
	 * 
	 * @param output
	 *            Sensor output
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param trafficLight
	 *            Traffic light
	 * @throws IllegalArgumentException
	 *             Thrown if no traffic light is null
	 */
	public TrafficLightSensor(Output output, int sensorId, Coordinate position, TrafficLight trafficLight)
			throws IllegalArgumentException {
		super(output, sensorId, position);
		if (trafficLight == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("trafficLight"));
		}
		this.trafficLight = trafficLight;
	}

	@Override
	public SensorTypeEnum getSensorType() {
		return SensorTypeEnum.TRAFFICLIGHT_SENSOR;
	}

	@Override
	public void transmitUsageData(EventList<TrafficEvent> eventList) {
		// // Send data
		this.getOutput().transmitDouble(this.trafficLight.getAngle1());
		this.getOutput().transmitDouble(this.trafficLight.getAngle2());
		this.getOutput().transmitByte((byte) this.trafficLight.getState().getStateId()); // stateId
		this.getOutput().transmitLong(this.trafficLight.getId());
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
	}

	@Override
	public void vehicleOnNodeRegistered(BaseVehicle<D> vehicle) {
		// nothing to do here
	}
}
