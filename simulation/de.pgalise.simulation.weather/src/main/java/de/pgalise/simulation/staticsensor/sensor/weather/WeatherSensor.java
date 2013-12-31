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
package de.pgalise.simulation.staticsensor.sensor.weather;

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.AbstractSensor;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.staticsensor.AbstractStaticSensor;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * Super class for weather sensors
 *
 * @author Andreas Rehfeldt
 * @author Marina
 * @author marcus
 * @version 1.0 (Aug 23, 2012)
 * @param <X>
 */
public abstract class WeatherSensor<X extends SensorData> extends AbstractStaticSensor<WeatherEvent,X> {

	/**
	 * Weather controller used by the weather sensors
	 */
	private final WeatherController weatherController;

	/**
	 * Weather interferer
	 */
	private final WeatherInterferer interferer;

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Reference to the weather controller of the
	 * simulation
	 * @param updateLimit Update limit
	 * @param weatherInterferer the {@link WeatherInterferer}
	 */
	protected WeatherSensor(Long id,Output output,
		final Coordinate position,
		WeatherController weatherController,
		int updateLimit,
		final WeatherInterferer weatherInterferer,
		SensorType sensorType, X  sensorData) {
		super(id,output,
			position,
			sensorType,
			updateLimit,sensorData);
		if (weatherController == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"weathercontroller"));
		}
		if (weatherInterferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"weatherInterferer"));
		}
		this.weatherController = weatherController;
		this.interferer = weatherInterferer;
	}

	protected WeatherController getWeatherController() {
		return this.weatherController;
	}

	public WeatherInterferer getInterferer() {
		return interferer;
	}

	/**
	 * Transmit five dummy values in this order: double, byte, short, short, short
	 */
	public void transmitDummyValues() {
		this.getOutput().transmitDouble(0);
		this.getOutput().transmitByte((byte) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
		this.getOutput().transmitShort((short) 0);
	}
}
