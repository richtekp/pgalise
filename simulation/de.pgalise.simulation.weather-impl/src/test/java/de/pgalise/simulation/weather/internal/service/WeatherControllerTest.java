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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.controller.Controller.StatusEnum;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.shared.controller.ServerConfiguration.Entity;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventEnum;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import javax.vecmath.Vector2d;

/**
 * JUnit Testcases for WeatherController
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 27, 2012)
 */
public class WeatherControllerTest {

	/**
	 * Test City
	 */
	private static City city;

	/**
	 * Timestamp (end)
	 */
	private static long endTimestamp;

	/**
	 * Logger
	 */
	private static Logger log = LoggerFactory.getLogger(WeatherControllerTest.class);

	/**
	 * Timestamp (start)
	 */
	private static long startTimestamp;

	/**
	 * Context
	 */
	private static Context ctx;

	/**
	 * Test timestamp of the events
	 */
	private static long eventTimestamp;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("simulation.configuration.filepath","src/test/resources/simulation.conf");
		// Load EJB properties
		Properties prop = new Properties();
		prop.load(Controller.class.getResourceAsStream("/jndi.properties"));
		EJBContainer container = EJBContainer.createEJBContainer(prop);
		WeatherControllerTest.ctx = container.getContext();

		/* Create simulation controller and init/start the simulation: */
		ServiceDictionary serviceDictionary = (ServiceDictionary) WeatherControllerTest.ctx
				.lookup("java:global/de.pgalise.simulation.services-impl/de.pgalise.simulation.service.ServiceDictionary");
		serviceDictionary.init(WeatherControllerTest.produceServerConfiguration());

		// Create city
		City city = new City("Berlin",
			3375222,
			80,
			true,
			true,
			new Coordinate(52.516667, 13.4));

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2010, 1, 1, 0, 0, 0);
		WeatherControllerTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2010, 1, 2, 0, 0, 0);
		WeatherControllerTest.endTimestamp = cal.getTimeInMillis();

		// Event time
		cal.set(2010, 1, 1, 18, 0, 0);
		WeatherControllerTest.eventTimestamp = cal.getTimeInMillis();
	}

	/**
	 * Creates the server configuration. Change this for more traffic servers or for distributed servers.
	 * 
	 * @return
	 */
	private static ServerConfiguration produceServerConfiguration() {
		ServerConfiguration conf = new ServerConfiguration();
		List<Entity> entities = new ArrayList<>();
		entities.add(new Entity(ServiceDictionary.RANDOM_SEED_SERVICE));
		entities.add(new Entity(ServiceDictionary.WEATHER_CONTROLLER));
		conf.getConfiguration().put("127.0.0.1:8081", entities);

		return conf;
	}

	private volatile Throwable throwable;

	@Test
	public void controllerTest() throws InterruptedException, NoWeatherDataFoundException, IllegalArgumentException,
			ExecutionException, IllegalStateException, InitializationException, NamingException {
		WeatherController ctrl;
		Number testNumber = null;

		// Local test variables
		long valueTime = WeatherControllerTest.startTimestamp + 360000;
		WeatherParameterEnum testParameter = WeatherParameterEnum.WIND_VELOCITY;
		Coordinate testPosition = new Coordinate(2, 3);
		List<SimulationEvent> testEventList = new ArrayList<>();
		testEventList.add(new ChangeWeatherEvent(UUID.randomUUID(), WeatherEventEnum.HOTDAY, 30.0f,
				WeatherControllerTest.eventTimestamp, 6.0f));
		SimulationEventList testEvent = new SimulationEventList(testEventList, valueTime, UUID.randomUUID());

		InitParameter initParameter = new InitParameter();
		initParameter.setStartTimestamp(WeatherControllerTest.startTimestamp);
		initParameter.setEndTimestamp(WeatherControllerTest.endTimestamp);
		
		StartParameter parameter = new StartParameter();
		parameter.setCity(WeatherControllerTest.city);
		parameter.setWeatherEventHelperList(null);
		parameter.setAggregatedWeatherDataEnabled(true);

		// Create controller
		ctrl = (WeatherController) WeatherControllerTest.ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.service.WeatherController");

		/*
		 * Log
		 */
		WeatherControllerTest.log.debug("Testmethod: init()");

		ctrl.init(initParameter);

		Assert.assertEquals(StatusEnum.INITIALIZED, ctrl.getStatus());

		/*
		 * Log
		 */
		WeatherControllerTest.log.debug("Testmethod: start()");

		// Test (normal) -> call onSuccess
		ctrl.start(parameter);

		Assert.assertEquals(StatusEnum.STARTED, ctrl.getStatus());

		// Test second start: can not started twice -> call onFailure
		try {
			ctrl.start(parameter);
			Assert.assertFalse(true);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
		Assert.assertEquals(StatusEnum.STARTED, ctrl.getStatus());

		/*
		 * Log
		 */
		WeatherControllerTest.log.debug("Testmethod: getValue()");

		// Test (normal) -> call onSuccess
		try {
			testNumber = ctrl.getValue(testParameter, valueTime, testPosition);
			Assert.assertEquals(3.457, testNumber.doubleValue(), 0.5);
		} catch (Exception e1) {
			Assert.assertTrue(false);
		}

		// Test false parameters: null as key -> call onFailure
		try {
			testNumber = ctrl.getValue(null, valueTime, testPosition);
			Assert.assertNull(testNumber);
		} catch (Exception e1) {
			Assert.assertTrue(true);
		}

		// Test false parameters: wrong timestamp -> call onFailure
		try {
			testNumber = ctrl.getValue(testParameter, 0, testPosition);
			Assert.assertNull(testNumber);
		} catch (Exception e1) {
			Assert.assertTrue(true);
		}

		// Test false parameters: wrong position -> call onFailure
		try {
			testNumber = ctrl.getValue(testParameter, 0, new Coordinate(-1, -2));
			Assert.assertNull(testNumber);
		} catch (Exception e1) {
			Assert.assertTrue(true);
		}

		/*
		 * Log
		 */
		WeatherControllerTest.log.debug("Testmethod: update()");

		// Test (normal) -> call onSuccess
		try {
			ctrl.update(testEvent);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
		Assert.assertEquals(StatusEnum.STARTED, ctrl.getStatus());

		// Test false parameter: no event -> call onFailure
		try {
			ctrl.update(null);
			Assert.assertFalse(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
		Assert.assertEquals(StatusEnum.STARTED, ctrl.getStatus());

		/*
		 * Log
		 */
		WeatherControllerTest.log.debug("Testmethod: stop()");

		// Stops the controller. Validates the state enum to check, if the
		// controller has the stopped state.
		ctrl.stop();
		Assert.assertEquals(StatusEnum.STOPPED, ctrl.getStatus());

		/*
		 * Log
		 */
		WeatherControllerTest.log.debug("Testmethod: reset()");

		// Init the controller. Validates the state enum to check, if the
		// controller has the init state.
		ctrl.reset();
		Assert.assertEquals(StatusEnum.INIT, ctrl.getStatus());
	}

	@After
	public void tearDown() throws Throwable {
		// Is there an error in an other thread?
		if (this.throwable != null) {
			throw this.throwable;
		}
	}

}
