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
 
package de.pgalise.simulation.traffic.event;

import java.io.Serializable;

import de.pgalise.simulation.traffic.VehicleInformation;

/**
 * Holds data for a random vehicle.
 * 
 * @author Timo
 */
public abstract class CreateRandomVehicleData implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 8693442854311741057L;

	/**
	 * Information about the vehicle
	 */
	private VehicleInformation vehicleInformation;
	
	/**
	 * Constructor
	 * 
	 * @param sensorHelpers
	 *            List of SensorHelper for sensors
	 * @param vehicleInformation
	 *            Information about the vehicle
	 */
	public CreateRandomVehicleData(VehicleInformation vehicleInformation) {
		this.vehicleInformation = vehicleInformation;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CreateRandomVehicleData other = (CreateRandomVehicleData) obj;
		if (vehicleInformation == null) {
			if (other.vehicleInformation != null) {
				return false;
			}
		} else if (!vehicleInformation.equals(other.vehicleInformation)) {
			return false;
		}
		return true;
	}

	public VehicleInformation getVehicleInformation() {
		return vehicleInformation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vehicleInformation == null) ? 0 : vehicleInformation.hashCode());
		return result;
	}

	public void setVehicleInformation(VehicleInformation vehicleInformation) {
		this.vehicleInformation = vehicleInformation;
	}

}
