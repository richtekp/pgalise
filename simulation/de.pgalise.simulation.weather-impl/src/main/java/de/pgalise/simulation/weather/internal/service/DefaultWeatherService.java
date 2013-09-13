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
 
package de.pgalise.simulation.weather.internal.service;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.event.weather.WeatherEventEnum;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataMap;
import de.pgalise.simulation.weather.internal.modifier.WeatherStrategyContext;
import de.pgalise.simulation.weather.internal.positionconverter.LinearWeatherPositionConverter;
import de.pgalise.simulation.weather.internal.util.comparator.WeatherStrategyComparator;
import de.pgalise.simulation.weather.model.MutableStationData;
import de.pgalise.simulation.weather.modifier.WeatherDayEventModifier;
import de.pgalise.simulation.weather.modifier.WeatherStrategy;
import de.pgalise.simulation.weather.parameter.WeatherParameter;
import de.pgalise.simulation.weather.parameter.WeatherParameterBase;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.parameter.WindStrength;
import de.pgalise.simulation.weather.positionconverter.WeatherPositionConverter;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.simulation.weather.util.WeatherStrategyHelper;
import java.sql.Date;
import java.sql.Time;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 * The {@link WeatherService} coordinates the {@link WeatherParameter} requests 
 * and provides access with the help of the {@link WeatherLoader} to the needed 
 * weather information. An implementation of that interface is the 
 * {@link DefaultWeatherService}. This implementation realizes a 
 * synchronization concept between the loading of new data and responding to 
 * value requests. Due to the fact that the component should not allocate too 
 * much space during runtime, it provides only a specific amount of modified 
 * weather information. If there is a request for {@link WeatherParameter} 
 * values, which cannot be calculated from the provided data, the 
 * WeatherService try to get the needed data automatically. Only one thread can 
 * get the new information from the database. At that moment the other requests 
 * have to wait till the new data are available. Many threads can request 
 * specific {@link WeatherParameter} values simultaneously. At the time of the 
 * WeatherService creation, the class loads the {@link WeatherParameter} by the 
 * help of the {@link WeatherParameterEnum} automatically. There are eleven 
 * {@link WeatherParameter} available. Some of them are calculated by other 
 * {@link WeatherParameter}. These parameters will be cached by the
 * {@link WeatherService} for another request to reduce the CPU processing. An 
 * example is the parameter {@link WindStrength}.<br />
 * <br />
 * Due to the fact that the component should not allocate too much space during 
 * runtime, it provides only a specific amount of modified weather information. 
 * If there is a request for weather parameter values, which cannot be 
 * calculated from the provided data, the {@link DefaultWeatherService} try to 
 * get the needed data automatically. Requests for dates that not bear on the 
 * simulation date interval be ignored by the component and an exception occurs
 * during runtime.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0
 */
public class DefaultWeatherService implements WeatherService {

	/**
	 * Map with cached parameters
	 */
	private HashMap<WeatherParameterEnum, Map<Long, Number>> cachedParameters;

	/**
	 * City of the simulation
	 */
	private City city;

	/**
	 * Helper for calculations with the grid
	 */
	private WeatherPositionConverter gridConverter;

	/**
	 * Timestamp of the current loaded date
	 */
	private long loadedTimestamp = -1;

	/**
	 * Loader for weather data
	 */
	private WeatherLoader loader;

	/**
	 * Map with all weather parameters
	 */
	private HashMap<WeatherParameterEnum, WeatherParameter> parameters;

	/**
	 * Position of the reference point
	 */
	private Coordinate referencePosition;

	/**
	 * Values linked to the reference point
	 */
	private WeatherMap referenceValues;

	/**
	 * Semaphore
	 */
	private final Semaphore semaphore = new Semaphore(1, true);

	/**
	 * Timestamp for the simulation end
	 */
	private long simEndTimestamp = -1;

	/**
	 * Planned event modifier
	 */
	private List<WeatherStrategyHelper> plannedEventModifiers;

