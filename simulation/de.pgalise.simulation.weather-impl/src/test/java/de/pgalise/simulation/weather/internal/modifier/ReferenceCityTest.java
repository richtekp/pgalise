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
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.internal.util.comparator.TemperatureComparator;

/**
 * JUnit test for ReferenceCitymodifier
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
public class ReferenceCityTest {

	/**
	 * End timestamp
	 */
	public static long endTimestamp;

	/**
	 * Start timestamp
	 */
	public static long startTimestamp;

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
		City city = new City("Berlin",
			3375222,
			80,
			true,
			true,
			new Coordinate(52.516667, 13.4));

		// Load EJB for Weather loader
		ReferenceCityTest.loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 8, 20, 0, 0, 0);
		ReferenceCityTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2012, 8, 21, 0, 0, 0);
		ReferenceCityTest.endTimestamp = cal.getTimeInMillis();

		// Create service
		ReferenceCityTest.service = new DefaultWeatherService(city, ReferenceCityTest.loader);
	}

	@Before
	public void setUp() throws Exception {
		// Get reference weather informations
		ReferenceCityTest.service.addNewWeather(ReferenceCityTest.startTimestamp, ReferenceCityTest.endTimestamp, true,
				null);
	}

	@Test
	public void testDeployChanges() throws Exception {
		// Get reference values
		WeatherMap referenceValues = ReferenceCityTest.service.getReferenceValues();

		// Get max of reference values
		Weather refmax = Collections.max(referenceValues.values(), new TemperatureComparator());
		float refvalue1 = refmax.getTemperature();
		float refvalue2 = refmax.getPerceivedTemperature();
		float refvalue3 = refmax.getRelativHumidity();
		float refvalue4 = refmax.getWindVelocity();
		long reftime = refmax.getTimestamp();

		// Deploy strategy
		ReferenceCityModifier modifier = new ReferenceCityModifier(
				new DefaultRandomSeedService().getSeed(ReferenceCityTest.class.toString()), ReferenceCityTest.loader);
		ReferenceCityTest.service.deployStrategy(modifier);

		// Get modifier values
		WeatherMap modifierValues = ReferenceCityTest.service.getReferenceValues();

		// Get max of modifier values
		Weather decmax = modifierValues.get(reftime);

		/*
		 * Testcase 1 : Temperature
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue1 != decmax.getTemperature());

		/*
		 * Testcase 2 : Perceived Temperature
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue2 != decmax.getPerceivedTemperature());

		/*
		 * Testcase 3 : RelativHumidity
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue3 != decmax.getRelativHumidity());

		/*
		 * Testcase 4 : WindVelocity
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue4 != decmax.getWindVelocity());
	}
}
