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

import com.vividsolutions.jts.geom.Coordinate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.modifier.events.ColdDayEvent;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.modifier.WeatherMapModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;

/**
 * JUnit test for ColdDayEvent
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
public class ColdDayEventTest {

	/**
	 * End timestamp
	 */
	public static long endTimestamp;

	/**
	 * Start timestamp
	 */
	public static long startTimestamp;

	/**
	 * Test timestamp
	 */
	public static long testTimestamp;

	/**
	 * Test value
	 */
	public static float testValue = -10.0f;

	/**
	 * Test duration
	 */
	public static float testDuration = 4.0f;

	/**
	 * Service Class
	 */
	private static DefaultWeatherService service;

	/**
	 * Weather Loader
	 */
	private static WeatherLoader loader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Load EJB properties
		Properties prop = new Properties();
		prop.load(Controller.class.getResourceAsStream("/jndi.properties"));
		EJBContainer container = EJBContainer.createEJBContainer(prop);
		Context ctx = container.getContext();

		// City
		City city = new City();
		city.setPopulation(200000);

		// Load EJB for Weather loader
		ColdDayEventTest.loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2010, 6, 12, 0, 0, 0);
		ColdDayEventTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2010, 6, 13, 0, 0, 0);
		ColdDayEventTest.endTimestamp = cal.getTimeInMillis();

		// Test time
		cal.set(2010, 6, 12, 18, 0, 0);
		ColdDayEventTest.testTimestamp = cal.getTimeInMillis();

		// Create service
		ColdDayEventTest.service = new DefaultWeatherService(city, ColdDayEventTest.loader);
	}

	@Before
	public void setUp() throws Exception {
		// Get reference weather informations
		ColdDayEventTest.service.addNewWeather(ColdDayEventTest.startTimestamp, ColdDayEventTest.endTimestamp, true,
				null);
	}

	@Test
	public void testDeployChanges() throws Exception {
		// Get extrema of reference values
		float refvalue = ColdDayEventTest.service.getValue(WeatherParameterEnum.TEMPERATURE,
				ColdDayEventTest.testTimestamp).floatValue();

		// Deploy strategy
		ColdDayEvent event = new ColdDayEvent(
				new DefaultRandomSeedService().getSeed(ColdDayEventTest.class.toString()),
				ColdDayEventTest.testTimestamp, null, ColdDayEventTest.testValue, ColdDayEventTest.testDuration,
				ColdDayEventTest.loader);
		ColdDayEventTest.service.deployStrategy(event);

		// Get extrema of decorator values
		float decvalue = ColdDayEventTest.service.getValue(WeatherParameterEnum.TEMPERATURE,
				ColdDayEventTest.testTimestamp).floatValue();

		/*
		 * Testcase 1
		 */

		// Test 1: Min are not equals - Temperature
		Assert.assertTrue(refvalue > decvalue);

		// Test 2: Min are as high event - Temperature
		Assert.assertEquals(WeatherMapModifier.round(event.getMinValue(), 3), WeatherMapModifier.round(decvalue, 3), 1);
	}

}
