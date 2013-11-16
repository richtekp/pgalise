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
 
package de.pgalise.simulation.weather.internal.modifier;

import de.pgalise.testutils.TestUtils;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.ejb.embeddable.EJBContainer;

import org.junit.Assert;
import org.junit.Test;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.dataloader.DatabaseWeatherLoader;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.events.HotDayEvent;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.model.DefaultServiceDataCurrent;
import de.pgalise.simulation.weather.model.DefaultServiceDataForecast;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.modifier.AbstractWeatherMapModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.testutils.WeatherTestUtils;
import java.sql.Date;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.BeforeClass;

/**
 * JUnit test for HotDayEvent
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
@LocalClient
@ManagedBean
public class HotDayEventTest {
	@PersistenceUnit(unitName = "pgalise")
	private EntityManagerFactory entityManagerFactory;
	private static EJBContainer CONTAINER;

	/**
	 * End timestamp
	 */
	private static long endTimestamp;

	/**
	 * Start timestamp
	 */
	private static long startTimestamp;

	/**
	 * Test timestamp
	 */
	private static long testTimestamp;

	/**
	 * Test value
	 */
	private static float testValue = 30.0f;

	/**
	 * Test duration
	 */
	private static float testDuration = 4.0f;

	/**
	 * Service Class
	 */
	private DefaultWeatherService service;

	/**
	 * Weather Loader
	 */
	private WeatherLoader<DefaultWeatherCondition> loader;
	
	private	City city;
	
	@Resource
	private UserTransaction userTransaction;

	@SuppressWarnings("LeakingThisInConstructor")
	public HotDayEventTest() throws NamingException {
		CONTAINER.getContext().bind("inject",
			this);
		
		city = TestUtils.createDefaultTestCityInstance();
		
		// Load EJB for Weather loader
		loader = new DatabaseWeatherLoader(entityManagerFactory.createEntityManager());

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2010, 5, 12, 0, 0, 0);
		HotDayEventTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2010, 5, 13, 0, 0, 0);
		HotDayEventTest.endTimestamp = cal.getTimeInMillis();

		// Test time
		cal.set(2010, 5, 12, 18, 0, 0);
		HotDayEventTest.testTimestamp = cal.getTimeInMillis();

		// Create service
		service = new DefaultWeatherService(city, loader);
	}
	
	@BeforeClass
	public static void setUpClass() {
		CONTAINER = TestUtils.getContainer();
	}

	@Test
	public void testDeployChanges() throws Exception {
		service = new DefaultWeatherService(city, loader);
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(startTimestamp);
		cal.add(Calendar.DATE, -1);
		
		Map<Date, StationDataNormal> entities = WeatherTestUtils.setUpWeatherStationData(startTimestamp,
			endTimestamp,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataCurrent> entities0 = WeatherTestUtils.setUpWeatherServiceDataCurrent(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataForecast> entities1 = WeatherTestUtils.setUpWeatherServiceDataForecast(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		service.addNewWeather(startTimestamp, endTimestamp, true,
				null);
		
		// Get extrema of reference values
		float refvalue = service.getValue(WeatherParameterEnum.TEMPERATURE,
				HotDayEventTest.testTimestamp).floatValue();

		// Deploy strategy
		HotDayEvent event = new HotDayEvent(new DefaultRandomSeedService().getSeed(ColdDayEventTest.class.toString()),
				HotDayEventTest.testTimestamp, null, HotDayEventTest.testValue, HotDayEventTest.testDuration,
				loader);
		service.deployStrategy(event);

		// Get extrema of decorator values
		float decvalue = service.getValue(WeatherParameterEnum.TEMPERATURE,
				HotDayEventTest.testTimestamp).floatValue();

		/*
		 * Testcase 1
		 */

		// Test 1: Max are not equals - Temperature
		Assert.assertTrue(refvalue < decvalue);

		// Test 2: Max are as high event - Temperature
		Assert.assertEquals(AbstractWeatherMapModifier.round(event.getMaxValue(), 3), AbstractWeatherMapModifier.round(decvalue, 3), 1);
		
		WeatherTestUtils.tearDownWeatherData(entities,StationDataNormal.class,
			userTransaction,
			entityManagerFactory);
		WeatherTestUtils.tearDownWeatherData(entities0,
			DefaultServiceDataCurrent.class,
			userTransaction,
			entityManagerFactory);
		WeatherTestUtils.tearDownWeatherData(entities1,
			DefaultServiceDataForecast.class,
			userTransaction,
			entityManagerFactory);
	}

}
