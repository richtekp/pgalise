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
 
package de.pgalise.staticsensor.internal.sensor.energy;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.staticsensor.sensor.energy.EnergyInterferer;
import de.pgalise.simulation.staticsensor.sensor.energy.EnergySensor;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * Measures the energy consumption.
 * 
 * @author Timo
 */
public class SmartMeterSensor extends EnergySensor {

	/**
	 * Radius
	 */
	private final int measureRadiusInMeter;

	/**
	 * Constructor
	 * 
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param weatherController
	 *            Weather interface to access the weather data
	 * @param energyController
	 *            Energy interface to access the energy consumption data
	 * @param randomSeedService
	 * Seed service
	 * @param measureRadiusInMeter
	 *            Radius in meter
	 * @param interferer
	 *            Energy interferer
	 */
	
	public SmartMeterSensor(Output output, long sensorId, Coordinate position, WeatherController weatherController, EnergyController energyController, RandomSeedService randomSeedService, int measureRadiusInMeter, EnergyInterferer interferer) {
		this(output, sensorId, position, weatherController, energyController, randomSeedService, measureRadiusInMeter,
				1, interferer);
	}

	/**
	 * Constructor
	 * 
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param weatherController
	 *            Weather interface to access the weather data
	 * @param energyController
	 *            Energy interface to access the energy consumption data
	 * @param randomSeedService
	 * Seed service
	 * @param measureRadiusInMeter
	 *            Radius in meter
	 * @param updateLimit
	 *            Update limit
	 * @param interferer
	 *            Energy interferer
	 */
	
	public SmartMeterSensor(Output output, long sensorId, Coordinate position, WeatherController weatherController, EnergyController energyController, RandomSeedService randomSeedService, int measureRadiusInMeter, int updateLimit, EnergyInterferer interferer) {
		super(output, sensorId, position, weatherController, energyController, randomSeedService, updateLimit,
				interferer);
		this.measureRadiusInMeter = measureRadiusInMeter;
	}

	@Override
	public SensorType getSensorType() {
		return SensorType.SMARTMETER;
	}

	@Override
	public void transmitUsageData(EventList eventList) {
		// Get value
		double value = this.getEnergyController().getEnergyConsumptionInKWh(eventList.getTimestamp(),
				this.getPosition(), this.measureRadiusInMeter);

		// Interferer
		value = this.getInterferer().interfere(value, this.getPosition(), eventList.getTimestamp());

		// transmit value
		this.getOutput().transmitDouble(value);
		// transmit dummy values
		this.transmitDummyValues();
	}
}
