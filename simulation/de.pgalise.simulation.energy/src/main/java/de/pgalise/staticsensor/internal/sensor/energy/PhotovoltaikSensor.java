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

import de.pgalise.simulation.shared.city.Coordinate;
import java.util.concurrent.ExecutionException;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.energy.sensor.EnergySensorTypeEnum;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.energy.sensor.EnergyInterferer;
import de.pgalise.simulation.energy.sensor.EnergySensor;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * Class to generate a photovoltaics sensor.
 *
 * @author Marina
 * @author marcus
 * @version 1.0
 */
public class PhotovoltaikSensor extends EnergySensor<PhotovoltaikSensorData> {

	private static final long serialVersionUID = 1L;
	

	/**
	 * Roof area (qm)
	 */
	private int area;

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Weather interface to access the weather data
	 * @param randomSeedService Seed service
	 * @param energyController Energy interface to access the energy consumption
	 * data
	 * @param area roof area in m²
	 * @param interferer Energy interferer
	 */
	public PhotovoltaikSensor(Output output,
		Coordinate position,
		WeatherController weatherController,
		EnergyController energyController,
		RandomSeedService randomSeedService,
		int area,
		EnergyInterferer interferer) throws InterruptedException, ExecutionException {
		this(output,
			position,
			weatherController,
			energyController,
			randomSeedService,
			area,
			1,
			interferer);
	}

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Weather interface to access the weather data
	 * @param energyController Energy interface to access the energy consumption
	 * data
	 * @param randomSeedService Seed service
	 * @param area roof area in m²
	 * @param updateLimit Update limit
	 * @param interferer Energy interferer
	 */
	public PhotovoltaikSensor(Output output,
		Coordinate position,
		WeatherController weatherController,
		EnergyController energyController,
		RandomSeedService randomSeedService,
		int area,
		int updateLimit,
		EnergyInterferer interferer) throws InterruptedException, ExecutionException {
		super(output,
			position,
			weatherController,
			energyController,
			randomSeedService,
			updateLimit,
			interferer,
			EnergySensorTypeEnum.PHOTOVOLTAIK, new PhotovoltaikSensorData());
		this.area = area;
	}

	/**
	 * Returns the calculated roof area.
	 *
	 * @return area
	 */
	public int getArea() {
		return this.area;
	}

	/**
	 * Sets the roof area.
	 *
	 * @param area roof area
	 */
	public void setArea(int area) {
		this.area = area;
	}

	/**
	 * Calculate the produced energy.
	 *
	 * @return producedEnergy produced energy
	 */
	public double calculateProducedEnergy() {
		double maxLightIntesity = 80000;
		double maxProducedEnergyPerQm = 150;
		double maxProducedEnergy = this.area * maxProducedEnergyPerQm;
		double percentLightIntesity = (this.getSensorData().getLightIntensity() / maxLightIntesity) * 100;

		// Calculates the produced energy
		double producedEnergy = maxProducedEnergy * (percentLightIntesity / 100);

		return producedEnergy;
	}

	/**
	 * Transmits the usage data.
	 *
	 * @param eventList List of SimulationEvents
	 */
	@Override
	public void transmitUsageData(EventList eventList) {
		// Get value
		double lightIntensity = (this.getWeatherController().getValue(
			WeatherParameterEnum.LIGHT_INTENSITY,
			eventList.getTimestamp(),
			this.getPosition())).doubleValue();
		double pvpower = this.calculateProducedEnergy();

		// Interferer
		pvpower = this.getInterferer().interfere(pvpower,
			this.getPosition(),
			eventList.getTimestamp());

		// transmit value
		this.getOutput().transmitDouble(pvpower);
		// transmit dummy values
		this.transmitDummyValues();

	}
}
