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
 
package de.pgalise.simulation.sensorFramework;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import java.util.Arrays;

/**
 * Enum for various sensor types.
 * New sensor types needs to be added here.
 * The {@link SimulationController} will send the sensor helper
 * to every known controller, so one controller needs to handle it without an exception.
 * A sensor type contains the type ID, the class of {@link SensorHelper} that implements it, the unit as string and all possible {@link SensorInterfererType} for it.
 * 
 * @author Marcus
 * @author Timo
 */
public enum SensorTypeEnum implements SensorType {

;

	/**
	 * maps sensor type ids integers to {@link SensorType}
	 */
	private static Map<Integer, SensorType> SENSOR_TYPE_IDS;
	
	/**
	 * Returns SENSOR_TYPE_IDS.
	 * 
	 * @return SENSOR_TYPE_IDS
	 */
	public static Map<Integer, SensorType> getSensorTypeIds() {
		if (SensorTypeEnum.SENSOR_TYPE_IDS == null) {
			SensorTypeEnum.SENSOR_TYPE_IDS = new HashMap<>();
		}
		return SENSOR_TYPE_IDS;
	}

	/**
	 * Returns an unmodifiable {@link Map} of the sensorId/SensorType mapping.
	 * 
	 * @return an unmodifiable {@link Map} of the sensorId/SensorType mapping
	 */
	public static Map<Integer, SensorType> getSensorTypeIdsAsUnmodifiable() {
		return SensorTypeEnum.getSensorTypeIds();
	}

	/**
	 * Returns the {@link SensorType} for the passed sensor type id integer.
	 * 
	 * @param sensorTypeId
	 *            the sensor type id of the wanted {@link SensorType}
	 * @return the {@link SensorType} for the passed sensor type id integer
	 */
	public static SensorType getForSensorTypeId(final int sensorTypeId) {
		return SensorTypeEnum.getSensorTypeIds().get(sensorTypeId);
	}

	/**
	 * the integer sensor type id
	 */
	private final int sensorTypeId;

	/**
	 * Used class. Useful for gson deserialization.
	 */
	private final Class<?> sensorTypeClass;

	/**
	 * Measure unit
	 */
	private final String unit;
	
	/**
	 * Contains all possible sensor interferer for this sensor.
	 */
	private Set<SensorInterfererType> sensorInterfererTypeSet;

	/**
	 * Creates an {@link SensorType}.
	 * 
	 * @param sensorTypeId
	 *            the integer sensor type id belonging to the literal
	 * @param sensorTypeClass
	 *            the used class. Useful for gson deserialization.
	 * @param sensorControllerClass
	 *            to find a suitable sensor controller.
	 * @param unit
	 *            measure unit of the sensor
	 * @param sensorInterfererTypes
	 * 			  contains all the useable sensor interferers for the sensor.
	 * @throws IllegalArgumentException
	 *             if argument 'sensorTypeId' is negative
	 * @throws IllegalStateException
	 *             if argument 'sensorTypeId' is already mapped to a {@link SensorType}
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	private SensorTypeEnum(final int sensorTypeId, final Class<?> sensorTypeClass, String unit, SensorInterfererType... sensorInterfererTypes)
			throws IllegalArgumentException, IllegalStateException {
		if (sensorTypeId < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("sensorTypeId", true));
		}
		if (SensorTypeEnum.getSensorTypeIds().containsKey(sensorTypeId)) {
			throw new IllegalStateException("Argument 'sensorTypeId' = " + sensorTypeId + " is already registered");
		}
		this.sensorTypeId = sensorTypeId;
		SensorTypeEnum.getSensorTypeIds().put(sensorTypeId, this);
		this.sensorTypeClass = sensorTypeClass;
		this.unit = unit;
		this.sensorInterfererTypeSet = new HashSet<>();
		this.sensorInterfererTypeSet.addAll(Arrays.asList(sensorInterfererTypes));
	}

	/**
	 * Returns the sensor type id belonging to this enum literal.
	 * 
	 * @return the sensor type id belonging to this enum literal
	 */
	@Override
	public int getSensorTypeId() {
		return this.sensorTypeId;
	}

	@Override
	public Class<?> getSensorTypeClass() {
		return sensorTypeClass;
	}

	@Override
	public String getUnit() {
		return unit;
	}

	@Override
	public Set<SensorInterfererType> getSensorInterfererTypeSet() {
		return Collections.unmodifiableSet(sensorInterfererTypeSet);
	}
}
