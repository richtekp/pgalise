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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
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

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.modifier.DatabaseTestUtils;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherService;
import java.io.IOException;
import java.util.logging.Level;
import javax.naming.NamingException;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;

/**
 * Tests the synchronization of the weather service
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 29, 2012)
 */
public class DefaultWeatherServiceSyncTest {
	
	/**
	 * End timestamp
	 */
	public long endTime = 0;

	/**
	 * Logger
	 */
	public static final Logger log = LoggerFactory.getLogger(DefaultWeatherServiceSyncTest.class);

	/**
	 * Start timestamp
	 */
	public long startTime = 0;

	/**
	 * Test class
	 */
	public WeatherService testclass;

	/**
	 * Number of test threads
	 */
	public final int testNumberOfThreads = 100;

	/**
	 * Weather loader
	 */
	private WeatherLoader loader;
		
	private City city;

	public DefaultWeatherServiceSyncTest() throws NamingException {
		Coordinate referencePoint = new Coordinate(20, 20);
		Polygon referenceArea = DatabaseTestUtils.getGEOMETRY_FACTORY().createPolygon(
			new Coordinate[] {
				new Coordinate(referencePoint.x-1, referencePoint.y-1), 
				new Coordinate(referencePoint.x-1, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y-1),
				new Coordinate(referencePoint.x-1, referencePoint.y-1)
			}
		);
		city = new City("test_city", 200000, 100, true, true, referenceArea);
		
		Context ctx = DatabaseTestUtils.getCONTAINER().getContext();

		// Load EJB for Weather loader
		loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		testclass = new DefaultWeatherService(city, loader);

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2011, 1, 1, 0, 0, 0);
		startTime = cal.getTimeInMillis();

		// End
		cal.set(2011, 1, 2, 0, 0, 0);
		endTime = cal.getTimeInMillis();

		// Add weather
		testclass.addNewWeather(startTime,
				endTime, true, null);
	}

	@Test
	public void testGetValue() throws InterruptedException {
		// Test time
		final long testTime = startTime + (1000 * 60 * 60 * 5);
		// All threads
		final List<Thread> threads = new ArrayList<>();

		// Creates 50 Threads
		for (int i = 0; i < testNumberOfThreads; i++) {
			final int y = i;
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Number value = null;

					// Sleep with random value
					try {
						Thread.sleep((long) (Math.random() * 1000L));
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}

					if ((y % 20) == 0) {
						// Every 20 thread add new weather
						testclass.addNewWeather(startTime,
								endTime, true, null);
						log.debug("New weather added!");

					} else {
						// Get test value
						value = testclass.getValue(WeatherParameterEnum.TEMPERATURE,
								testTime);
						log.debug("Thread (" + y + ") value: " + value.floatValue());
						Assert.assertTrue(value != null);
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
