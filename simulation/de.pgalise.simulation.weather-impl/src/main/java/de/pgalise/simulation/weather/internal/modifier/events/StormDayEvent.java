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
 
package de.pgalise.simulation.weather.internal.modifier.events;

import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.event.weather.WeatherEventEnum;
import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.modifier.WeatherDayEventModifier;
import de.pgalise.simulation.weather.modifier.WeatherMapModifier;
import de.pgalise.simulation.weather.modifier.WeatherStrategy;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.util.DateConverter;

/**
 * Simulate a strom day ({@link WeatherMapModifier} and {@link WeatherStrategy}).<br />
 * <br />
 * The file weather_decorators.properties describes the default properties for the implemented modifier. If no
 * parameters are given in the constructor of an implemented modifier, the standard properties of the file will be used.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (09.08.2012)
 */
public final class StormDayEvent extends WeatherDayEventModifier {

	/**
	 * Event type
	 */
	public static final WeatherEventEnum TYPE = WeatherEventEnum.STORMDAY;

	/**
	 * Parameter which will be changed
	 */
	public static final WeatherParameterEnum CHANGE_PARAMETER = WeatherParameterEnum.WIND_VELOCITY;

	/**
	 * Order id
	 */
	public static final int ORDER_ID = 7;

	/**
	 * Logger (for tests)
	 */
	private static Logger log = LoggerFactory.getLogger(StormDayEvent.class);

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 3482366855222183348L;

	/**
	 * Max value for storms
	 */
	public float max_value;

	/**
	 * Min value for stroms
	 */
	public float min_value;

	/**
	 * Max value
	 */
	private float maxValue;

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 * @param time
	 *            Timestamp of the simulation event
	 * @param props
	 *            Properties
	 * @param value
	 *            Specified value
	 * @param duration
	 *            Maximal duration of the event
	 */
	public StormDayEvent(long seed, long time, Properties props, Float value, Float duration,
			WeatherLoader weatherLoader) {
		super(seed, time, props, value, duration, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 * @param props
	 *            Properties
	 */
	public StormDayEvent(long seed, Properties props, WeatherLoader weatherLoader) {
		super(seed, props, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 */
	public StormDayEvent(long seed, WeatherLoader weatherLoader) {
		super(seed, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param map
	 *            WeatherMap
	 * @param seed
	 *            Seed for random generators
	 */
	public StormDayEvent(WeatherMap map, long seed, WeatherLoader weatherLoader) {
		super(map, seed, weatherLoader);
	}

	/*
	 * (non-Javadoc)
	 * @see de.pgalise.simulation.weather.database.Weather#deployChanges()
	 */
	@Override
	public void deployChanges() {
		// Calculate max value
		if (this.eventValue != null) {
			this.maxValue = this.eventValue;
		} else {
			do {
				this.maxValue = this.getRandomDouble((int) this.max_value);
			} while (this.maxValue < this.min_value);
		}

		// Get time
		if (this.eventTimestamp < 1) {
			this.eventTimestamp = this.getRandomTimestamp(this.simulationTimestamp);
		}

		Weather max = this.getNextWeatherForTimestamp(this.eventTimestamp);

		// Calculate difference between max (reference) and max (event)
		float maxDifference = this.maxValue - max.getWindVelocity();

		StormDayEvent.log.debug("Max. value of event (" + new Date(max.getTimestamp()) + "): " + this.maxValue
				+ " (actual: " + max.getPrecipitationAmount() + " ; difference: " + maxDifference + ")");

		// If there is a difference
		if (maxDifference > 0) {

			/*
			 * Create limits
			 */
			// Event limits
			long minTime = this.getMinHour(max.getTimestamp(), this.eventDuration);
			StormDayEvent.log.debug("Min. time of event: " + new Date(minTime) + " (Duration: " + this.eventDuration
					+ ")");
			long maxTime = this.getMaxHour(max.getTimestamp(), this.eventDuration);
			StormDayEvent.log.debug("Max. time of event: " + new Date(maxTime) + " (Duration: " + this.eventDuration
					+ ")");
			long actTime;

			// Interpolate limits for difference
			long minTimeInterpolate = minTime + DateConverter.HOUR;
			long maxTimeInterpolate = maxTime - DateConverter.HOUR;

			// Sort values
			Vector<Long> times = new Vector<Long>(this.map.keySet());
			Collections.sort(times);

			/*
			 * Add to reference values
			 */
			float value, difference;
			for (Long time : times) {
				// Get weather
				Weather weather = this.map.get(time);
				actTime = weather.getTimestamp();

				// Between the interval?
				if ((actTime < minTime) || (actTime > maxTime)) {
					continue;
				}

				// Calculate difference
				if (actTime < minTimeInterpolate) {
					difference = (float) WeatherDayEventModifier.interpolate(minTime, 0, minTimeInterpolate,
							maxDifference, actTime);
				} else if (actTime > maxTimeInterpolate) {
					difference = (float) WeatherDayEventModifier.interpolate(maxTimeInterpolate, maxDifference,
							maxTime, 0, actTime);
				} else {
					difference = maxDifference;
				}
				// log.debug("Difference: " + difference);

				// Change parameters
				value = weather.getWindVelocity() + difference;
				// log.debug("Value changes: " + value + " (actual: " + weather.getWindVelocity() + ")");
				weather.setWindVelocity(value);
			}

		}

		// Super class
		super.deployChanges();
	}

	public float getMaxValue() {
		return this.maxValue;
	}

	@Override
	public WeatherEventEnum getType() {
		return StormDayEvent.TYPE;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Initiate the decorator
	 */
	@Override
	protected void initDecorator() {
		if (this.props != null) {
			// Load properties from file
			this.orderID = Integer.parseInt(this.props.getProperty("stormday_order_id"));
			this.min_value = Float.parseFloat(this.props.getProperty("stormday_min_value"));
			this.max_value = Integer.parseInt(this.props.getProperty("stormday_max_value"));
			this.eventDuration = Float.parseFloat(this.props.getProperty("stormday_max_duration"));
		} else {
			// Take default values
			this.orderID = StormDayEvent.ORDER_ID;
			this.min_value = 5.5f;
			this.max_value = 35.0f;
			this.eventDuration = 4.0f;
		}
	}
}
