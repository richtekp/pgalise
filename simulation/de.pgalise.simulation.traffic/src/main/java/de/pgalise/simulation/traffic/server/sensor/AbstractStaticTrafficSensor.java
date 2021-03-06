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

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.staticsensor.AbstractStaticSensor;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 * Interface for the traffic sensors
 *
 * @author Mischa
 * @version 1.0 (Oct 28, 2012)
 */
public abstract class AbstractStaticTrafficSensor<X extends SensorData> extends AbstractStaticSensor<TrafficEvent<?>, X>
	implements StaticTrafficSensor<X> {

	private TrafficNode node;

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param position Position of the sensor
	 * @param updateLimit Update limit
	 * @throws IllegalArgumentException
	 */
	public AbstractStaticTrafficSensor(Long id,
		Output output,
		TrafficNode node,
		int updateLimit,
		X sensorData)
		throws IllegalArgumentException {
		super(id,
			output,
			node,
			updateLimit,
			sensorData);
		this.node = node;
	}

	/**
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param position Position of the sensor
	 * @throws IllegalArgumentException
	 */
	public AbstractStaticTrafficSensor(Long id,
		Output output,
		TrafficNode node,
		X sensorData) throws
		IllegalArgumentException {
		super(id,
			output,
			node,
			sensorData);
		this.node = node;
	}
}
