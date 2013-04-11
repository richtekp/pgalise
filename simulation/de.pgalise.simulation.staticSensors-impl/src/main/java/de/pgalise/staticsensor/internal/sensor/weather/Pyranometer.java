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
 
package de.pgalise.staticsensor.internal.sensor.weather;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensor;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.PyranometerWhiteNoiseInterferer;
import de.pgalise.util.vector.Vector2d;

/**
 * Class to generate a pyranometer.
 * 
 * @author Marina
 * @author Andreas Rehfeldt
 * @author marcus
 * @version 1.0
 */
public final class Pyranometer extends WeatherSensor {

	/**
	 * Radiation
	 */
	double radiation;

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
	 *            Reference to the weather controller of the simulation
	 * @throws IllegalArgumentException
	 *             if argument 'weatherController' is 'null' or if argument 'weatherController' is not a type of
	 *             {@link PyranometerWhiteNoiseInterferer}
	 */
	public Pyranometer(Output output, Object sensorId, Vector2d position, WeatherController weatherController,
			final WeatherInterferer weatherInterferer) throws IllegalArgumentException {
		this(output, sensorId, position, weatherController, 1, weatherInterferer);
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
	 *            Reference to the weather controller of the simulation
	 * @param updateLimit
	 *            Update limit * @throws IllegalArgumentException if argument 'weatherController' is 'null' or if
	 *            argument 'weatherController' is not a type of {@link PyranometerWhiteNoiseInterferer}
	 */
	public Pyranometer(Output output, Object sensorId, Vector2d position, WeatherController weatherController,
			int updateLimit, final WeatherInterferer weatherInterferer) throws IllegalArgumentException {
		super(output, sensorId, position, weatherController, updateLimit, weatherInterferer);
		// if(!(weatherInterferer instanceof PyranometerWhiteNoiseInterferer)) {
		// throw new IllegalArgumentException("Argument 'weatherInterferer' must be a type '" +
		// PyranometerWhiteNoiseInterferer.class.getName() + "'");
		// }
	}

	/**
	 * Returns the radiation.
	 * 
	 * @return radiation
	 */
	public double getRadiation() {
		return this.radiation;
	}

	@Override
	public SensorType getSensorType() {
		return SensorType.PYRANOMETER;
	}

	/**
	 * Sets the radiation.
	 * 
	 * @param radiation
	 *            Radiation
	 */
	public void setRadiation(double radiation) {
		this.radiation = radiation;
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
		this.radiation = (this.getWeatherController().getValue(WeatherParameterEnum.RADIATION,
				eventList.getTimestamp(), this.getPosition())).doubleValue();

		// Interfere
		this.radiation = this.getInterferer().interfere(this.radiation, this.getPosition(), eventList.getTimestamp());

		// transmit value
		this.getOutput().transmitDouble(this.radiation);
		// transmit dummy values
		this.transmitDummyValues();
	}
}
