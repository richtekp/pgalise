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
 
package de.pgalise.simulation.traffic.model.vehicle;

import java.io.Serializable;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;

/**
 * General information about a vehicle.
 * 
 * @author Marcus
 * @version 1.0 (Nov 7, 2012)
 */
public class VehicleData implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 2649565387450310817L;

	/**
	 * Length in mm
	 */
	private final int length; // MM

	/**
	 * Number of axle
	 */
	private final int axleCount;

	/**
	 * Wheelbase in mm (Distance between axe 1 and 2)
	 */
	private final int wheelbase1; // MM

	/**
	 * Wheelbase in mm (Distance between axe 2 and 3)
	 */
	private final int wheelbase2; // MM

	/**
	 * Vehicle type
	 */
	private final VehicleTypeEnum type;

	/**
	 * SensorHelper of the GPS sensor
	 */
	private SensorHelper gpsSensorHelper;
	
	private transient Sensor gpsSensor;

	/**
	 * Constructor
	 * 
	 * @param length
	 *            Length in mm
	 * @param wheelbase
	 *            Wheelbase in mm
	 * @param axleCount
	 *            Number of axle
	 */
	protected VehicleData(int length, int wheelbase1, int wheelbase2, int axleCount, VehicleTypeEnum type,
			SensorHelper gpsSensor) {
		this.length = length;
		this.wheelbase1 = wheelbase1;
		this.wheelbase2 = wheelbase2;
		this.axleCount = axleCount;
		this.type = type;
		this.gpsSensorHelper = gpsSensor;
	}

	public int getLength() {
		return this.length;
	}

	public int getWheelbase1() {
		return this.wheelbase1;
	}

	public int getWheelbase2() {
		return this.wheelbase2;
	}

	public int getAxleCount() {
		return this.axleCount;
	}

	public VehicleTypeEnum getType() {
		return this.type;
	}

	public SensorHelper getGpsSensorHelper() {
		return gpsSensorHelper;
	}

	public void setGpsSensorHelper(SensorHelper gpsSensor) {
		this.gpsSensorHelper = gpsSensor;
	}

	public Sensor getGpsSensor() {
		return gpsSensor;
	}

	public void setGpsSensor(Sensor gpsSensor) {
		this.gpsSensor = gpsSensor;
	}

	@Override
	public String toString() {
		return "VehicleData [length=" + length + ", axleCount=" + axleCount + ", wheelbase1=" + wheelbase1
				+ ", wheelbase2=" + wheelbase2 + ", type=" + type + ", gpsSensor=" + gpsSensorHelper + "]";
	}
}
