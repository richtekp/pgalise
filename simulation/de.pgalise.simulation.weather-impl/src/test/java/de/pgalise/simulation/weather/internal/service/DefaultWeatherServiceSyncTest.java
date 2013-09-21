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
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.it.TestUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.dataloader.DatabaseWeatherLoader;
import de.pgalise.simulation.weather.model.DefaultServiceDataCurrent;
import de.pgalise.simulation.weather.model.DefaultServiceDataForecast;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherService;
import java.sql.Date;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.BeforeClass;

/**
 * Tests the synchronization of the weather service
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 29, 2012)
 */
@LocalClient
@ManagedBean
public class DefaultWeatherServiceSyncTest {
	private static EJBContainer CONTAINER;
	@PersistenceUnit(unitName = "weather_test")
	private EntityManagerFactory entityManagerFactory;
	
	/**
	 * End timestamp
	 */
	private long endTimestamp;

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultWeatherServiceSyncTest.class);

	/**
	 * Start timestamp
	 */
	private long startTimestamp;

	/**
	 * Test class
	 */
	private WeatherService testclass;

	/**
	 * Number of test threads
	 */
	private final int testNumberOfThreads = 100;

	/**
	 * Weather loader
	 */
	private WeatherLoader<DefaultWeatherCondition> loader;
		
	private City city;
	
	@Resource
	private UserTransaction userTransaction;

	@SuppressWarnings("LeakingThisInConstructor")
	public DefaultWeatherServiceSyncTest() throws NamingException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		CONTAINER.getContext().bind("inject",
			this);
	
		city = TestUtils.createDefaultTestCityInstance();
		
		// Load EJB for Weather loader
		loader = new DatabaseWeatherLoader(entityManagerFactory.createEntityManager());

		testclass = new DefaultWeatherService(city, loader);

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2011, 1, 1, 0, 0, 0);
		startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2011, 1, 2, 0, 0, 0);
		endTimestamp = cal.getTimeInMillis();
	}
	
	@BeforeClass
	public static void setUpClass() {
		CONTAINER = TestUtils.getContainer();
	}

	@Test
	public void testGetValue() throws Exception {
		// Test time
		final long testTime = startTimestamp + (1000 * 60 * 60 * 5);
		// All threads
		final List<Thread> threads = new ArrayList<>();
		
		Map<Date, StationDataNormal> entities = TestUtils.setUpWeatherStationData(startTimestamp,
			endTimestamp,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataCurrent> entities0 = TestUtils.setUpWeatherServiceDataCurrent(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataForecast> entities1 = TestUtils.setUpWeatherServiceDataForecast(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		testclass.addNewWeather(startTimestamp, endTimestamp, true,
				null);

		// Creates 50 Threads
		for (int i = 0; i < testNumberOfThreads; i++) {
			final int y = i;
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Number value;

					// Sleep with random value
					try {
						Thread.sleep((long) (Math.random() * 1000L));
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}

					if ((y % 20) == 0) {
						// Every 20 thread add new weather
						testclass.addNewWeather(startTimestamp,
								endTimestamp, true, null);
						log.debug("New weather added!");

					} else {
						// Get test value
						value = testclass.getValue(WeatherParameterEnum.TEMPERATURE,
								testTime);
						Assert.assertTrue(value != null);
						log.debug("Thread (" + y + ") value: " + value.floatValue());
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
		
		TestUtils.tearDownWeatherData(entities,StationDataNormal.class,
			userTransaction,
			entityManagerFactory);
		TestUtils.tearDownWeatherData(entities0,
			DefaultServiceDataCurrent.class,
			userTransaction,
			entityManagerFactory);
		TestUtils.tearDownWeatherData(entities1,
			DefaultServiceDataForecast.class,
			userTransaction,
			entityManagerFactory);
	}

}
