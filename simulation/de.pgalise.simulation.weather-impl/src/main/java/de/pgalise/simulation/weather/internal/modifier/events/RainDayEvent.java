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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.event.weather.WeatherEventEnum;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.model.MutableStationData;
import de.pgalise.simulation.weather.model.StationData;
import de.pgalise.simulation.weather.modifier.WeatherDayEventModifier;
import de.pgalise.simulation.weather.modifier.WeatherMapModifier;
import de.pgalise.simulation.weather.modifier.WeatherStrategy;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.util.DateConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * Changes the weather data for a rain day ({@link WeatherMapModifier} and {@link WeatherStrategy}).<br />
 * <br />
 * The file weather_decorators.properties describes the default properties for the implemented modifier. If no
 * parameters are given in the constructor of an implemented modifier, the standard properties of the file will be used.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (02.07.2012)
 */
public final class RainDayEvent extends WeatherDayEventModifier {

	/**
	 * Event type
	 */
	public static final WeatherEventEnum TYPE = WeatherEventEnum.RAINDAY;

	/**
	 * Parameter which will be changed
	 */
	public static final WeatherParameterEnum CHANGE_PARAMETER = WeatherParameterEnum.PRECIPITATION_AMOUNT;

	/**
	 * order id
	 */
	public static final int ORDER_ID = 6;

	/**
	 * Logger (for tests)
	 */
	private static Logger log = LoggerFactory.getLogger(RainDayEvent.class);

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -5324497252495371198L;

	/**
	 * Max rain value
	 */
	public int max_value;

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
	 * @param weatherLoader  
	 */
	public RainDayEvent(long seed, long time, Properties props, Float value, Float duration, WeatherLoader weatherLoader) {
		super(seed, time, props, value, duration, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 * @param props
	 *            Properties
	 * @param weatherLoader  
	 */
	public RainDayEvent(long seed, Properties props, WeatherLoader weatherLoader) {
		super(seed, props, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 * @param weatherLoader  
	 */
	public RainDayEvent(long seed, WeatherLoader weatherLoader) {
		super(seed, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param map
	 *            WeatherMap
	 * @param seed
	 *            Seed for random generators
	 * @param weatherLoader  
	 */
	public RainDayEvent(WeatherMap map, long seed, WeatherLoader weatherLoader) {
		super(map, seed, weatherLoader);
	}

	/*
	 * (non-Javadoc)
	 * @see de.pgalise.simulation.weather.database.Weather#deployChanges()
	 */
	@Override
	public void deployChanges() {
		// Calculate value
		if (this.eventValue != null) {
			this.maxValue = this.eventValue;
		} else {
			do {
				this.maxValue = this.getRandomDouble(this.max_value);
			} while (this.maxValue < 0.1);
		}

		// Get time
		if (this.eventTimestamp < 1) {
			this.eventTimestamp = this.getRandomTimestamp(this.simulationTimestamp);
		}

		StationData max = this.getNextWeatherForTimestamp(this.eventTimestamp);

		long minTime, maxTime, minTimeInterpolate, maxTimeInterpolate, actTime;

		// if (max.getPrecipitationAmount() == 0) {
		//
		// /* No Rain */
		//
		// log.debug("Max. value of event (" + new Time(max.getTimestamp()) + "): " + this.maxValue + " (actual: "
		// + max.getPrecipitationAmount() + ")");
		//
		// /*
		// * Create limits
		// */
		//
		// // Event limits
		// long midTime = max.getTime();
		// minTime = this.getMinHour(max.getTime(), this.eventDuration);
		// log.debug("Min. time of event: " + minTime + " / " + new Time(minTime) + " (Duration: "
		// + this.eventDuration + ")");
		// maxTime = this.getMaxHour(max.getTime(), this.eventDuration);
		// log.debug("Max. time of event: " + maxTime + " / " + new Time(maxTime) + " (Duration: "
		// + this.eventDuration + ")");
		//
		// // Get values
		// float minActRainValue = this.getNextWeatherForTimestamp(minTime + max.getDate()).getPrecipitationAmount();
		// float maxActRainValue = this.getNextWeatherForTimestamp(maxTime + max.getDate()).getPrecipitationAmount();
		//
		// // Sort values
		// Vector<Long> times = new Vector<Long>(this.map.keySet());
		// Collections.sort(times);
		//
		// /*
		// * Add to reference values
		// */
		// float value;
		// for (Long time : times) {
		// // Get weather
		// Weather weather = this.map.get(time);
		// actTime = weather.getTime();
		//
		// // Between the interval?
		// if ((actTime < minTime) || (actTime > maxTime)) {
		// continue;
		// }
		//
		// // Change parameters
		// if (actTime > midTime) {
		// value = (float) interpolate(midTime, this.maxValue, maxTime, maxActRainValue, actTime);
		// } else {
		// value = (float) interpolate(minTime, minActRainValue, midTime, this.maxValue, actTime);
		// }
		// log.debug("Value changes: " + value + " (actual: " + weather.getPrecipitationAmount() + ")");
		// weather.setPrecipitationAmount(value);
		// }
		//
		// } else {

		/* Rain is there */

		// Calculate difference between max (reference) and max (event)
		float maxDifference = this.maxValue - max.getPrecipitationAmount();

		RainDayEvent.log.debug("Max. value of event (" + max.getMeasureTime().getTime() + "): " + this.maxValue
				+ " (actual: " + max.getPrecipitationAmount() + " ; difference: " + maxDifference + ")");

		// If there is a difference
		if (maxDifference > 0) {

			/*
			 * Create limits
			 */
			// Event limits
			minTime = this.getMinHour(max.getMeasureTime(), this.eventDuration);
			RainDayEvent.log.debug("Min. time of event: " + new Date(minTime) + " (Duration: " + this.eventDuration
					+ ")");
			maxTime = this.getMaxHour(max.getMeasureTime(), this.eventDuration);
			RainDayEvent.log.debug("Max. time of event: " + new Date(maxTime) + " (Duration: " + this.eventDuration
					+ ")");

			// Interpolate limits for difference
			minTimeInterpolate = minTime + DateConverter.HOUR;
			maxTimeInterpolate = maxTime - DateConverter.HOUR;

			// Sort values
			List<Long> times = new ArrayList<>(this.map.keySet());
			Collections.sort(times);

			/*
			 * Add to reference values
			 */
			float value, difference;
			for (Long time : times) {
				// Get weather
				MutableStationData weather = this.map.get(time);
				actTime = weather.getMeasureTime().getTime();

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
				value = weather.getPrecipitationAmount() + difference;
				// log.debug("Value changes: " + value + " (actual: " + weather.getPrecipitationAmount() + ")");
				weather.setPrecipitationAmount(value);
			}
			// }
		}

		// Super class
		super.deployChanges();
	}

	public float getMaxValue() {
		return this.maxValue;
	}

	@Override
	public WeatherEventEnum getType() {
		return RainDayEvent.TYPE;
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
			this.orderID = Integer.parseInt(this.props.getProperty("rainday_order_id"));
			this.max_value = Integer.parseInt(this.props.getProperty("rainday_max_value"));
			this.eventDuration = Float.parseFloat(this.props.getProperty("rainday_max_duration"));
		} else {
			// Take default values
			this.orderID = RainDayEvent.ORDER_ID;
			this.max_value = 5;
			this.eventDuration = 5.0f;
		}
	}
}
