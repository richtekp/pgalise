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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.GPSMapper;
import de.pgalise.simulation.service.internal.DefaultGPSMapper;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherService;

/**
 * Tests the synchronization of the weather service
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 29, 2012)
 */
public class WeatherServiceSyncTest {
	/**
	 * End timestamp
	 */
	public static long endTime = 0;

	/**
	 * Logger
	 */
	public static final Logger log = LoggerFactory.getLogger(WeatherServiceSyncTest.class);

	/**
	 * Start timestamp
	 */
	public static long startTime = 0;

	/**
	 * Test class
	 */
	public static WeatherService testclass;

	/**
	 * Number of test threads
	 */
	public static final int testNumberOfThreads = 100;

	/**
	 * Weather loader
	 */
	private static WeatherLoader loader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Load EJB properties
		Properties prop = new Properties();
		prop.load(Controller.class.getResourceAsStream("/jndi.properties"));
		EJBContainer container = EJBContainer.createEJBContainer(prop);
		Context ctx = container.getContext();

		// GPS Mapper
		GPSMapper mapper = new DefaultGPSMapper();

		// City
		City city = new City();
		city.setPopulation(200000);

		// Load EJB for Weather loader
		WeatherServiceSyncTest.loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		WeatherServiceSyncTest.testclass = new DefaultWeatherService(city, mapper, WeatherServiceSyncTest.loader);

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2011, 1, 1, 0, 0, 0);
		WeatherServiceSyncTest.startTime = cal.getTimeInMillis();

		// End
		cal.set(2011, 1, 2, 0, 0, 0);
		WeatherServiceSyncTest.endTime = cal.getTimeInMillis();

		// Add weather
		WeatherServiceSyncTest.testclass.addNewWeather(WeatherServiceSyncTest.startTime,
				WeatherServiceSyncTest.endTime, true, null);
	}

	/**
	 * Exception
	 */
	private volatile Throwable throwable;

	@After
	public void tearDown() throws Throwable {
		if (this.throwable != null) {
			throw this.throwable;
		}
	}

	@Test
	public void testGetValue() throws InterruptedException {
		// Test time
		final long testTime = WeatherServiceSyncTest.startTime + (1000 * 60 * 60 * 5);
		// All threads
		final List<Thread> threads = new ArrayList<>();

		// Creates 50 Threads
		for (int i = 0; i < WeatherServiceSyncTest.testNumberOfThreads; i++) {
			final int y = i;
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Number value = null;

					// Sleep with random value
					try {
						Thread.sleep((long) (Math.random() * 1000L));
					} catch (InterruptedException e) {
						WeatherServiceSyncTest.this.throwable = e;
						e.printStackTrace();
					}

					if ((y % 20) == 0) {
						// Every 20 thread add new weather
						WeatherServiceSyncTest.testclass.addNewWeather(WeatherServiceSyncTest.startTime,
								WeatherServiceSyncTest.endTime, true, null);
						WeatherServiceSyncTest.log.debug("New weather added!");

					} else {
						// Get test value
						try {
							value = WeatherServiceSyncTest.testclass.getValue(WeatherParameterEnum.TEMPERATURE,
									testTime);
							WeatherServiceSyncTest.log.debug("Thread (" + y + ") value: " + value.floatValue());
							Assert.assertTrue(value != null);
						} catch (Exception e) {
							WeatherServiceSyncTest.this.throwable = e;
							e.printStackTrace();
							Assert.assertTrue(false);
						}
					}
				}
			});

			// Save thread
			threads.add(thread);

			// Start thread
			thread.start();
		}

		// Wait for threads
		for (Thread thread : threads) {
			thread.join();
		}
	}

}
