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
 
package de.pgalise.simulation.shared.traffic;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import de.pgalise.simulation.shared.exception.ExceptionMessages;

/**
 * Enum with all possible vehicle types.
 * 
 * @author Timo
 */
public enum VehicleTypeEnum implements VehicleType {
	/**
	 * VehicleTypeEnum for cars
	 */
	CAR(0),
	/**
	 * VehicleTypeEnum for motorcycles
	 */
	MOTORCYCLE(1),
	/**
	 * VehicleTypeEnum for bikes
	 */
	BIKE(2),
	/**
	 * VehicleTypeEnum for trucks
	 */
	TRUCK(3),
	/**
	 * VehicleTypeEnum for busses
	 */
	BUS(4);

	/**
	 * maps vehicle type ids integers to @see VehicleTypeEnum
	 */
	private static Map<Integer, VehicleTypeEnum> VEHICLE_TYPE_IDS;

	/**
	 * Returns vehicle type ids.
	 * 
	 * @return
	 */
	private static Map<Integer, VehicleTypeEnum> getVehicleTypeIds() {
		if (VehicleTypeEnum.VEHICLE_TYPE_IDS == null) {
			VehicleTypeEnum.VEHICLE_TYPE_IDS = new HashMap<>();
		}
		return VehicleTypeEnum.VEHICLE_TYPE_IDS;
	}

	/**
	 * Returns vehicle type ids.
	 * 
	 * @return
	 */
	public static Map<Integer, VehicleTypeEnum> getVehicleTypeIdsAsUnmodifiable() {
		return Collections.unmodifiableMap(VehicleTypeEnum.getVehicleTypeIds());
	}

	/**
	 * Returns the vehicle type enum for the given id.
	 * 
	 * @param vehicleTypeId
	 * @return
	 */
	public static VehicleTypeEnum getForVehicleTypeId(final int vehicleTypeId) {
		return VehicleTypeEnum.getVehicleTypeIds().get(vehicleTypeId);
	}

	private final int vehicleTypeId;

	private VehicleTypeEnum(final int vehicleTypeId) throws IllegalArgumentException, IllegalStateException {
		if (vehicleTypeId < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("vehicleTypeId", true));
		}
		if (VehicleTypeEnum.getVehicleTypeIds().containsKey(vehicleTypeId)) {
			throw new IllegalStateException("Argument 'vehicleTypeId' = " + vehicleTypeId + " is already registered");
		}
		VehicleTypeEnum.getVehicleTypeIds().put(this.vehicleTypeId = vehicleTypeId, this);
	}

	/**
	 * Returns the vehicle type id belonging to this enum literal.
	 * 
	 * @return the vehicle type id belonging to this enum literal
	 */
	public int getVehicleTypeId() {
		return this.vehicleTypeId;
	}

	/**
	 * Motor driven vehicles
	 */
	public static final EnumSet<VehicleTypeEnum> MOTORIZED_VEHICLES = EnumSet.of(VehicleTypeEnum.BUS,
			VehicleTypeEnum.CAR, VehicleTypeEnum.MOTORCYCLE, VehicleTypeEnum.TRUCK);

	/**
	 * Motor driven vehicles for individual traffic
	 */
	public static final EnumSet<VehicleTypeEnum> VEHICLES_FOR_INDIVIDUAL_TRAFFIC = EnumSet.of(VehicleTypeEnum.CAR,
			VehicleTypeEnum.MOTORCYCLE, VehicleTypeEnum.TRUCK, VehicleTypeEnum.BIKE);
}
