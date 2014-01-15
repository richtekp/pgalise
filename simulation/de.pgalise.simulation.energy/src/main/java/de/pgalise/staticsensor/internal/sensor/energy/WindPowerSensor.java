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

import de.pgalise.simulation.energy.entity.WindPowerSensorData;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.energy.sensor.EnergySensorTypeEnum;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.energy.sensor.EnergyInterferer;
import de.pgalise.simulation.energy.sensor.EnergySensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * Class to generate a wind power sensor.
 *
 * @author Marina
 * @author Marcus
 * @version 1.0
 */
public class WindPowerSensor extends EnergySensor<WindPowerSensorData> {

	private static final long serialVersionUID = 1L;

	/**
	 * Calculate the air density.
	 *
	 * @param airPressure Air pressure
	 * @param temperature Temperature in celsius
	 * @return airDensity
	 */
	public static double calculateAirDensity(double airPressure,
		double temperature) {
		// Temperature in Kelvin
		double kelvinTemp = temperature + 273.15;
		// Convert air pressure from mbar to Pa
		double airPressurePa = airPressure * 100.0;

		// Return calculated air density for dry air
		return airPressurePa / (287.058 * kelvinTemp);
	}

	/**
	 * Activity value
	 */
	private double activityValue = 50;
	/**
	 * Diameter of the rotor (m^2)
	 */
	private double diameter;// (e.g. 1194m^2)

	/**
	 * Length of the rotor
	 */
	private double rotorLength = 20;

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param id ID of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Weather interface to access the weather data
	 * @param randomSeedService Seed service
	 * @param energyController Energy interface to access the energy consumption
	 * data
	 * @param rotorLength
	 * @param activityValue
	 * @param interferer Energy interferer
	 */
	public WindPowerSensor(Long id,
		Output output,
		JaxRSCoordinate position,
		WeatherController weatherController,
		EnergyController energyController,
		RandomSeedService randomSeedService,
		double rotorLength,
		double activityValue,
		EnergyInterferer interferer) {
		this(id,
			output,
			position,
			weatherController,
			energyController,
			randomSeedService,
			rotorLength,
			activityValue,
			1,
			interferer);
	}

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param id ID of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Weather interface to access the weather data
	 * @param energyController Energy interface to access the energy consumption
	 * data
	 * @param randomSeedService Seed service
	 * @param rotorLength rotor length in m (usually between 7.5m and 63m).
	 * @param activityValue usually between 0.3 and 0.8
	 * @param updateLimit Update limit
	 * @param interferer Energy interferer
	 */
	public WindPowerSensor(Long id,
		Output output,
		JaxRSCoordinate position,
		WeatherController weatherController,
		EnergyController energyController,
		RandomSeedService randomSeedService,
		double rotorLength,
		double activityValue,
		int updateLimit,
		EnergyInterferer interferer) {
		super(id,
			output,
			position,
			weatherController,
			energyController,
			randomSeedService,
			updateLimit,
			interferer,
			new WindPowerSensorData());
		this.activityValue = activityValue;
		this.rotorLength = rotorLength;

		this.diameter = calculateDiameter(this.rotorLength);
	}

	/**
	 * Calculate the diameter on the basis of the rotor length.
	 *
	 * @param rotorLength rotor length
	 * @return
	 */
	public static double calculateDiameter(double rotorLength) {
		return Math.PI * (rotorLength * rotorLength);
	}

	/**
	 * Calculate the produced energy.
	 *
	 * @param airDensity air density
	 * @param activityValue Activ value
	 * @param diameter Diameter
	 * @param windVelocity wind velocity
	 * @return Amount of produced energy
	 */
	public static double calculateProducedEnergy(double airDensity,
		double activityValue,
		double diameter,
		double windVelocity) {
		// Produced Energy in Watt
		return 0.5 * (airDensity * activityValue * diameter * (Math.
			pow(windVelocity,
				3)));
	}

	/**
	 * Returns the activity value of the wind power station.
	 *
	 * @return activityValue
	 */
	public double getActivityValue() {
		return this.activityValue;
	}

	/**
	 * Sets the activity value of the wind power station.
	 *
	 * @param activityValue activity value
	 */
	public void setActivityValue(double activityValue) {
		this.activityValue = activityValue;
	}

	/**
	 * Returns the diameter.
	 *
	 * @return diameter
	 */
	public double getDiameter() {
		return this.diameter;
	}

	/**
	 * Returns the length of the rotor.
	 *
	 * @return rotorLength
	 */
	public double getRotorLength() {
		return this.rotorLength;
	}

	/**
	 * Sets the diameter.
	 *
	 * @param diameter diameter
	 */
	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}

	/**
	 * Sets the length of the rotor.
	 *
	 * @param rotorLength rotor length
	 */
	public void setRotorLength(double rotorLength) {
		this.rotorLength = rotorLength;
	}

	@Override
	public void transmitUsageData(EventList eventList) {
		// Get weather values
		double airPressure = (this.getWeatherController().getValue(
			WeatherParameterEnum.AIR_PRESSURE,
			eventList.getTimestamp(),
			this.getPosition())).doubleValue();
		double temperature = (this.getWeatherController().getValue(
			WeatherParameterEnum.TEMPERATURE,
			eventList.getTimestamp(),
			this.getPosition())).doubleValue();
		double windVelocity = (this.getWeatherController().getValue(
			WeatherParameterEnum.WIND_VELOCITY,
			eventList.getTimestamp(),
			this.getPosition())).doubleValue();

		// calculate air density
		double airDensity = calculateAirDensity(airPressure,
			temperature);
		getSensorData().setAirDensity(airDensity);

		// calculate wind power
		double windpower = calculateProducedEnergy(airDensity,
			this.activityValue,
			this.diameter,
			windVelocity);

		// Interferer
		windpower = this.getInterferer().interfere(windpower,
			this.getPosition(),
			eventList.getTimestamp());

		// transmit value
		this.getOutput().transmitDouble(windpower);
		// transmit dummy values
		this.transmitDummyValues();
	}

	@Override
	public SensorType getSensorType() {
		return EnergySensorTypeEnum.WINDPOWERSENSOR;
	}
}
