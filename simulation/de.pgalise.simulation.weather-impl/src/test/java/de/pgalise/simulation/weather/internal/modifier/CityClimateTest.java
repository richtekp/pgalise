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
import de.pgalise.simulation.weather.internal.modifier.simulationevents.CityClimateModifier;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.internal.util.comparator.TemperatureComparator;

/**
 * JUnit test for CityClimatemodifier
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
public class CityClimateTest {

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
		City city = new City();
		city.setPopulation(200000);

		// Load EJB for Weather loader
		CityClimateTest.loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2011, 5, 10, 0, 0, 0);
		CityClimateTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2011, 5, 14, 0, 0, 0);
		CityClimateTest.endTimestamp = cal.getTimeInMillis();

		// Create service
		CityClimateTest.service = new DefaultWeatherService(city, CityClimateTest.loader);
	}

	@Before
	public void setUp() throws Exception {
		// Get reference weather informations
		CityClimateTest.service.addNewWeather(CityClimateTest.startTimestamp, CityClimateTest.endTimestamp, true, null);
	}

	@Test
	public void testDeployChanges() throws Exception {
		// Get reference values
		WeatherMap referenceValues = CityClimateTest.service.getReferenceValues();

		// Get max of reference values
		Weather refmax = Collections.max(referenceValues.values(), new TemperatureComparator());
		float refvalue1 = refmax.getTemperature();
		float refvalue2 = refmax.getPerceivedTemperature();
		float refvalue3 = refmax.getRadiation();
		float refvalue4 = refmax.getRelativHumidity();
		float refvalue5 = refmax.getWindVelocity();
		float refvalue6 = refmax.getPrecipitationAmount();
		long reftime = refmax.getTimestamp();

		// Deploy strategy
		CityClimateModifier modifier = new CityClimateModifier(
				new DefaultRandomSeedService().getSeed(CityClimateTest.class.toString()), CityClimateTest.loader);
		CityClimateTest.service.deployStrategy(modifier);

		// Get modifier values
		WeatherMap modifierValues = CityClimateTest.service.getReferenceValues();

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
		 * Testcase 3 : Radiation
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue3 != decmax.getRadiation());

		/*
		 * Testcase 4 : RelativHumidity
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue4 != decmax.getRelativHumidity());

		/*
		 * Testcase 5 : WindVelocity
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue5 != decmax.getWindVelocity());

		/*
		 * Testcase 6 : PrecipitationAmount
		 */

		// Test 1: Max are not equals
		if (modifier.isRainDay()) {
			Assert.assertTrue(refvalue6 != decmax.getPrecipitationAmount());
		} else {
			Assert.assertEquals(refvalue6, decmax.getPrecipitationAmount());
		}

	}

}
