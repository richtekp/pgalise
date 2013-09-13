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
import java.sql.Time;
import java.text.ParseException;
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

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.simulation.weather.util.WeatherStrategyHelper;
import javax.vecmath.Vector2d;

/**
 * JUnit Tests for WeatherService
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 27, 2012)
 */
public class WeatherServiceTest {

	/**
	 * End timestamp
	 */
	public static long endTimestamp;

	/**
	 * Start timestamp
	 */
	public static long startTimestamp;

	/**
	 * Test class
	 */
	private static DefaultWeatherService service;

	/**
	 * Weather loader
	 */
	private static WeatherLoader loader;

	/**
	 * Wird am Anfang einmal ausgeführt
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Load EJB properties
		Properties prop = new Properties();
		prop.load(Controller.class.getResourceAsStream("/jndi.properties"));
		EJBContainer container = EJBContainer.createEJBContainer(prop);
		Context ctx = container.getContext();

		// City
		Coordinate referencePoint = new Coordinate(52.516667, 13.4);
		Polygon referenceArea = GeotoolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
			new Coordinate[] {
				new Coordinate(referencePoint.x-1, referencePoint.y-1), 
				new Coordinate(referencePoint.x-1, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y-1),
				new Coordinate(referencePoint.x-1, referencePoint.y-1)
			}
		);
		City city = new City("Berlin",
			3375222,
			80,
			true,
			true,
			referenceArea);

		// Load EJB for Weather loader
		WeatherServiceTest.loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		WeatherServiceTest.service = new DefaultWeatherService(city, WeatherServiceTest.loader);

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 11, 1, 20, 0, 0);
		WeatherServiceTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2012, 11, 2, 20, 14, 0);
		WeatherServiceTest.endTimestamp = cal.getTimeInMillis();
	}

	@After
	public void tearDown() throws Exception {
		// Delete old data
		if ((WeatherServiceTest.service.getReferenceValues() == null)
				|| !WeatherServiceTest.service.getReferenceValues().isEmpty()) {
			WeatherServiceTest.service._clearValues();
		}
	}

	@Test
	public void testAddNextWeather() throws NoWeatherDataFoundException {
		/*
		 * Test cases
		 */

		// Test no weather data loaded
		try {
			WeatherServiceTest.service.addNextWeather();
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		/*
		 * Test preparations
		 */

		// Get weather
		WeatherServiceTest.service.addNewWeather(WeatherServiceTest.startTimestamp, WeatherServiceTest.endTimestamp,
				true, null);

		/*
		 * Test cases
		 */

		// Test next day (normal)
		try {
			WeatherServiceTest.service.addNextWeather();
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testAddWeatherDate() throws ParseException {
		/*
		 * Test cases
		 */

		List<WeatherStrategyHelper> strategyList = new ArrayList<>();
		strategyList.add(new WeatherStrategyHelper(new ReferenceCityModifier(WeatherServiceTest.startTimestamp,
				WeatherServiceTest.loader), WeatherServiceTest.startTimestamp));

		// Test (normal)
		try {
			WeatherServiceTest.service.addNewWeather(WeatherServiceTest.startTimestamp,
					WeatherServiceTest.endTimestamp, true, strategyList);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}

		// Test false Date
		try {
			WeatherServiceTest.service.addNewWeather(0, 0, true, null);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testCheckDate() throws ParseException {

		long testFalseDate = DateConverter.convertDate("12.03.2002", "dd.mm.yyyy").getTime();

		/*
		 * Test cases
		 */

		// Test check Date (normal)
		try {
			WeatherServiceTest.service.checkDate(WeatherServiceTest.startTimestamp);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}

		// Test false Date
		try {
			WeatherServiceTest.service.checkDate(0);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		// Test false Date (future)
		Assert.assertTrue(!(WeatherServiceTest.service.checkDate(System.currentTimeMillis())));

		// Test false Date (no data available)
		Assert.assertTrue(!(WeatherServiceTest.service.checkDate(testFalseDate)));

	}

	@Test
	public void testGetValueWeatherParameterEnumLong() throws NoWeatherDataFoundException, ParseException {
		/*
		 * Test preparations
		 */

		// Get weather
		WeatherServiceTest.service.addNewWeather(WeatherServiceTest.startTimestamp, WeatherServiceTest.endTimestamp,
				true, null);

		/*
		 * Test variables
		 */

		// Test Parameter
		WeatherParameterEnum testParameter = WeatherParameterEnum.AIR_PRESSURE;
		Time time = DateConverter.convertTime("18:00:00", "hh:mm:ss");
		long timestamp = WeatherServiceTest.startTimestamp + time.getTime();

		/*
		 * Test cases
		 */

		Number value;

		// Test (normal)
		value = WeatherServiceTest.service.getValue(testParameter, timestamp);
		Assert.assertEquals(1008.0, value.doubleValue(), 10.0); // Aggregate 1008

		// Test false timestamp
		try {
			value = WeatherServiceTest.service.getValue(testParameter, 0);
		} catch (RuntimeException e) {
			Assert.assertTrue(true);
		}

		// Test false key
		try {
			value = WeatherServiceTest.service.getValue(null, 0);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testGetValueWeatherParameterEnumLongVector2d() throws NoWeatherDataFoundException, ParseException {
		/*
		 * Test variables
		 */

		// Test Parameter
		WeatherParameterEnum testParameter = WeatherParameterEnum.LIGHT_INTENSITY;

		// Test times
		Time time = DateConverter.convertTime("18:00:00", "hh:mm:ss");
		long timestamp = WeatherServiceTest.startTimestamp + time.getTime();
		long timestamp2 = WeatherServiceTest.startTimestamp + DateConverter.NEXT_DAY_IN_MILLIS + time.getTime();

		// Test Position
		Coordinate position = new Coordinate(2, 3);

		/*
		 * Test preparations
		 */

		// Get weather
		WeatherServiceTest.service.addNewWeather(WeatherServiceTest.startTimestamp, WeatherServiceTest.endTimestamp,
				true, null);

		/*
		 * Test cases
		 */

		Number value;

		// Test (normal)
		value = WeatherServiceTest.service.getValue(testParameter, timestamp, position);
		Assert.assertEquals(11000.000, value.doubleValue(), 5000);

		// Test false key
		try {
			value = WeatherServiceTest.service.getValue(null, 0, position);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		// Test false timestamp
		try {
			value = WeatherServiceTest.service.getValue(testParameter, 0, position);
		} catch (RuntimeException e) {
			Assert.assertTrue(true);
		}

		// Test false position
		try {
			value = WeatherServiceTest.service.getValue(testParameter, timestamp, null);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		// Test false position
		try {
			value = WeatherServiceTest.service.getValue(testParameter, timestamp, new Coordinate(-1, -1));
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		// Test (normal) other date
		value = WeatherServiceTest.service.getValue(testParameter, timestamp2, position);
		Assert.assertEquals(11000.000, value.doubleValue(), 6000);

		// Test false timestamp
		try {
			value = WeatherServiceTest.service.getValue(testParameter, WeatherServiceTest.endTimestamp
					+ DateConverter.NEXT_DAY_IN_MILLIS + time.getTime(), position);
		} catch (RuntimeException e) {
			Assert.assertTrue(true);
		}
	}

}
