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
import java.util.HashSet;
import java.util.Set;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensor;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorTypeEnum;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * Class to generate weather stations. These sensors can add other sensors. The
 * composite pattern
 * (http://de.wikipedia.org/wiki/Kompositum_%28Entwurfsmuster%29#Java) are
 * realized by this class.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 28, 2012)
 */
public class WeatherStation extends WeatherSensor<WeatherStationData> {
	private static final long serialVersionUID = 1L;

	/**
	 * List with other weather sensors
	 */
	final private Set<WeatherSensor> children = new HashSet<>();

	/**
	 * Constructor
	 *
	 * @param id
	 * @param output Output of the sensor
	 * @param position Position of the sensor
	 * @param weatherInterferer
	 * @param weatherController Reference to the weather controller of the
	 * simulation
	 * @param updateLimit Update limit
	 * @param sensors Weather sensors
	 */
	public WeatherStation(Long id,
		Output output,
		Coordinate position,
		WeatherController weatherController,
		final WeatherInterferer weatherInterferer,
		WeatherSensor... sensors) {
		this(id,
			output,
			position,
			weatherController,
			weatherInterferer,
			1,
			sensors);
	}

	/**
	 * Constructor
	 *
	 * @param id
	 * @param output Output of the sensor
	 * @param position Position of the sensor
	 * @param weatherController Reference to the weather controller of the
	 * simulation
	 * @param weatherInterferer
	 * @param updateLimit Update limit
	 */
	public WeatherStation(Long id,
		Output output,
		Coordinate position,
		WeatherController weatherController,
		final WeatherInterferer weatherInterferer,
		int updateLimit,
		WeatherSensor... sensors) {
		super(id,
			output,
			position,
			weatherController,
			updateLimit,
			weatherInterferer,
			WeatherSensorTypeEnum.WEATHER_STATION,
			new WeatherStationData());
		if (sensors == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"sensors"));
		}
		for (WeatherSensor weatherSensor : sensors) {
			this.add(weatherSensor);
		}
	}

	@Override
	public void transmitUsageData(EventList eventList) {
		// Call all children
		for (WeatherSensor sensor : this.children) {
			sensor.transmitUsageData(eventList);
		}

	}

	/**
	 * Adds a new weather sensor to the children of the weather station
	 *
	 * @param sensor new weather sensor
	 */
	public void add(final WeatherSensor sensor) {
		children.add(sensor);
	}

	/**
	 * Removes a weather sensor from the children
	 *
	 * @param sensor weather sensor to remove
	 */
	public void remove(final WeatherSensor sensor) {
		if (children.contains(sensor)) {
			children.remove(sensor);
		}
	}

}
