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
 
package de.pgalise.simulation.traffic.server.sensor;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import javax.vecmath.Vector2d;

/**
 * Interface for the traffic sensors
 * 
 * @author Mischa
 * @version 1.0 (Oct 28, 2012)
 */
public abstract class StaticTrafficSensor extends Sensor {

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
	 * @throws IllegalArgumentException
	 */
	protected StaticTrafficSensor(Output output, long sensorId, Coordinate position, int updateLimit)
			throws IllegalArgumentException {
		super(output, sensorId, position, updateLimit);
	}

	/**
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @throws IllegalArgumentException
	 */
	protected StaticTrafficSensor(Output output, long sensorId, Coordinate position) throws IllegalArgumentException {
		super(output, sensorId, position);
	}

	/**
	 * Register vehicle on the node
	 * 
	 * @param vehicle
	 *            Vehicle
	 */
	public abstract void vehicleOnNodeRegistered(Vehicle<? extends VehicleData> vehicle);
}
