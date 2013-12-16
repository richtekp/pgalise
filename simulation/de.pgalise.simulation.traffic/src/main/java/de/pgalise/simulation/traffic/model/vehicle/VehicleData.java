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

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import javax.faces.bean.ManagedBean;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

/**
 * General information about a vehicle.
 *
 * @author Marcus
 * @version 1.0 (Nov 7, 2012)
 */
@MappedSuperclass
@ManagedBean
public class VehicleData extends AbstractIdentifiable
{

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 2649565387450310817L;

	/**
	 * Length in mm
	 */
	/*
	 length is a reserved SQL keyword
	 */
	private int vehicleLength; // MM

	/**
	 * Number of axle
	 */
	private int axleCount;

	/**
	 * Wheelbase in mm (Distance between axe 1 and 2)
	 */
	private int wheelbase1; // MM

	/**
	 * Wheelbase in mm (Distance between axe 2 and 3)
	 */
	private int wheelbase2; // MM

	/**
	 * Vehicle type
	 */
	private VehicleTypeEnum type;

	/**
	 * SensorHelper of the GPS sensor
	 */
	private GpsSensor gpsSensor;

	public VehicleData() {
	}

	/**
	 * Constructor
	 *
	 * @param length
	 *                   Length in mm
	 * @param wheelbase1
	 * @param wheelbase2
	 * @param type
	 * @param axleCount
	 *                   Number of axle
	 * @param gpsSensor
	 */
	public VehicleData(int length, int wheelbase1, int wheelbase2,
					int axleCount, VehicleTypeEnum type,
					GpsSensor gpsSensor) {
		this.vehicleLength = length;
		this.wheelbase1 = wheelbase1;
		this.wheelbase2 = wheelbase2;
		this.axleCount = axleCount;
		this.type = type;
		this.gpsSensor = gpsSensor;
	}

	public int getVehicleLength() {
		return this.vehicleLength;
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

	public void setVehicleLength(int vehicleLength) {
		this.vehicleLength = vehicleLength;
	}

	public void setWheelbase2(int wheelbase2) {
		this.wheelbase2 = wheelbase2;
	}

	public void setWheelbase1(int wheelbase1) {
		this.wheelbase1 = wheelbase1;
	}

	public void setType(VehicleTypeEnum type) {
		this.type = type;
	}

	public void setAxleCount(int axleCount) {
		this.axleCount = axleCount;
	}

	public GpsSensor getGpsSensor() {
		return gpsSensor;
	}

	public void setGpsSensor(GpsSensor gpsSensor) {
		this.gpsSensor = gpsSensor;
	}

//	@Override
//	public String toString() {
//		return "VehicleData [length=" + vehicleLength + ", axleCount=" + axleCount
//					 + ", wheelbase1=" + wheelbase1
//									 + ", wheelbase2=" + wheelbase2 + ", type=" + type
//					 + ", gpsSensor=" + gpsSensor + "]";
//	}
}
