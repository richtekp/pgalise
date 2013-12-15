/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic;

import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightIntersectionSensor;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author richter
 */
public enum TrafficSensorTypeEnum implements TrafficSensorType {
	/**
	 * {@link SensorType} for induction loops
	 */
	INDUCTIONLOOP(6, InductionLoopSensor.class, "amount", SensorInterfererType.INDUCTION_LOOP_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for infrared sensors
	 */
	INFRARED(7, InfraredSensor.class, "amount", SensorInterfererType.INFRARED_WHITE_NOISE_INTERFERER),
	
	/**
	 * {@link SensorType} for GPS bike sensors
	 */
	GPS(2, GpsSensor.class, "lat,lng", SensorInterfererType.GPS_ATMOSPHERE_INTERFERER, 
			SensorInterfererType.GPS_CLOCK_INTERFERER, 
			SensorInterfererType.GPS_RECEIVER_INTERFERER, 
			SensorInterfererType.GPS_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for topo radars
	 */
	TOPORADAR(18, TopoRadarSensor.class, "amount"),
	/**
	 * {@link SensorType} for traffic lights
	 */
	TRAFFICLIGHT_SENSOR(14, TrafficLightSensor.class, ""),

	/**
	 * {@link SensorType} for traffic light intersections
	 */
	TRAFFIC_LIGHT_INTERSECTION(21, TrafficLightIntersectionSensor.class, "");
	
	
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
		if (TrafficSensorTypeEnum.SENSOR_TYPE_IDS == null) {
			TrafficSensorTypeEnum.SENSOR_TYPE_IDS = new HashMap<>();
		}
		return Collections.unmodifiableMap(TrafficSensorTypeEnum.SENSOR_TYPE_IDS);
	}

	/**
	 * Returns an unmodifiable {@link Map} of the sensorId/SensorType mapping.
	 * 
	 * @return an unmodifiable {@link Map} of the sensorId/SensorType mapping
	 */
	public static Map<Integer, SensorType> getSensorTypeIdsAsUnmodifiable() {
		return Collections.unmodifiableMap(SensorTypeEnum.getSensorTypeIds());
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
	private TrafficSensorTypeEnum(final int sensorTypeId, final Class<?> sensorTypeClass, String unit, SensorInterfererType... sensorInterfererTypes)
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
