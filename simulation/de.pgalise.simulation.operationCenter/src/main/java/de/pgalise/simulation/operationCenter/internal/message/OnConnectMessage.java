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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.pgalise.simulation.operationCenter.internal.model.OCOnConnectData;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
/**
 * The on connect message will be send to the client when it connects.
 * It contains information about possible sensor types and sensor units.
 * @author Timo
 */
public class OnConnectMessage extends OCWebSocketMessage<OCOnConnectData>{

	/**
	 * Default
	 */
	public OnConnectMessage() {
		super(OCWebSocketMessage.MessageType.ON_CONNECT, new OCOnConnectData(getSensorTypeIds(), getSensorUnits()));
	}
	
	private static Map<Integer, String> getSensorTypeIds() {
		Map<Integer, String> sensorIdMap = new HashMap<>();
		
		for(Entry<Integer, SensorTypeEnum> entry : SensorTypeEnum.getSensorTypeIdsAsUnmodifiable().entrySet()) {
			sensorIdMap.put(entry.getKey(), entry.getValue().toString());
		}
		
		return sensorIdMap;
	}
	
	private static Map<Integer, String> getSensorUnits() {
		Map<Integer, String> sensorUnitMap = new HashMap<>();

		for(Entry<Integer, SensorTypeEnum> entry : SensorTypeEnum.getSensorTypeIdsAsUnmodifiable().entrySet()) {
			sensorUnitMap.put(entry.getKey(), entry.getValue().getUnit());
		}
		
		return sensorUnitMap;
	}
}
