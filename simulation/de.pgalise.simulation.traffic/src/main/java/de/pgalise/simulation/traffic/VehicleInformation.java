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
 
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import java.io.Serializable;

/**
 * Information about a vehicle.
 * A vehicle has a UUID and can have an actives GPS sensor and a trip.
 * 
 * @author Timo
 */
public class VehicleInformation implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 8560799482665484767L;

	/**
	 * Option to activate the GPS sensor
	 */
	private boolean gpsActivated;

	/**
	 * Type of the vehicle
	 */
	private VehicleTypeEnum vehicleType;

	/**
	 * Model of the vehicle
	 */
	private VehicleModelEnum vehicleModel;

	/**
	 * Current route of the vehicle
	 */
	private TrafficTrip trip;

	/**
	 * Name of the vehicle (optional)
	 */
	private String name;

	/**
	 * Default constructor
	 */
	public VehicleInformation() {

	}

	/**
	 * Constructor
	 * 
	 * @param vehicleID
	 *            ID of the vehicle
	 * @param gpsActivated
	 *            Option to activate the GPS sensor
	 * @param vehicleType
	 *            Type of the vehicle
	 * @param vehicleModel
	 *            Model of the vehicle. It must be available for the vehicleType
	 * @param trip
	 *            a trips. If null, the simulation will create a random trip
	 * @param name
	 *            Name of the vehicle (optional)
	 */
	public VehicleInformation( boolean gpsActivated, VehicleTypeEnum vehicleType,
			VehicleModelEnum vehicleModel, TrafficTrip trip, String name) {
		this.gpsActivated = gpsActivated;
		this.vehicleType = vehicleType;
		this.vehicleModel = vehicleModel;
		this.name = name;
		this.trip = trip;
	}

	public boolean isGpsActivated() {
		return this.gpsActivated;
	}

	public void setGpsActivated(boolean gpsActivated) {
		this.gpsActivated = gpsActivated;
	}

	public VehicleTypeEnum getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleTypeEnum vehicleType) {
		this.vehicleType = vehicleType;
	}

	public VehicleModelEnum getVehicleModel() {
		return vehicleModel;
	}

	public void setVehicleModel(VehicleModelEnum vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public TrafficTrip getTrip() {
		return trip;
	}

	public void setTrip(TrafficTrip trip) {
		this.trip = trip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
