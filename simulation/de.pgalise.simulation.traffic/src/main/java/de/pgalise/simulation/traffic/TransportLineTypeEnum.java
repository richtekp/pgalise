/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic;

import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public enum TransportLineTypeEnum implements TransportLineType {
	
	TRAM(0),SUBWAY(1),RAIL(2),BUS(3),FERRY(4),CABLE_CAR(5),GONDOLA(6),FUNICULAR(7);
	
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
		if (TransportLineTypeEnum.SENSOR_TYPE_IDS == null) {
			TransportLineTypeEnum.SENSOR_TYPE_IDS = new HashMap<>();
		}
		return SENSOR_TYPE_IDS;
	}

	/**
	 * Returns an unmodifiable {@link Map} of the sensorId/SensorType mapping.
	 * 
	 * @return an unmodifiable {@link Map} of the sensorId/SensorType mapping
	 */
	public static Map<Integer, SensorType> getSensorTypeIdsAsUnmodifiable() {
		return TransportLineTypeEnum.getSensorTypeIds();
	}

	/**
	 * the integer sensor type id
	 */
	private final int transportLineTypeId;

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
	private TransportLineTypeEnum(final int sensorTypeId)
			throws IllegalArgumentException, IllegalStateException {
		if (sensorTypeId < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("sensorTypeId", true));
		}
		if (SensorTypeEnum.getSensorTypeIds().containsKey(sensorTypeId)) {
			throw new IllegalStateException("Argument 'sensorTypeId' = " + sensorTypeId + " is already registered");
		}
		this.transportLineTypeId = sensorTypeId;
	}

	/**
	 * Returns the sensor type id belonging to this enum literal.
	 * 
	 * @return the sensor type id belonging to this enum literal
	 */
	@Override
	public int getTransportLineTypeId() {
		return this.transportLineTypeId;
	}
}
