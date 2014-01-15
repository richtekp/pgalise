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

import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventType;
import de.pgalise.simulation.shared.event.weather.WeatherEventTypeEnum;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main interaction point of the component Weather is the interface
 * {@link WeatherController} that represents the weather environment controller
 * of the simulation. Its public methods can be called by other components.
 * Therefore, the controller is implemented as an EJB. For one thing the
 * controller sends all requests to the interface {@link WeatherService} and the
 * responses back to the other components, for another thing it inherits from
 * the generic {@link Controller} interface and implements its state
 * transitions. So this controller acts as intermediary between components of
 * the simulation and the {@link WeatherService}. The interface
 * {@link WeatherControllerLocal} can be used for local communication.<br />
 * <br />
 * If the {@link DefaultWeatherController} tries to start, it checks the
 * availability of weather information regarding the start date of the
 * simulation. An exception will occur if the component cannot connect to the
 * database or found any information. It is mandatory that there is weather data
 * to the first simulation day.
 *
 * @author Andreas Rehfeldt
 * @author Timo
 * @author Mustafa
 * @version 1.0 (Aug 24, 2012)
 */
@Stateful
public class DefaultWeatherController extends AbstractController<WeatherEvent, StartParameter, InitParameter>
	implements WeatherController {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(
		DefaultWeatherController.class);
	private static final String NAME = "WeatherController";

	/**
	 * 1 day in millis.
	 */
	private static final long ONE_DAY_IN_MILLIS = 86400000;

	/**
	 * File path for property file
	 */
	private static final String PROPERTIES_FILE_PATH = "weather_decorators.properties";

	private final static String JNDI_PROPERTIES_FILE_PATH = "/META-INF/jndi.properties";
	private static final long serialVersionUID = 1L;

	/**
	 * Properties for decorators
	 */
	private Properties decoratorProps = null;

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
	@EJB
	private WeatherService weatherservice;

	@EJB
	private WeatherLoader weatherLoader;

	/**
	 * Random Seed Service
	 */
	@EJB
	private RandomSeedService randomSeedService;

	private InitParameter initParameter;

	/**
	 * Constructor
	 */
	public DefaultWeatherController() {
		// Read propsInputStream propInFile
		try (InputStream propInFile = Thread.currentThread().getContextClassLoader()
			.getResourceAsStream(DefaultWeatherController.PROPERTIES_FILE_PATH)) {
			this.decoratorProps = new Properties();
			this.decoratorProps.loadFromXML(propInFile);
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

	public WeatherStrategy createStrategyFromEnum(WeatherEventType enumElement,
		long timestamp,
		long duration) throws IllegalArgumentException {
		throw new UnsupportedOperationException(
			"no use case for weather events without value check class architecture");
	}

	/**
	 * Creates a weather strategy from the given enum element
	 *
	 * @param enumElement WeatherEventTypeEnum
	 * @param timestamp Timestamp
	 * @param value Specified value for event
	 * @param duration Duration of the event
	 * @return weather strategy
	 * @throws IllegalArgumentException enumElement is null or weather strategy is
	 * not implemented
	 */
	public WeatherStrategy createStrategyFromEnum(WeatherEventType enumElement,
		long timestamp,
		Float value,
		long duration) throws IllegalArgumentException {
		if (enumElement == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"enumElement"));
		}

		/*
		 * INFO: You have to add new weather strategies here!
		 */
		if (enumElement.equals(WeatherEventTypeEnum.RAINDAY)) {
			return new RainDayEvent(this.randomSeedService.getSeed(RainDayEvent.class.
				getName()),
				timestamp,
				this.decoratorProps,
				value,
				duration,
				this.weatherLoader);
		} else if (enumElement.equals(WeatherEventTypeEnum.COLDDAY)) {
			return new ColdDayEvent(this.randomSeedService.getSeed(ColdDayEvent.class.
				getName()),
				timestamp,
				this.decoratorProps,
				value,
				duration,
				this.weatherLoader);
		} else if (enumElement.equals(WeatherEventTypeEnum.HOTDAY)) {
			return new HotDayEvent(this.randomSeedService.getSeed(HotDayEvent.class.
				getName()),
				timestamp,
				this.decoratorProps,
				value,
				duration,
				this.weatherLoader);
		} else if (enumElement.equals(WeatherEventTypeEnum.STORMDAY)) {
			return new StormDayEvent(this.randomSeedService.getSeed(
				StormDayEvent.class.getName()),
				timestamp,
				this.decoratorProps,
				value,
				duration,
				this.weatherLoader);
		} else if (enumElement.equals(WeatherEventTypeEnum.CITYCLIMATE)) {
			// Events with -1 are not considered
			return ((value != null) && (value < 0)) ? null : new CityClimateModifier(
				this.randomSeedService.getSeed(CityClimateModifier.class.getName()),
				this.decoratorProps,
				this.weatherLoader);
		} else if (enumElement.equals(WeatherEventTypeEnum.REFERENCECITY)) {
			// Events with -1 are not considered
			return ((value != null) && (value < 0)) ? null : new ReferenceCityModifier(
				this.randomSeedService.getSeed(ReferenceCityModifier.class.getName()),
				this.decoratorProps,
				this.weatherLoader);
		} else {
			throw new IllegalArgumentException("No weather strategy found!");
		}
	}

	/**
	 * Creates a list of weather strategies from the given enum list
	 *
	 * @param eventList List of WeatherEvent
	 * @return list of weather strategies
	 * @throws IllegalArgumentException
	 */
	public List<WeatherStrategyHelper> createStrategyList(
		List<WeatherEvent> eventList)
		throws IllegalArgumentException {
		if (eventList == null) {
			return null;
		}

		// Create strategies
		List<WeatherStrategyHelper> strategies = new ArrayList<>(eventList.size());
		for (WeatherEvent event : eventList) {
			WeatherStrategy strategy;
			if (event instanceof ChangeWeatherEvent) {
				strategy = this.createStrategyFromEnum(event.getType(),
					event.getTimestamp(),
					((ChangeWeatherEvent) event).getValue(),
					event.getDuration());
			} else {
				strategy = this.createStrategyFromEnum(event.getType(),
					event.getTimestamp(),
					event.getDuration());
			}
			if (strategy != null) {
				strategies.add(new WeatherStrategyHelper(strategy,
					event.getTimestamp()));
			}
		}

		// Return
		return strategies;
	}

	public long getNextNewDayInMillis() {
		return this.nextNewDayInMillis;
	}

	public long getStartTimestamp() {
		return this.startTimestamp;
	}

	@Override
	public Number getValue(final WeatherParameterEnum key,
		final long timestamp,
		final JaxRSCoordinate position) {
		return DefaultWeatherController.this.weatherservice.getValue(key,
			timestamp,
			position);
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
	 * @param city City
	 */
	public void startWeatherService(City city) {
		if (this.weatherservice == null) {
			this.weatherservice = new DefaultWeatherService(city,
				this.weatherLoader);
		} else {
			this.weatherservice.initValues();
			this.weatherservice.setCity(city);
		}
	}

	/**
	 * Returns the first new date after the timestamp. e.g. for 2012-11-09
	 * 14:44:00.0 and interval of 7 seconds it will return 2012-11-10 00:00:02.0
	 *
	 * @param timestamp in millis
	 * @param interval in millis
	 * @return Timestamp of the next date
	 */
	@SuppressWarnings("unused")
	private long getNextNewDateTimestamp(long timestamp,
		long interval) {
		long timestamp0 = timestamp;
		if (timestamp0 == this.startTimestamp) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(timestamp0);

			int currentHour = calendar.get(Calendar.HOUR);
			int currentMin = calendar.get(Calendar.MINUTE);
			int currentSec = calendar.get(Calendar.SECOND);
			int currentMillis = calendar.get(Calendar.MILLISECOND);

			while (true) {
				timestamp0 += interval;
				calendar.setTimeInMillis(timestamp0);

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
			return timestamp0 + DefaultWeatherController.ONE_DAY_IN_MILLIS;
		}
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		// Set random seed service
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
			.debug(
				"Start: " + this.initParameter.getStartTimestamp() + " - End: " + this.initParameter.
				getEndTimestamp());

		// Add new weather data
		this.weatherservice.addNewWeather(this.initParameter.getStartTimestamp().
			getTime(),
			this.initParameter.getEndTimestamp().getTime(),
			!param.isAggregatedWeatherDataEnabled(),
			this.createStrategyList(param.getWeatherEvents()));

		// Set start date
		this.setStartTimestamp(this.initParameter.getStartTimestamp().getTime());
		this.setNextNewDayInMillis(this.initParameter.getStartTimestamp().getTime());
	}

	@Override
	protected void onStop() {
		// Nothing to do
	}

	@Override
	protected void onUpdate(EventList<WeatherEvent> simulationEventList) {
		// Handle events
		for (AbstractEvent event : simulationEventList.getEventList()) {
			// DefaultWeatherController.log.debug("Event: " + event.getEventType());
			if (event instanceof ChangeWeatherEvent) {
				// Change the current weather data
				try {
					ChangeWeatherEvent cevent = (ChangeWeatherEvent) event;
					WeatherStrategy strategy = DefaultWeatherController.this.
						createStrategyFromEnum(cevent.getType(),
							cevent.getTimestamp(),
							cevent.getValue(),
							cevent.getDuration());
					DefaultWeatherController.log.debug("Prepare modifier: " + cevent.
						getType());
					if (strategy != null) {
						// Log
						DefaultWeatherController.log.debug("Deploy modifier: " + cevent.
							getType());
						DefaultWeatherController.this.weatherservice.
							deployStrategy(strategy);
					}
				} catch (IllegalArgumentException e) {
					log.warn("see nested exception",
						e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JaxRSCoordinate getReferencePosition() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
