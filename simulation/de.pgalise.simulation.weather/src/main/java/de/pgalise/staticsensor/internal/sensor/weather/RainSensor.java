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

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensor;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorTypeEnum;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.RainsensorWhiteNoiseInterferer;

/**
 * Class to generate a rain sensor.
 *
 * @author Marina
 * @author Andreas Rehfeldt
 * @author marcus
 * @version 1.0
 */
public class RainSensor extends WeatherSensor<RainSensorData> {

	/**
	 * Constructor
	 *
	 * @param id
	 * @param output Output of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Reference to the weather controller of the
	 * simulation * @throws IllegalArgumentException if argument
	 * 'weatherController' is 'null' or if argument 'weatherController' is not a
	 * type of {@link RainsensorWhiteNoiseInterferer}
	 * @param weatherInterferer
	 */
	public RainSensor(Long id,
		Output output,
		Coordinate position,
		WeatherController weatherController,
		final WeatherInterferer weatherInterferer) throws IllegalArgumentException {
		this(id,
			output,
			position,
			weatherController,
			1,
			weatherInterferer);
	}

	/**
	 * Constructor
	 *
	 * @param id
	 * @param output Output of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Reference to the weather controller of the
	 * simulation
	 * @param updateLimit Update limit 
	 * @param weatherInterferer 
	 * @throws IllegalArgumentException if
	 * argument 'weatherController' is 'null' or if argument 'weatherController'
	 * is not a type of {@link RainsensorWhiteNoiseInterferer}
	 */
	public RainSensor(Long id,
		Output output,
		Coordinate position,
		WeatherController weatherController,
		int updateLimit,
		final WeatherInterferer weatherInterferer) throws IllegalArgumentException {
		super(id,
			output,
			position,
			weatherController,
			updateLimit,
			weatherInterferer,
			WeatherSensorTypeEnum.RAIN,
			new RainSensorData());
		// if(!(weatherInterferer instanceof RainsensorWhiteNoiseInterferer)) {
		// throw new IllegalArgumentException("Argument 'weatherInterferer' must be a type '" +
		// RainsensorWhiteNoiseInterferer.class.getName() + "'");
		// }
	}

	/**
	 * Transmits the usage data.
	 *
	 * @param eventList List of SimulationEvents
	 */
	@Override
	public void transmitUsageData(EventList eventList) {

		// Get value
		double precipitationAmount = (this.getWeatherController().getValue(
			WeatherParameterEnum.PRECIPITATION_AMOUNT,
			eventList.getTimestamp(),
			this.getPosition())).doubleValue();
		getSensorData().setPrecipitationAmount(precipitationAmount);

		// Interfere
		precipitationAmount = this.getInterferer().interfere(precipitationAmount,
			this.getPosition(),
			eventList.getTimestamp());

		// transmit value
		this.getOutput().transmitDouble(precipitationAmount);
		// transmit dummy values
		this.transmitDummyValues();
	}
}
