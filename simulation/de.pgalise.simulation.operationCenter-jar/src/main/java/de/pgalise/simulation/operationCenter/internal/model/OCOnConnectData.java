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
 
package de.pgalise.simulation.operationCenter.internal.model;

import java.util.Map;

/**
 * Data that will be send to the client on connect.
 * It contains IDs for sensor types and the corresponding sensor units.
 * @author Timo
 */
public class OCOnConnectData {
	private Map<Integer, String> sensorTypeMap, sensorUnitMap;

	/**
	 * Constructor
	 * @param sensorTypeMap
	 * @param sensorUnitMap
	 */
	public OCOnConnectData(Map<Integer, String> sensorTypeMap, Map<Integer, String> sensorUnitMap) {
		this.sensorTypeMap = sensorTypeMap;
		this.sensorUnitMap = sensorUnitMap;
	}

	public Map<Integer, String> getSensorTypeMap() {
		return sensorTypeMap;
	}

	public void setSensorTypeMap(Map<Integer, String> sensorTypeMap) {
		this.sensorTypeMap = sensorTypeMap;
	}

	public Map<Integer, String> getSensorUnitMap() {
		return sensorUnitMap;
	}

	public void setSensorUnitMap(Map<Integer, String> sensorUnitMap) {
		this.sensorUnitMap = sensorUnitMap;
	}
}
