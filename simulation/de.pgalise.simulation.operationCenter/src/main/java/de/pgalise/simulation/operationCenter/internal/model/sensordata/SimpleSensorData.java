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

import de.pgalise.simulation.shared.sensor.SensorHelperSmartMeter;

/**
 * A simple sensor data with one value. E.g. for {@link SensorHelperSmartMeter}.
 * @author Timo
 */
public class SimpleSensorData extends SensorData {
	private static final long serialVersionUID = 1588118319493499615L;
	private double value;
	
	/**
	 * Constructor
	 * @param sensorType
	 * 			the type of the sensor
	 * @param sensorID
	 * 			the id of the sensor
	 * @param sensorValue
	 * 			the measured value
	 */
	public SimpleSensorData(int sensorType, int sensorID, double sensorValue) {
		super(sensorType, sensorID);
		this.value = sensorValue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
