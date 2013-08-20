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

import java.util.concurrent.ExecutionException;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.staticsensor.sensor.energy.EnergyInterferer;
import de.pgalise.simulation.staticsensor.sensor.energy.EnergySensor;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import javax.vecmath.Vector2d;

/**
 * Class to generate a photovoltaics sensor.
 * 
 * @author Marina
 * @author marcus
 * @version 1.0
 */
public class PhotovoltaikSensor extends EnergySensor {

	/**
	 * Roof area (qm)
	 */
	private int area;

	/**
	 * Light intensity
	 */
	private double lightIntensity;

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
	 * @param randomSeedService
	 *            Seed service
	 * @param energyController
	 *            Energy interface to access the energy consumption data
	 * @param area
	 *            roof area in m²
	 * @param interferer
	 *            Energy interferer
	 */
	public PhotovoltaikSensor(Output output, Object sensorId, Vector2d position, WeatherController weatherController,
			EnergyController energyController, RandomSeedService randomSeedService, int area,
			EnergyInterferer interferer) throws InterruptedException, ExecutionException {
		this(output, sensorId, position, weatherController, energyController, randomSeedService, area, 1, interferer);
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
	 * @param randomSeedService
	 *            Seed service
	 * @param energyController
	 *            Energy interface to access the energy consumption data
	 * @param area
	 *            roof area in m²
	 * @param updateLimit
	 *            Update limit
	 * @param interferer
	 *            Energy interferer
	 */
	public PhotovoltaikSensor(Output output, Object sensorId, Vector2d position, WeatherController weatherController,
			EnergyController energyController, RandomSeedService randomSeedService, int area, int updateLimit,
			EnergyInterferer interferer) throws InterruptedException, ExecutionException {
		super(output, sensorId, position, weatherController, energyController, randomSeedService, updateLimit,
				interferer);
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
		double percentLightIntesity = (this.lightIntensity / maxLightIntesity) * 100;

		// Calculates the produced energy
		double producedEnergy = maxProducedEnergy * (percentLightIntesity / 100);

		return producedEnergy;
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
	 * Returns the used light intensity.
	 * 
	 * @return lightIntensity
	 */
	public double getLightIntensity() {
		return this.lightIntensity;
	}

	@Override
	public SensorType getSensorType() {
		return SensorType.PHOTOVOLTAIK;
	}

	/**
	 * Sets the roof area.
	 * 
	 * @param area
	 *            roof area
	 */
	public void setArea(int area) {
		this.area = area;
	}

	/**
	 * Sets the light intensity.
	 * 
	 * @param lightIntensity
	 *            light intensity
	 */
	public void setLightIntensity(double lightIntensity) {
		this.lightIntensity = lightIntensity;
	}

	/**
	 * Transmits the usage data.
	 * 
	 * @param eventList
	 *            List of SimulationEvents
	 */
	@Override
	public void transmitUsageData(SimulationEventList eventList) {
		// Get value
		this.lightIntensity = (this.getWeatherController().getValue(WeatherParameterEnum.LIGHT_INTENSITY,
				eventList.getTimestamp(), this.getPosition())).doubleValue();
		double pvpower = this.calculateProducedEnergy();

		// Interferer
		pvpower = this.getInterferer().interfere(pvpower, this.getPosition(), eventList.getTimestamp());

		// transmit value
		this.getOutput().transmitDouble(pvpower);
		// transmit dummy values
		this.transmitDummyValues();

	}
}
