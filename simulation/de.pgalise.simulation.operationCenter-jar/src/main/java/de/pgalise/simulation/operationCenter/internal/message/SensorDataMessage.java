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
 
package de.pgalise.simulation.operationCenter.internal.message;

import java.util.Collection;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;

/**
 * Sensor data message.
 * @author Timo
 */
public class SensorDataMessage extends OCWebSocketMessage<Collection<Sensor<?,?>>> {
	private long time;
	
	/**
	 * Constructor
	 * @param sensorData
	 * 			the measured value with sensor type and id
	 * @param timestamp
	 * 			the current time stamp of the moment of measuring the value
	 */
	public SensorDataMessage(Collection<Sensor<?,?>> sensorData, long timestamp) {
		super(MessageType.SENSOR_DATA, sensorData);
		this.setContent(sensorData);
		this.time = timestamp;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
