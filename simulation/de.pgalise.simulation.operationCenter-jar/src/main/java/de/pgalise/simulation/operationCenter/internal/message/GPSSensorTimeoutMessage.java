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

/**
 * This message will be send, if the last update from a dynamic
 * sensor is older than (current time - (updateSteps * interval)).
 * @author Timo
 */
public class GPSSensorTimeoutMessage extends OCWebSocketMessage<Collection<SensorData>>{

	/**
	 * Constructor
	 * @param sensorData
	 * 			the gps sensors with a time out
	 */
	public GPSSensorTimeoutMessage(Collection<SensorData> sensorData) {
		super(OCWebSocketMessage.MessageType.GPS_SENSOR_TIMEOUT, sensorData);
	}
}
