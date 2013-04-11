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

import java.util.Collection;
import java.util.UUID;

/**
 * Data for one vehicle. A vehicle can have more than one sensor and
 * this class wraps them.
 * @author Timo
 */
public class VehicleData {
	private Collection<SensorHelperTypeWrapper> sensors;
	private UUID vehicleID;
	private int vehicleTypeID;
	private int vehicleModelID;
	
	/**
	 * Creates a new vehicle.
	 * @param sensors
	 * 			the sensors in/on the vehicle
	 * @param vehicleID
	 * 			the ID of the vehicle
	 * @param vehicleTypeID
	 * 			the type of the vehicle
	 * @param vehicleModelID
	 * 			the model of the vehicle
	 */
	public VehicleData(Collection<SensorHelperTypeWrapper> sensors,
			UUID vehicleID, int vehicleTypeID, int vehicleModelID) {
		this.sensors = sensors;
		this.vehicleID = vehicleID;
		this.vehicleTypeID = vehicleTypeID;
		this.vehicleModelID = vehicleModelID;
	}

	public Collection<SensorHelperTypeWrapper> getSensors() {
		return sensors;
	}
	
	public void setSensors(Collection<SensorHelperTypeWrapper> sensors) {
		this.sensors = sensors;
	}
	
	public UUID getVehicleID() {
		return vehicleID;
	}
	
	public void setVehicleID(UUID vehicleID) {
		this.vehicleID = vehicleID;
	}
	
	public int getVehicleTypeID() {
		return vehicleTypeID;
	}
	
	public void setVehicleTypeID(int vehicleTypeID) {
		this.vehicleTypeID = vehicleTypeID;
	}
	
	public int getVehicleModelID() {
		return vehicleModelID;
	}
	
	public void setVehicleModelID(int vehicleModelID) {
		this.vehicleModelID = vehicleModelID;
	}
}
