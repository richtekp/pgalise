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

import de.pgalise.simulation.weather.entity.HygrometerData;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensor;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorTypeEnum;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.HygrometerWhiteNoiseInterferer;

/**
 * Class to generate a hygrometer.
 *
 * @author Marina
 * @author Andreas Rehfeldt
 * @version 1.0
 */
public class Hygrometer extends WeatherSensor<HygrometerData> {

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Reference to the weather controller of the
	 * simulation * @param updateLimit Update limit * @throws
	 * IllegalArgumentException if argument 'weatherController' is 'null' or if
	 * argument 'weatherController' is not a type of
	 * {@link HygrometerWhiteNoiseInterferer}
	 */
	public Hygrometer(Long id,
		final Output output,
		final JaxRSCoordinate position,
		final WeatherController weatherController,
		final WeatherInterferer weatherInterferer)
		throws IllegalArgumentException {
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
	 * @param output Output of the sensor
	 * @param sensorId ID of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Reference to the weather controller of the
	 * simulation
	 * @param updateLimit Update limit * @throws IllegalArgumentException if
	 * argument 'weatherController' is 'null' or if argument 'weatherController'
	 * is not a type of {@link HygrometerWhiteNoiseInterferer}
	 */
	public Hygrometer(Long id,
		Output output,
		JaxRSCoordinate position,
		WeatherController weatherController,
		int updateLimit,
		final WeatherInterferer weatherInterferer) throws IllegalArgumentException {
		super(id,
			output,
			position,
			weatherController,
			updateLimit,
			weatherInterferer,
			new HygrometerData());
		// if(!(weatherInterferer instanceof HygrometerWhiteNoiseInterferer)) {
		// throw new IllegalArgumentException("Argument 'weatherInterferer' must be a type '" +
		// HygrometerWhiteNoiseInterferer.class.getName() + "'");
		// }
	}

	/**
	 * Transmits the usage data.
	 *
	 * @param eventList List of SimulationEvent
	 */
	@Override
	public void transmitUsageData(EventList eventList) {

		// Get value
		double relativHumidity = (this.getWeatherController().getValue(
			WeatherParameterEnum.RELATIV_HUMIDITY,
			eventList.getTimestamp(),
			this.getPosition())).doubleValue();
		getSensorData().setRelativHumidity(relativHumidity);

		// Interfere
		relativHumidity = this.getInterferer().interfere(relativHumidity,
			this.getPosition(),
			eventList.getTimestamp());

		// transmit value
		this.getOutput().transmitDouble(relativHumidity);
		// transmit dummy values
		this.transmitDummyValues();
	}

	@Override
	public SensorType getSensorType() {
		return WeatherSensorTypeEnum.HYGROMETER;
	}
}
