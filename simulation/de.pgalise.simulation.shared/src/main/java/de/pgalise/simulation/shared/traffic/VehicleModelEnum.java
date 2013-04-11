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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.pgalise.simulation.shared.exception.ExceptionMessages;

/**
 * Enum with all possible vehicle models.
 * @author Timo
 */
public enum VehicleModelEnum {
	/**
	 * VehicleModelEnum for children bikes
	 */
	BIKE_CHILDREN(0, VehicleTypeEnum.BIKE),
	/**
	 * VehicleModelEnum for gazelle bikes
	 */
	BIKE_GAZELLE(1, VehicleTypeEnum.BIKE),
	/**
	 * VehicleModelEnum for maintain bikes
	 */
	BIKE_MAINTAINBIKE(2, VehicleTypeEnum.BIKE),
	/**
	 * VehicleModelEnum for random created bikes
	 */
	BIKE_RANDOM(3, VehicleTypeEnum.BIKE),
	/**
	 * VehicleModelEnum for recument bikes
	 */
	BIKE_RECUMENT(4, VehicleTypeEnum.BIKE),
	/**
	 * VehicleModelEnum for citaro busses
	 */
	BUS_CITARO(5, VehicleTypeEnum.BUS),
	/**
	 * VehicleModelEnum for citaro-g busses
	 */
	BUS_CITARO_G(6, VehicleTypeEnum.BUS), 
	/**
	 * VehicleModelEnum for citaro-k busses
	 */
	BUS_CITARO_K(7, VehicleTypeEnum.BUS),
	/**
	 * VehicleModelEnum for citaro-l busses
	 */
	BUS_CITARO_L(8, VehicleTypeEnum.BUS),
	/**
	 * VehicleModelEnum for citaro-le busses
	 */
	BUS_CITARO_LE(9, VehicleTypeEnum.BUS),
	/**
	 * VehicleModelEnum for random created busses
	 */
	BUS_RANDOM(10, VehicleTypeEnum.BUS),
	/**
	 * VehicleModelEnum for bmw 1 cars
	 */
	CAR_BMW_1(11, VehicleTypeEnum.CAR),
	/**
	 * VehicleModelEnum for bmw 3 cars
	 */
	CAR_BMW_3(12, VehicleTypeEnum.CAR),
	/**
	 * VehicleModelEnum for bmw 5 cars
	 */
	CAR_BMW_5(13, VehicleTypeEnum.CAR),
	/**
	 * VehicleModelEnum for bmw 7 cars
	 */
	CAR_BMW_7(14, VehicleTypeEnum.CAR),
	/**
	 * VehicleModelEnum for bmw x5 cars
	 */
	CAR_BMW_X5(15, VehicleTypeEnum.CAR),
	/**
	 * VehicleModelEnum for bmw z4 cars
	 */
	CAR_BMW_Z4(16, VehicleTypeEnum.CAR),
	/**
	 * VehicleModelEnum for golf 6 cars
	 */
	CAR_GOLF_6(17, VehicleTypeEnum.CAR),
	/**
	 * VehicleModelEnum for golf 7 cars
	 */
	CAR_GOLF_7(18, VehicleTypeEnum.CAR),
	/**
	 * VehicleModelEnum for random created cars cars
	 */
	CAR_RANDOM(19, VehicleTypeEnum.CAR),
	/**
	 * VehicleModelEnum for kawasaki ninja motorcycles
	 */
	MOTORCYCLE_KAWASAKI_NINJA(20, VehicleTypeEnum.MOTORCYCLE),
	/**
	 * VehicleModelEnum for random created motorcycles
	 */
	MOTORCYCLE_RANDOM(21, VehicleTypeEnum.MOTORCYCLE),
	/**
	 * VehicleModelEnum for suzuki hayabusa motorcycles
	 */
	MOTORCYCLE_SUZUKI_HAYABUSA(22, VehicleTypeEnum.MOTORCYCLE),
	/**
	 * VehicleModelEnum for yamaha yzf motorcycles
	 */
	MOTORCYCLE_YAMAHA_YZF(23, VehicleTypeEnum.MOTORCYCLE),
	/**
	 * VehicleModelEnum coca-cola trucks
	 */
	TRUCK_COCA_COLA(24, VehicleTypeEnum.TRUCK),
	/**
	 * VehicleModelEnum man-tgx trucks
	 */
	TRUCK_MAN_TGX(25, VehicleTypeEnum.TRUCK),
	/**
	 * VehicleModelEnum daf-xft trucks
	 */
	TRUCK_DAF_XFT(26, VehicleTypeEnum.TRUCK),
	/**
	 * VehicleModelEnum random created trucks
	 */
	TRUCK_RANDOM(27, VehicleTypeEnum.TRUCK);
	
