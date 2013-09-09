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
 
package de.pgalise.simulation.weather.modifier;

import java.sql.Date;
import java.sql.Time;
import java.util.Properties;

import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.model.StationData;
import de.pgalise.simulation.weather.util.DateConverter;

/**
 * The {@link WeatherMap} serves as the root for the weather modifiers that are all derived from the class
 * {@link WeatherMapModifier}. For that reason the decorator pattern is realized. Furthermore, the decorators can also
 * function as a {@link WeatherStrategy} because the strategy pattern is implemented. The different weather modifiers
 * are separated into two groups: Some are derived from the class {@link WeatherDayEventModifier} and others expand the
 * class {@link WeatherSimulationEventModifier}. The event modifiers, which expand the {@link WeatherDayEventModifier},
 * can be activated more than one time for each day during a simulation runtime.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (02.07.2012)
 */
public abstract class WeatherDayEventModifier extends WeatherMapModifier {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 2216988605839775963L;

	/**
	 * Interpolate two values
	 * 
	 * @param minTime
	 *            min time
	 * @param minValue
	 *            min value
	 * @param maxTime
	 *            max time
	 * @param maxValue
	 *            max value
	 * @param actTime
	 *            time for the new value
	 * @return interpolated value
	 */
	protected static double interpolate(long minTime, float minValue, long maxTime, float maxValue, long actTime) {
		/*
		 * Linear interpolation: f(x)=f0+((f1-f0)/(x1-x0))*(x-x0)
		 */
		double value = minValue + (((maxValue - minValue) / (maxTime - minTime)) * (actTime - minTime));

		return WeatherMapModifier.round(value, 3);
	}

	/**
	 * Timestamp of the simulation event
	 */
	protected long eventTimestamp = -1;

	/**
	 * Specified min or max value of the event
	 */
	protected Float eventValue = null;

	/**
	 * Maximal duration of the event
	 */
	protected Float eventDuration;

	/**
	 * Constructor
	 * 
	 * @param props
	 *            Properties
	 * @param seed
	 *            Seed for random generators
	 * @param time
	 *            Event timestamp
	 * @param value
	 *            Specified value
	 * @param duration
	 *            Maximal duration of the event
	 */
	public WeatherDayEventModifier(long seed, long time, Properties props, Float value, Float duration,
			WeatherLoader weatherLoader) {
		super(seed, props, weatherLoader);
		this.eventTimestamp = time;
		this.eventValue = value;
		this.eventDuration = duration;
	}

