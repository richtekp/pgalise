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
package de.pgalise.simulation.traffic.internal.server.sensor;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Entity;

/**
 * Sensordata for topo radar data. A {@link SensorHelper} with type
 * {@link SensorType#TOPORADAR}. This works only for vehicles with axial of two
 * or three.
 *
 * @author Timo
 */
@Entity
public class TopoRadarSensorData extends SensorData {

	private static final long serialVersionUID = -1327298928609668912L;
	private int axleCount, vehicleLength, axialDistance1, axialDistance2;

	/**
	 * Constructor
	 */
	public TopoRadarSensorData() {
	}

	public TopoRadarSensorData(int axleCount,
		int vehicleLength,
		int axialDistance1,
		int axialDistance2) {
		this.axleCount = axleCount;
		this.vehicleLength = vehicleLength;
		this.axialDistance1 = axialDistance1;
		this.axialDistance2 = axialDistance2;
	}

	public int getAxleCount() {
		return axleCount;
	}

	public void setAxleCount(int axleCount) {
		this.axleCount = axleCount;
	}

	public int getVehicleLength() {
		return vehicleLength;
	}

	public void setVehicleLength(int vehicleLength) {
		this.vehicleLength = vehicleLength;
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
