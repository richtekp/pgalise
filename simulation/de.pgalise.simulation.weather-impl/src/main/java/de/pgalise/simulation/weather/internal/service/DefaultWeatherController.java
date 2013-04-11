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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.GPSMapper;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventEnum;
import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.modifier.events.ColdDayEvent;
import de.pgalise.simulation.weather.internal.modifier.events.HotDayEvent;
import de.pgalise.simulation.weather.internal.modifier.events.RainDayEvent;
import de.pgalise.simulation.weather.internal.modifier.events.StormDayEvent;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.CityClimateModifier;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.modifier.WeatherStrategy;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.simulation.weather.util.WeatherStrategyHelper;
import de.pgalise.util.vector.Vector2d;

/**
 * The main interaction point of the component Weather is the interface {@link WeatherController} that represents the
 * weather environment controller of the simulation. Its public methods can be called by other components. Therefore,
 * the controller is implemented as an EJB. For one thing the controller sends all requests to the interface
 * {@link WeatherService} and the responses back to the other components, for another thing it inherits from the generic
 * {@link Controller} interface and implements its state transitions. So this controller acts as intermediary between
 * components of the simulation and the {@link WeatherService}. The interface {@link WeatherControllerLocal} can be used
 * for local communication.<br />
 * <br />
 * If the {@link DefaultWeatherController} tries to start, it checks the availability of weather information regarding
 * the start date of the simulation. An exception will occur if the component cannot connect to the database or found
 * any information. It is mandatory that there is weather data to the first simulation day.
 * 
 * @author Andreas Rehfeldt
 * @author Timo
 * @author Mustafa
 * @version 1.0 (Aug 24, 2012)
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.weather.service.WeatherController")
@Local(WeatherControllerLocal.class)
@Remote(WeatherController.class)
public final class DefaultWeatherController extends AbstractController implements WeatherControllerLocal {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultWeatherController.class);
	private static final String NAME = "WeatherController";

	/**
	 * 1 day in millis.
	 */
	private static final long ONE_DAY_IN_MILLIS = 86400000;

	/**
	 * File path for property file
	 */
	private static final String PROPERTIES_FILE_PATH = "/weather_decorators.properties";

	/**
	 * Properties for decorators
	 */
	private Properties decorator_props = null;

	/**
	 * First time on a new day.
	 */
	private long nextNewDayInMillis;

	/**
	 * The start timestamp.
	 */
	private long startTimestamp;

	/**
	 * WeatherService
	 */
	private WeatherService weatherservice;

	@EJB
	private ServiceDictionary serviceDictionary;

	@EJB
	private WeatherLoader weatherLoader;

	@EJB
	private GPSMapper mapper;

	/**
	 * Random Seed Service
	 */
	private RandomSeedService randomSeedService;
	
	private InitParameter initParameter;

	/**
	 * Constructor
	 */
	public DefaultWeatherController() {
		// Read propsInputStream propInFile
		try (InputStream propInFile = WeatherLoader.class
				.getResourceAsStream(DefaultWeatherController.PROPERTIES_FILE_PATH)) {
			this.decorator_props = new Properties();
			this.decorator_props.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void checkDate(long timestamp) {
		// Check date
		if (!this.weatherservice.checkDate(timestamp)) {
			throw new IllegalArgumentException("There is no data available.");
		}
	}

	/**
	 * Creates a weather strategy from the given enum element
	 * 
	 * @param enumElement
	 *            WeatherEventEnum
	 * @param timestamp
	 *            Timestamp
	 * @param value
	 *            Specified value for event
	 * @param duration
	 *            Duration of the event
	 * @return weather strategy
	 * @throws IllegalArgumentException
	 *             enumElement is null or weather strategy is not implemented
	 */
	public WeatherStrategy createStrategyFromEnum(WeatherEventEnum enumElement, long timestamp, Float value,
			Float duration) throws IllegalArgumentException {
		if (enumElement == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("enumElement"));
		}

		/*
		 * INFO: You have to add new weather strategies here!
		 */
		switch (enumElement) {
			case RAINDAY:
				return new RainDayEvent(this.randomSeedService.getSeed(RainDayEvent.class.getName()), timestamp,
						this.decorator_props, value, duration, this.weatherLoader);
			case COLDDAY:
				return new ColdDayEvent(this.randomSeedService.getSeed(ColdDayEvent.class.getName()), timestamp,
						this.decorator_props, value, duration, this.weatherLoader);
			case HOTDAY:
				return new HotDayEvent(this.randomSeedService.getSeed(HotDayEvent.class.getName()), timestamp,
						this.decorator_props, value, duration, this.weatherLoader);
			case STORMDAY:
				return new StormDayEvent(this.randomSeedService.getSeed(StormDayEvent.class.getName()), timestamp,
						this.decorator_props, value, duration, this.weatherLoader);
			case CITYCLIMATE:
				// Events with -1 are not considered
				return ((value != null) && (value < 0)) ? null : new CityClimateModifier(
						this.randomSeedService.getSeed(CityClimateModifier.class.getName()), this.decorator_props,
						this.weatherLoader);
			case REFERENCECITY:
				// Events with -1 are not considered
				return ((value != null) && (value < 0)) ? null : new ReferenceCityModifier(
						this.randomSeedService.getSeed(ReferenceCityModifier.class.getName()), this.decorator_props,
						this.weatherLoader);
			default:
				throw new IllegalArgumentException("No weather strategy found!");
		}
	}

	/**
	 * Creates a list of weather strategies from the given enum list
	 * 
	 * @param eventList
	 *            List of WeatherEventHelper
	 * @return list of weather strategies
	 * @throws IllegalArgumentException
	 */
	public List<WeatherStrategyHelper> createStrategyList(List<WeatherEventHelper> eventList)
			throws IllegalArgumentException {
		if (eventList == null) {
			return null;
		}

		// Create strategies
		List<WeatherStrategyHelper> strategies = new ArrayList<>();
		for (WeatherEventHelper event : eventList) {
			WeatherStrategy strategy = this.createStrategyFromEnum(event.getEvent(), event.getTimestamp(),
					event.getValue(), event.getDuration());
			if (strategy != null) {
				strategies.add(new WeatherStrategyHelper(strategy, event.getTimestamp()));
			}
		}

		// Return
		return strategies;
	}

	public long getNextNewDayInMillis() {
		return this.nextNewDayInMillis;
	}

	@Override
	public Vector2d getReferencePosition() {
		return this.weatherservice.getReferencePosition();
	}

	public long getStartTimestamp() {
		return this.startTimestamp;
	}

	@Override
	public Number getValue(final WeatherParameterEnum key, final long timestamp, final Vector2d position) {
		return DefaultWeatherController.this.weatherservice.getValue(key, timestamp, position);
	}

	public WeatherService getWeatherservice() {
		return this.weatherservice;
	}

	public void setNextNewDayInMillis(long nextNewDayInMillis) {
		this.nextNewDayInMillis = nextNewDayInMillis;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	/**
	 * Start the weather service or initialize this service
	 * 
	 * @param city
	 *            City
	 */
	public void startWeatherService(City city) {
		if (this.weatherservice == null) {
			this.weatherservice = new DefaultWeatherService(city, this.mapper, this.weatherLoader);
		} else {
			this.weatherservice.initValues();
			this.weatherservice.setCity(city);
		}
	}

	/**
	 * Returns the first new date after the timestamp. e.g. for 2012-11-09 14:44:00.0 and interval of 7 seconds it will
	 * return 2012-11-10 00:00:02.0
	 * 
	 * @param timestamp
	 *            in millis
	 * @param interval
	 *            in millis
	 * @return Timestamp of the next date
	 */
	@SuppressWarnings("unused")
	private long getNextNewDateTimestamp(long timestamp, long interval) {
		if (timestamp == this.startTimestamp) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(timestamp);

			int currentHour = calendar.get(Calendar.HOUR);
			int currentMin = calendar.get(Calendar.MINUTE);
			int currentSec = calendar.get(Calendar.SECOND);
			int currentMillis = calendar.get(Calendar.MILLISECOND);

			while (true) {
				calendar.setTimeInMillis(timestamp += interval);

				int tmpHour = calendar.get(Calendar.HOUR);
				int tmpMin = calendar.get(Calendar.MINUTE);
				int tmpSec = calendar.get(Calendar.SECOND);
				int tmpMillis = calendar.get(Calendar.MILLISECOND);

				if ((tmpHour <= currentHour) && (tmpMin <= currentMin) && (tmpSec <= currentSec)
						&& (tmpMillis <= currentMillis)) {
					return calendar.getTimeInMillis();

				} else {
					currentHour = tmpHour;
					currentMin = tmpMin;
					currentSec = tmpSec;
					currentMillis = tmpMillis;
				}
			}
		} else {
			return timestamp + DefaultWeatherController.ONE_DAY_IN_MILLIS;
		}
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		// Set random seed service
		this.randomSeedService = this.serviceDictionary.getRandomSeedService();
		this.initParameter = param;
	}

	@Override
	protected void onReset() {
		// Do nothing
	}

	@Override
	protected void onResume() {
		// Nothing to do
	}

	@Override
	protected void onStart(StartParameter param) {
		// Save values
		this.startWeatherService(param.getCity());

		// Log
		DefaultWeatherController.log
				.debug("Start: " + this.initParameter.getStartTimestamp() + " - End: " + this.initParameter.getEndTimestamp());

		// Add new weather data
		this.weatherservice.addNewWeather(this.initParameter.getStartTimestamp(), this.initParameter.getEndTimestamp(),
				!param.isAggregatedWeatherDataEnabled(), this.createStrategyList(param.getWeatherEventHelperList()));

		// Set start date
		this.setStartTimestamp(this.initParameter.getStartTimestamp());
		this.setNextNewDayInMillis(this.initParameter.getStartTimestamp());
	}

	@Override
	protected void onStop() {
		// Nothing to do
	}

	@Override
	protected void onUpdate(SimulationEventList simulationEventList) {
		// Handle events
		for (SimulationEvent event : simulationEventList.getEventList()) {
			// DefaultWeatherController.log.debug("Event: " + event.getEventType());
			if (event instanceof ChangeWeatherEvent) {
				// Change the current weather data
				try {
					ChangeWeatherEvent cevent = (ChangeWeatherEvent) event;
					WeatherStrategy strategy = DefaultWeatherController.this.createStrategyFromEnum(cevent.getEvent(),
							cevent.getTimestamp(), cevent.getValue(), cevent.getDuration());
					DefaultWeatherController.log.debug("Prepare modifier: " + cevent.getEvent());
					if (strategy != null) {
						// Log
						DefaultWeatherController.log.debug("Deploy modifier: " + cevent.getEvent());
						DefaultWeatherController.this.weatherservice.deployStrategy(strategy);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
			}
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
