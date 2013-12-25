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
package de.pgalise.simulation.energy.sensor;

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.staticsensor.AbstractStaticSensor;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * Abstract superclass of an energy sensor.
 *
 * @author Marcus
 * @version 1.0
 */
public abstract class EnergySensor<X extends SensorData> extends AbstractStaticSensor<EnergyEvent,X> {

	private static final long serialVersionUID = 1L;

	/**
	 * The energy controller used by the energy sensor.
	 */
	private final EnergyController energyController;

	/**
	 * The random seed service of the energy sensor.
	 */
	private final RandomSeedService randomSeedService;

	/**
	 * The weather controller used by the energy sensor.
	 */
	private final WeatherController weatherController;

	/**
	 * Energy interferer
	 */
	private final EnergyInterferer interferer;

	/**
	 * Creates an energy sensor.
	 *
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Weather interface to access the weather data
	 * @param randomSeedService Seed service
	 * @param energyController Energy interface to access the energy consumption
	 * data
	 * @param updateLimit Update limit
	 * @param interferer Energy interferer
	 */
	protected EnergySensor(Output output,
		Coordinate position,
		WeatherController weatherController,
		EnergyController energyController,
		RandomSeedService randomSeedService,
		int updateLimit,
		EnergyInterferer interferer,
		SensorType sensorType,X sensorData) {
		super(output,
			position,
			sensorType,
			updateLimit,sensorData);
		if (weatherController == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"weather"));
		}
		if (energyController == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"energycontroller"));
		}
		if (randomSeedService == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"randomSeedService"));
		}
		if (interferer == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"interferer"));
		}
		this.weatherController = weatherController;
		this.randomSeedService = randomSeedService;
		this.energyController = energyController;
		this.interferer = interferer;
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

	public EnergyController getEnergyController() {
		return this.energyController;
	}

	public RandomSeedService getRandomSeedService() {
		return this.randomSeedService;
	}

	public WeatherController getWeatherController() {
		return this.weatherController;
	}

	public EnergyInterferer getInterferer() {
		return interferer;
	}
}