	/**
	 * Constructor
	 * 
	 * @param city
	 *            City
	 * @param loader  
	 */
	public DefaultWeatherService(City city, WeatherLoader loader) {
		if (city == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("city"));
		}
		this.referencePosition = city.getReferencePoint();
		this.city = city;
		this.loader = loader;

		// Init maps
		this.referenceValues = null;
		this.parameters = new HashMap<>();
		this.cachedParameters = new HashMap<>();
		this.gridConverter = new LinearWeatherPositionConverter(city.getReferenceArea());
		this.plannedEventModifiers = new ArrayList<>();

		// Add parameters
		try {
			this.initParameters();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void _clearValues() {
		this.clearValues();
	}

	@Override
	public void addNewWeather(long startTimestamp, long endTimestamp, boolean takeNormalData,
			List<WeatherStrategyHelper> strategyList) {
		// We have only weather data after
		if (startTimestamp < 1057528800000L) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForMustBetween("startTimestamp",
					startTimestamp, 1057528800000.0));
		} else if (endTimestamp < startTimestamp) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForMustBetween("endTimestamp", endTimestamp,
					startTimestamp));
		}

		// Add weather data
		try {
			// Only one thread can add new weather
			this.semaphore.acquire();
			this.internalAddNewWeather(startTimestamp, endTimestamp, takeNormalData, strategyList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Free semaphore
			this.semaphore.release();
		}
	}

	@Override
	public void addNextWeather() {
		// Has a date been simulated before?
		if (this.loadedTimestamp < 1) {
			throw new RuntimeException("Service was not started before!");
		}

		try {
			// Only one thread can add new weather
			this.semaphore.acquire();
			this.internalAddNextWeather();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Free semaphore
			this.semaphore.release();
		}
	}

	@Override
	public boolean checkDate(long timestamp) {
		if (timestamp < 1) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotPositive("timestamp", false));
		}

		// Check date
		return this.loader.checkStationDataForDay(timestamp);
	}

	@Override
	public void deployStrategy(WeatherStrategy strategy)   {
		// There is no weather data added
		if ((this.referenceValues == null) || this.referenceValues.isEmpty()) {
			throw new IllegalStateException("No weather data has been added so far");
		}

		if (WeatherEventEnum.SIMULATION_EVENTS.contains(strategy.getType())) {
			// Remember simulation Event?
			if (!this.isPlannedEventType(strategy.getType())) {
				// Execute
				WeatherStrategyContext context = new WeatherStrategyContext(strategy);
				context.execute(this.referenceValues, this.city, this.loadedTimestamp);

				// Plan event for further days
				this.plannedEventModifiers.add(new WeatherStrategyHelper(strategy, this.loadedTimestamp));
			}
		} else {
			WeatherDayEventModifier dayEvent = (WeatherDayEventModifier) strategy;

			// Execute event?
			if (DateConverter.isTheSameDay(dayEvent.getEventTimestamp(), this.loadedTimestamp)) {
				WeatherStrategyContext context = new WeatherStrategyContext(strategy);
				context.execute(this.referenceValues, this.city, this.loadedTimestamp);
			} else {
				// Plan event for further days
				this.plannedEventModifiers.add(new WeatherStrategyHelper(strategy, dayEvent.getEventTimestamp()));
			}
		}

	}

	public HashMap<WeatherParameterEnum, Map<Long, Number>> getCachedParameters() {
		return this.cachedParameters;
	}

	@Override
	public long getLoadedTimestamp() {
		return this.loadedTimestamp;
	}

	public HashMap<WeatherParameterEnum, WeatherParameter> getParameters() {
		return this.parameters;
	}

	@Override
	public Coordinate getReferencePosition() {
		return this.referencePosition;
	}

	@Override
	public WeatherMap getReferenceValues() {
		return this.referenceValues;
	}

	public long getSimEndTimestamp() {
		return this.simEndTimestamp;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Number> T getValue(WeatherParameterEnum key, long time) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("key"));
		} else if (time <= 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotPositive("time", false));
		}

		try {
			this.semaphore.acquire(1);

			// Is the correct weather data set?
			if (!DateConverter.isTheSameDay(this.loadedTimestamp, time)) {
				// Load new data
				this.internalAddNextWeather();
			}

			// Cached Value?
			if (key.isCachedParameter()) {
				Map<Long, Number> cache = this.cachedParameters.get(key);

				// Is available?
				if (!cache.isEmpty() && cache.containsKey(time)) {
					return (T) cache.get(time);
				}

				// Add the new value
				T value = this.parameters.get(key).getValue(time);
				cache.put(time, value);

				return value;
			}

			// Return the value
			return this.parameters.get(key).getValue(time);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.semaphore.release();
		}

		// No return -> ERROR
		throw new RuntimeException("Value can not be returned!");
	}

	@Override
	public <T extends Number> T getValue(WeatherParameterEnum key, long time, Coordinate position)
			throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("key"));
		} else if ((position == null)) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("position"));
		} else if ((position.x < 0) || (position.y < 0)) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotPositive("position (x or y)", true));
		} else if (time <= 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotPositive("time", false));
		}

		// Get the reference value
		T value = this.getValue(key, time);

		// Calculate a new value for the given position
		return this.gridConverter.getValue(key, time, position, value);
	}

	@Override
	public void initValues() {
		this.simEndTimestamp = -1;
		this.plannedEventModifiers.clear();

		// Clear the values for the date
		this.clearValues();
	}

	@Override
	public void setCity(City city) {
		this.city = city;
	}

	public void setLoadedTimestamp(long loadedTimestamp) {
		this.loadedTimestamp = loadedTimestamp;
	}

	public void setSimEndTimestamp(long simEndTimestamp) {
		this.simEndTimestamp = simEndTimestamp;
	}

	/**
	 * Changes the weather informations to the next day
	 */
	private void changeReferenceValuesToNextDay() {
		WeatherMap map = new StationDataMap();

		long nextDay;
		for (MutableStationData weather : this.referenceValues.values()) {
			nextDay = weather.getMeasureTime().getTime()+ DateConverter.NEXT_DAY_IN_MILLIS;
			// Change date
			weather.setMeasureTime(new Time(nextDay));
			weather.setMeasureDate(new Date(nextDay));

			// Add to new map
			map.put(nextDay, weather);
		}

		// Change
		this.referenceValues = map;
	}

	/**
	 * Clear all stored data to use new data
	 */
	private void clearValues() {
		// Delete generel data
		this.referenceValues = null;
		this.loadedTimestamp = -1;

		// Delete cached values
		for (WeatherParameterEnum enumElement : this.cachedParameters.keySet()) {
			this.cachedParameters.get(enumElement).clear();
		}
	}

	/**
	 * Initiate all parameters
	 * 
	 * @throws Exception
	 *             There is a parameter that can not be initiated.
	 */
	private void initParameters() throws Exception {
		for (WeatherParameterEnum enumElement : WeatherParameterEnum.values()) {
			// Add parameter
			WeatherParameterBase type = enumElement.getValueType().getConstructor(WeatherService.class)
					.newInstance(this);
			this.parameters.put(enumElement, type);

			// Add cached parameter
			if (enumElement.isCachedParameter()) {
				this.cachedParameters.put(enumElement, new HashMap<Long, Number>());
			}
		}
	}

	/**
	 * Add new weather informations
	 * 
	 * @param startTimestamp
	 *            Start timestamp weather will be loaded from the next morning after 00:00
	 * @param endTimestamp
	 *            End timestamp whether will be loaded until the next morning after 00:00
	 * @param takeNormalData
	 *            Option to take normal data
	 * @param strategyList
	 *            List with strategies to modify the data
	 * @throws NoWeatherDataFoundException
	 *             No data found
	 */
	private void internalAddNewWeather(long startTimestamp, long endTimestamp, boolean takeNormalData,
			List<WeatherStrategyHelper> strategyList) {
		// Delete all values
		this.initValues();

		// Convert dates to 00:00:00
		long startTimestampMidnight = DateConverter.convertTimestampToMidnight(startTimestamp);
		this.simEndTimestamp = DateConverter.convertTimestampToMidnight(endTimestamp)
				+ DateConverter.NEXT_DAY_IN_MILLIS;

		// Change loader
		this.loader.setLoadOption(takeNormalData);

		// Save start strategies
		if ((strategyList != null) && !strategyList.isEmpty()) {
			this.plannedEventModifiers.addAll(strategyList);
		}

		// Add new weather data
		this.loadWeather(startTimestampMidnight);
	}

	/**
	 * Replace the weather information with data of the next day
	 */
	private void internalAddNextWeather() {
		// Calculate next day
		long nextday = this.loadedTimestamp + DateConverter.NEXT_DAY_IN_MILLIS;

		// Enddate?
		if ((this.simEndTimestamp > 0) && (nextday > this.simEndTimestamp)) {
			throw new RuntimeException("Timestamp for the simulation end is overstepped!");
		}

		// Check weather informations
		if (!this.checkDate(nextday)) {

			/*
			 * Take existing map
			 */

			// Check existing map
			if ((this.referenceValues == null) || this.referenceValues.isEmpty()) {
				throw new IllegalStateException();
			}

			// Change map to next day
			this.changeReferenceValuesToNextDay();
			this.loadedTimestamp = nextday;

		} else {
			/*
			 * Get new weather
			 */
			// Add new weather data
			this.loadWeather(nextday);
		}
	}

	/**
	 * Returns true if the event type is planned for further events
	 * 
	 * @param type
	 *            WeatherEventEnum
	 * @return true if the event type is planned for further events else false
	 */
	private boolean isPlannedEventType(WeatherEventEnum type) {
		for (WeatherStrategyHelper helper : this.plannedEventModifiers) {
			if (helper.getStrategy().getType().equals(type)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Loads new weather data for the given date
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @throws NoWeatherDataFoundException
	 *             No data found for the given date
	 */
	private void loadWeather(long timestamp) {
		/*
		 * Load data
		 */

		// Zum Testen der Threads
		// try {
		// System.out.println("Wait");
		// Thread.sleep(2000);
		// System.out.println("End wait");
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

		// Get data from database
		WeatherMap map = this.loader.loadStationData(timestamp);

		/*
		 * Use modifiers
		 */

		// Use strategy context
		WeatherStrategyContext context = new WeatherStrategyContext();

		if (!this.plannedEventModifiers.isEmpty()) {

			// Get strategies for the current day
			List<WeatherStrategy> dayStrategies = new ArrayList<>();
			Iterator<WeatherStrategyHelper> iterator = this.plannedEventModifiers.iterator();
			while (iterator.hasNext()) {
				WeatherStrategyHelper strategyHelper = iterator.next();
				if (strategyHelper != null) {
					if (WeatherEventEnum.SIMULATION_EVENTS.contains(strategyHelper.getStrategy().getType())) {
						dayStrategies.add(strategyHelper.getStrategy());
					} else if (DateConverter.isTheSameDay(strategyHelper.getTimestamp(), timestamp)) {
						dayStrategies.add(strategyHelper.getStrategy());
						iterator.remove();
					}
				}
			}

			// Use strategies to modify data
			if (!dayStrategies.isEmpty()) {
				// Sort by orderID
				Collections.sort(dayStrategies, new WeatherStrategyComparator());

				// Execute strategy
				for (WeatherStrategy strategy : dayStrategies) {
					context.setStrategy(strategy);
					context.execute(map, this.city, timestamp);
				}
			}
		}

		/*
		 * Save and clear all
		 */

		// Clear all values for that day
		this.clearValues();
		// Set new weather data
		this.loadedTimestamp = timestamp;
		this.referenceValues = map;

		// Alle sortiert ausgeben (zum Testen)
		// Vector<Long> times = new Vector<Long>(this.referenceValues.keySet());
		// Collections.sort(times);
		// for (Long time : times) {
		// Weather w = this.referenceValues.get(time);
		// System.out.println(new Date(w.getDate()) + " - " + new Time(w.getTime()) + " - "
		// + new Timestamp(w.getTimestamp()));
		// }
	}

	@Override
	public Unit<Temperature> getTemperatureUnit() {
		return SI.CELSIUS;
	}
}
