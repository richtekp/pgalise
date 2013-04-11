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
 
package de.pgalise.simulation.shared.sensor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.pgalise.simulation.shared.exception.ExceptionMessages;

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
public enum SensorType {

	/**
	 * {@link SensorType} for Anemometers
	 */
	ANEMOMETER(0, SensorHelperWeather.class, "m/s", SensorInterfererType.ANEMOMETER_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for Barometers
	 */
	BAROMETER(1, SensorHelperWeather.class, "hPa", SensorInterfererType.BAROMETER_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for GPS bike sensors
	 */
	GPS_BIKE(2, SensorHelper.class, "lat,lng", SensorInterfererType.GPS_ATMOSPHERE_INTERFERER, 
			SensorInterfererType.GPS_CLOCK_INTERFERER, 
			SensorInterfererType.GPS_RECEIVER_INTERFERER, 
			SensorInterfererType.GPS_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for GPS bus sensors
	 */
	GPS_BUS(3, SensorHelper.class, "lat,lng", SensorInterfererType.GPS_ATMOSPHERE_INTERFERER, 
			SensorInterfererType.GPS_CLOCK_INTERFERER, 
			SensorInterfererType.GPS_RECEIVER_INTERFERER, 
			SensorInterfererType.GPS_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for GPS car sensors
	 */
	GPS_CAR(4, SensorHelper.class, "lat,lng", SensorInterfererType.GPS_ATMOSPHERE_INTERFERER, 
			SensorInterfererType.GPS_CLOCK_INTERFERER, 
			SensorInterfererType.GPS_RECEIVER_INTERFERER, 
			SensorInterfererType.GPS_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for hygrometers
	 */
	HYGROMETER(5, SensorHelperWeather.class, "%", SensorInterfererType.HYGROMETER_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for induction loops
	 */
	INDUCTIONLOOP(6, SensorHelper.class, "amount", SensorInterfererType.INDUCTION_LOOP_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for infrared sensors
	 */
	INFRARED(7, SensorHelper.class, "amount", SensorInterfererType.INFRARED_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for luxmeters
	 */
	LUXMETER(8, SensorHelperWeather.class, "Lux", SensorInterfererType.LUXMETER_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for photovoltaik plants
	 */
	PHOTOVOLTAIK(9, SensorHelperPhotovoltaik.class, "kWh", SensorInterfererType.PHOTOVOLTAIK_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for pyranometers
	 */
	PYRANOMETER(10, SensorHelperWeather.class, "W/qm", SensorInterfererType.PYRANOMETER_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for rain sensors
	 */
	RAIN(11, SensorHelperWeather.class, "mm", SensorInterfererType.RAINSENSOR_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for smartmeters
	 */
	SMARTMETER(12, SensorHelperSmartMeter.class, "kWh", SensorInterfererType.SMART_METER_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for thermometers
	 */
	THERMOMETER(13, SensorHelperWeather.class, "°C", SensorInterfererType.THERMOMETER_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for traffic lights
	 */
	TRAFFICLIGHT_SENSOR(14, SensorHelper.class, ""),

	/**
	 * {@link SensorType} for wind flags
	 */
	WINDFLAG(15, SensorHelperWeather.class, "°", SensorInterfererType.WIND_FLAG_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for wind power sensors
	 */
	WINDPOWERSENSOR(16, SensorHelperWindPower.class, "Watt", SensorInterfererType.WIND_POWER_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for weather stations
	 */
	WEATHER_STATION(17, SensorHelper.class, ""),

	/**
	 * {@link SensorType} for topo radars
	 */
	TOPORADAR(18, SensorHelper.class, "amount"),

	/**
	 * {@link SensorType} for GPS truck sensors
	 */
	GPS_TRUCK(19, SensorHelper.class, "lat,lng", SensorInterfererType.GPS_ATMOSPHERE_INTERFERER, 
			SensorInterfererType.GPS_CLOCK_INTERFERER, 
			SensorInterfererType.GPS_RECEIVER_INTERFERER, 
			SensorInterfererType.GPS_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for GPS motorcycle sensors
	 */
	GPS_MOTORCYCLE(20, SensorHelper.class, "lat,lng", SensorInterfererType.GPS_ATMOSPHERE_INTERFERER, 
			SensorInterfererType.GPS_CLOCK_INTERFERER, 
			SensorInterfererType.GPS_RECEIVER_INTERFERER, 
			SensorInterfererType.GPS_WHITE_NOISE_INTERFERER),

	/**
	 * {@link SensorType} for traffic light intersections
	 */
	TRAFFIC_LIGHT_INTERSECTION(21, SensorHelperTrafficLightIntersection.class, "");

	/**
	 * GPS types
	 */
	public static final EnumSet<SensorType> GPS = EnumSet.of(GPS_CAR, GPS_MOTORCYCLE, GPS_TRUCK, GPS_BIKE, GPS_BUS);

	/**
	 * maps sensor type ids integers to {@link SensorType}
	 */
	private static Map<Integer, SensorType> SENSOR_TYPE_IDS;

	/**
	 * Returns SENSOR_TYPE_IDS.
	 * 
	 * @return SENSOR_TYPE_IDS
	 */
	private static Map<Integer, SensorType> getSensorTypeIds() {
		if (SensorType.SENSOR_TYPE_IDS == null) {
			SensorType.SENSOR_TYPE_IDS = new HashMap<>();
		}
		return SensorType.SENSOR_TYPE_IDS;
	}

	/**
	 * Returns an unmodifiable {@link Map} of the sensorId/SensorType mapping.
	 * 
	 * @return an unmodifiable {@link Map} of the sensorId/SensorType mapping
	 */
	public static Map<Integer, SensorType> getSensorTypeIdsAsUnmodifiable() {
		return Collections.unmodifiableMap(SensorType.getSensorTypeIds());
	}

	/**
	 * Returns the {@link SensorType} for the passed sensor type id integer.
	 * 
	 * @param sensorTypeId
	 *            the sensor type id of the wanted {@link SensorType}
	 * @return the {@link SensorType} for the passed sensor type id integer
	 */
	public static SensorType getForSensorTypeId(final int sensorTypeId) {
		return SensorType.getSensorTypeIds().get(sensorTypeId);
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
	private SensorType(final int sensorTypeId, final Class<?> sensorTypeClass, String unit, SensorInterfererType... sensorInterfererTypes)
			throws IllegalArgumentException, IllegalStateException {
		if (sensorTypeId < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("sensorTypeId", true));
		}
		if (SensorType.getSensorTypeIds().containsKey(sensorTypeId)) {
			throw new IllegalStateException("Argument 'sensorTypeId' = " + sensorTypeId + " is already registered");
		}
		SensorType.getSensorTypeIds().put(this.sensorTypeId = sensorTypeId, this);
		this.sensorTypeClass = sensorTypeClass;
		this.unit = unit;
		this.sensorInterfererTypeSet = new HashSet<>();
		for(int i = 0; i < sensorInterfererTypes.length; i++) {
			this.sensorInterfererTypeSet.add(sensorInterfererTypes[i]);
		}
	}

	/**
	 * Returns the sensor type id belonging to this enum literal.
	 * 
	 * @return the sensor type id belonging to this enum literal
	 */
	public int getSensorTypeId() {
		return this.sensorTypeId;
	}

	public Class<?> getSensorTypeClass() {
		return sensorTypeClass;
	}

	public String getUnit() {
		return unit;
	}

	public Set<SensorInterfererType> getSensorInterfererTypeSet() {
		return Collections.unmodifiableSet(sensorInterfererTypeSet);
	}
}