	/**
	 * maps vehicle model ids integers to VehicleTypeEnum
	 */
	private static Map<Integer, VehicleModelEnum> VEHICLE_MODEL_IDS;
	
	/**
	 * Mapts the vehicle models to the types.
	 */
	private static Map<VehicleTypeEnum, List<VehicleModelEnum>> VEHICLE_TYPE_MODELS;

	/**
	 * Returns vehicle type ids.
	 * @return
	 */
	private static Map<Integer, VehicleModelEnum> getVehicleModelIds() {
		if(VehicleModelEnum.VEHICLE_MODEL_IDS == null) {
			VehicleModelEnum.VEHICLE_MODEL_IDS = new HashMap<>();
		}
		return VehicleModelEnum.VEHICLE_MODEL_IDS;
	}
	
	/**
	 * Returns a map with vehicle models for every vehicle type.
	 * @return
	 */
	private static Map<VehicleTypeEnum, List<VehicleModelEnum>> getVehicleTypeModels() {
		if(VehicleModelEnum.VEHICLE_TYPE_MODELS == null) {
			VehicleModelEnum.VEHICLE_TYPE_MODELS = new HashMap<>();
		}
		
		return VehicleModelEnum.VEHICLE_TYPE_MODELS;
	}
	
	/**
	 * Returns vehicle model ids.
	 * @return
	 */
	public static Map<Integer, VehicleModelEnum> getVehicleModelIdsAsUnmodifiable() {
		return Collections.unmodifiableMap(VehicleModelEnum.getVehicleModelIds());
	}

	/**
	 * Returns a list with possible vehicles models for the given type.
	 * @param vehicleType
	 * @return
	 */
	public static List<VehicleModelEnum> getVehicleModelsForTypeAsUnmodifiable(VehicleTypeEnum vehicleType) {
		return Collections.unmodifiableList(VehicleModelEnum.getVehicleTypeModels().get(vehicleType));
	}
	

	/**
	 * Returns the vehicle model enum for the given id.
	 * @param vehicleTypeId
	 * @return
	 */
	public static VehicleModelEnum getForVehicleTypeId(final int vehicleModelId) {
		return VehicleModelEnum.getVehicleModelIds().get(vehicleModelId);
	}

	private final int vehicleModelId;
	private final VehicleTypeEnum availableVehicleType;

	/**
	 * Ceontructor
	 * @param vehicleModelId
	 * @param availableVehicleType
	 * 			every model is only available for one vehicle type.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private VehicleModelEnum(final int vehicleModelId, VehicleTypeEnum availableVehicleType) throws IllegalArgumentException, IllegalStateException {
		if(vehicleModelId < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("vehicleModelId", true));
		}
		if(VehicleModelEnum.getVehicleModelIds().containsKey(vehicleModelId)) {
			throw new IllegalStateException("Argument 'vehicleModelId' = " + vehicleModelId + " is already registered");
		}
		if(availableVehicleType == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("possibleVehicleType"));
		}
		VehicleModelEnum.getVehicleModelIds().put(this.vehicleModelId = vehicleModelId, this);
		this.availableVehicleType = availableVehicleType;
		List<VehicleModelEnum> vehicleModelEnumList = VehicleModelEnum.getVehicleTypeModels().get(availableVehicleType);
		if(vehicleModelEnumList == null) {
			vehicleModelEnumList = new ArrayList<>();
			VehicleModelEnum.getVehicleTypeModels().put(availableVehicleType, vehicleModelEnumList);
		}
		vehicleModelEnumList.add(this);
	}

	/**
	 * Returns the vehicle model id belonging to this enum literal.
	 * 
	 * @return the vehicle model id belonging to this enum literal
	 */
	public int getVehicleModelId() {
		return this.vehicleModelId;
	}
	
	/**
	 * Returns the available vehicle type.
	 * @return
	 */
	public VehicleTypeEnum getAvailableVehicleType() {
		return this.availableVehicleType;
	}
}
