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

import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;

/**
 * Sensordata for topo radar data. A {@link SensorHelper} with type {@link SensorType#TOPORADAR}.
 * This works only for vehicles with axial of two or three.
 * @author Timo
 */
public class TopoRadarSensorData extends SensorData {
	private static final long serialVersionUID = -1327298928609668912L;
	private int axleCount, length, axialDistance1, axialDistance2;

	/**
	 * Constructor
	 * @param sensorType
	 * 			the type of the sensor
	 * @param sensorID
	 * 			the ID of the sensor
	 * @param axleCount
	 * 			the amount of axles of the measured vehicle
	 * @param length
	 * 			the measured length of the car
	 * @param axialDistance1
	 * 			the distance between axial one and two
	 * @param axialDistance2
	 * 			the distance between axial two and three
	 */
	public TopoRadarSensorData(int sensorType, int sensorID, int axleCount, int length, int axialDistance1, int axialDistance2) {
		super(sensorType, sensorID);
		this.axleCount = axleCount;
		this.length = length;
		this.axialDistance1 = axialDistance1;
		this.axialDistance2 = axialDistance2;
	}

	public int getAxleCount() {
		return axleCount;
	}

	public void setAxleCount(int axleCount) {
		this.axleCount = axleCount;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getAxialDistance1() {
		return axialDistance1;
	}

	public void setAxialDistance1(int axialDistance1) {
		this.axialDistance1 = axialDistance1;
	}

	public int getAxialDistance2() {
		return axialDistance2;
	}

	public void setAxialDistance2(int axialDistance2) {
		this.axialDistance2 = axialDistance2;
	}
}