	/**
	 * Constructor
	 * 
	 * @param props
	 *            Properties
	 * @param seed
	 *            Seed for random generators
	 */
	public WeatherDayEventModifier(long seed, Properties props, WeatherLoader weatherLoader) {
		super(seed, props, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 */
	public WeatherDayEventModifier(long seed, WeatherLoader weatherLoader) {
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
	public WeatherDayEventModifier(WeatherMap map, long seed, WeatherLoader weatherLoader) {
		super(map, seed, weatherLoader);
	}

	public Float getEventDuration() {
		return this.eventDuration;
	}

	public long getEventTimestamp() {
		return this.eventTimestamp;
	}

	public Float getEventValue() {
		return this.eventValue;
	}

	public void setEventDuration(Float eventDuration) {
		this.eventDuration = eventDuration;
	}

	public void setEventTimestamp(long timestamp) {
		this.eventTimestamp = timestamp;
	}

	public void setEventValue(Float eventValue) {
		this.eventValue = eventValue;
	}

	/**
	 * Returns the maximal hour (in millis)
	 * 
	 * @param time
	 *            Timestamp
	 * @param duration
	 *            Duration in hour
	 * @return Timestamp
	 */
	protected long getMaxHour(Time time, float duration) {
		long max = (long) (time.getTime() + ((duration / 2) * DateConverter.HOUR));
		long nextDay = DateConverter.getCleanDate(time.getTime() + DateConverter.NEXT_DAY_IN_MILLIS);

		return (max > nextDay) ? nextDay - WeatherMap.INTERPOLATE_INTERVAL : max;
	}

	/**
	 * Returns the minimal hour (in millis)
	 * 
	 * @param time
	 *            Timestamp
	 * @param duration
	 *            Duration in hour
	 * @return Timestamp
	 */
	protected long getMinHour(Time time, float duration) {
		long min = (long) (time.getTime() - ((duration / 2) * DateConverter.HOUR));
		long currentDay = DateConverter.getCleanDate(time.getTime());
		return (min < currentDay) ? currentDay + DateConverter.NEXT_DAY_IN_MILLIS : min;
	}

	/**
	 * Returns the next weather object to the timestamp.
	 * 
	 * @param timestamp
	 *            Event time
	 * @return weather object
	 */
	protected StationData getNextWeatherForTimestamp(long timestamp) {
		// Get weather
		StationData weather = this.map.get(timestamp);
		if (weather == null) {

			// Calculate next minute
			long newTime = timestamp + (WeatherMap.INTERPOLATE_INTERVAL - (timestamp % WeatherMap.INTERPOLATE_INTERVAL));
			weather = this.map.get(newTime);

			// Get weather
			if (weather == null) {

				// Look for the last 5 Minutes
				for (int i = 0; i < 5; i++) {
					newTime -= WeatherMap.INTERPOLATE_INTERVAL;
					weather = this.map.get(newTime);
					if (weather != null) {
						break;
					}
				}

				if (weather == null) {
					// Load new Data
					throw new RuntimeException("Keine Wetterinformationen zum Zeitpunkt " + new Date(newTime) + " - "
							+ new Time(newTime) + " gefunden.");
				}
			}
		}

		return weather;
	}

	/**
	 * Returns the next weather object to the timestamp. If the timestamp is later than maxTimeValue, the weather for
	 * maxTimeValue will be returned. If the timestamp is earlier than minTimeValue, the weather for minTimeValue will
	 * be returned.
	 * 
	 * @param timestamp
	 *            Event time
	 * @param maxTimeValue
	 *            Maximal hour
	 * @param minTimeValue
	 *            Minimal hour
	 * @return weather object
	 */
	protected StationData getNextWeatherForTimestamp(long timestamp, int maxTimeValue, int minTimeValue) {
		StationData weather = null;
		int hour = DateConverter.getHourOfDay(timestamp);
		if ((hour >= 0) && (hour < minTimeValue)) {
			// Between 0 and min?

			// Calculate for minTimeValue
			long mintime = DateConverter.HOUR * minTimeValue;
			long newTime = timestamp + (mintime - (timestamp % mintime));
			weather = this.map.get(newTime);

		} else if ((hour > maxTimeValue) && (hour < 24)) {
			// Between max and 24?

			// Calculate for maxTimeValue
			long maxtime = DateConverter.HOUR * maxTimeValue;
			long newTime = timestamp + (maxtime - (timestamp % maxtime));
			weather = this.map.get(newTime);

		} else {
			// Correct interval?
			weather = this.getNextWeatherForTimestamp(timestamp);
		}

		return weather;
	}

	/**
	 * Returns a random hour between min and max.
	 * 
	 * @param min
	 *            Min hour
	 * @param max
	 *            Max hour
	 * @return Random hour
	 */
	protected long getRandomHour(int min, int max) {
		int limit = max - min;
		int r = this.randomGen.nextInt(limit);

		return ((r + min) * DateConverter.HOUR);
	}

	/**
	 * Returns a random timestamp.
	 * 
	 * @param day
	 *            Timestamp of the day
	 * @return random timestamp
	 */
	protected long getRandomTimestamp(long day) {
		int hour = this.randomGen.nextInt(23);
		int min = this.randomGen.nextInt(59);

		return day + (hour * DateConverter.HOUR) + (min * DateConverter.MINUTE);
	}
}
