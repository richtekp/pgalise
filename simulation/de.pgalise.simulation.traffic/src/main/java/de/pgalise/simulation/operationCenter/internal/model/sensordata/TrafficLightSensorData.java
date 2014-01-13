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
package de.pgalise.simulation.operationCenter.internal.model.sensordata;

import javax.persistence.Entity;

/**
 * Sensor data for traffic light. The sensorID of the traffic light sensor data
 * is the intersection ID. It also provides an ID for the single traffic light,
 * called trafficlightID {@link TrafficLightSensorData#getTrafficLightID()}.
 *
 * @author Dennis
 */
@Entity
public class TrafficLightSensorData extends SensorData {

	private static final long serialVersionUID = 1588118319493499615L;
	private int value;

	/**
	 * Constructor
	 */
	public TrafficLightSensorData() {
	}

	public TrafficLightSensorData(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
