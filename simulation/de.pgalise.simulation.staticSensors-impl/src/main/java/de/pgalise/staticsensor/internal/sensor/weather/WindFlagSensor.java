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
import javax.vecmath.Vector2d;

/**
 * Class to generate a wind flag sensor.
 * 
 * @author Marina
 * @author Andreas Rehfeldt
 * @author marcus
 * @version 1.0
 */
public final class WindFlagSensor extends WeatherSensor {

	/**
	 * Wind direction
	 */
	double windDirection;

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
	 */
	public WindFlagSensor(Output output, Object sensorId, Vector2d position, WeatherController weatherController,
			final WeatherInterferer weatherInterferer) {
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
	 *            Update limit
	 */
	public WindFlagSensor(Output output, Object sensorId, Vector2d position, WeatherController weatherController,
			int updateLimit, final WeatherInterferer weatherInterferer) {
		super(output, sensorId, position, weatherController, updateLimit, weatherInterferer);
	}

	@Override
	public SensorType getSensorType() {
		return SensorType.WINDFLAG;
	}

	/**
	 * Returns the wind direction.
	 * 
	 * @return windDirection
	 */
	public double getWindDirection() {
		return this.windDirection;
	}

	/**
	 * Sets the wind direction.
	 * 
	 * @param windDirection
	 *            wind direction
	 */
	public void setWindDirection(double windDirection) {
		this.windDirection = windDirection;
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
		this.windDirection = (this.getWeatherController().getValue(WeatherParameterEnum.WIND_DIRECTION,
				eventList.getTimestamp(), this.getPosition())).doubleValue();

		// Interfere
		this.windDirection = this.getInterferer().interfere(this.windDirection, this.getPosition(),
				eventList.getTimestamp());

		// transmit value
		this.getOutput().transmitDouble(this.windDirection);
		// transmit dummy values
		this.transmitDummyValues();
	}
}
