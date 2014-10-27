/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.energy.sensor;

import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.staticsensor.internal.sensor.energy.PhotovoltaikSensor;
import de.pgalise.staticsensor.internal.sensor.energy.SmartMeterSensor;
import de.pgalise.staticsensor.internal.sensor.energy.WindPowerSensor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author richter
 */
public enum EnergySensorTypeEnum implements EnergySensorType {
	
	/**
	 * {@link SensorType} for photovoltaik plants
	 */
	PHOTOVOLTAIK(9, PhotovoltaikSensor.class, "kWh", SensorInterfererType.PHOTOVOLTAIK_WHITE_NOISE_INTERFERER),
	
	/**
	 * {@link SensorType} for wind power sensors
	 */
	WINDPOWERSENSOR(16, WindPowerSensor.class, "Watt", SensorInterfererType.WIND_POWER_WHITE_NOISE_INTERFERER),
	
	
	/**
	 * {@link SensorType} for smartmeters
	 */
	SMARTMETER(12, SmartMeterSensor.class, "kWh", SensorInterfererType.SMART_METER_WHITE_NOISE_INTERFERER),
	;

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
		
	@SuppressWarnings("LeakingThisInConstructor")
	private EnergySensorTypeEnum(final int sensorTypeId, final Class<?> sensorTypeClass, String unit, SensorInterfererType... sensorInterfererTypes)
			throws IllegalArgumentException, IllegalStateException {
		if (sensorTypeId < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("sensorTypeId", true));
		}
		if (de.pgalise.simulation.sensorFramework.SensorTypeEnum.getSensorTypeIds().containsKey(sensorTypeId)) {
			throw new IllegalStateException("Argument 'sensorTypeId' = " + sensorTypeId + " is already registered");
		}
		this.sensorTypeId = sensorTypeId;
		de.pgalise.simulation.sensorFramework.SensorTypeEnum.getSensorTypeIds().put(sensorTypeId, this);
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
