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
 
package de.pgalise.simulation.traffic.governor;

import com.vividsolutions.jts.geom.Coordinate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.traffic.internal.governor.FuzzyTrafficGovernor;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import javax.vecmath.Vector2d;

/**
 * Tests of the FuzzyTrafficGovernor
 * 
 * @author Marcus
 * @version 1.0 (Oct 24, 2012)
 */
public class FuzzyTrafficGovernorTest {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(FuzzyTrafficGovernorTest.class);

	/**
	 * Test timestamp
	 */
	private static long testTimestamp;

	/**
	 * Weather controller
	 */
	public static WeatherController weather;

	/**
	 * Test class
	 */
	private static FuzzyTrafficGovernor trafficGovernor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Test time
		Calendar cal = new GregorianCalendar();

		/* Start Parameter for Weather Controller */
		cal.set(2011, 11, 15, 0, 0, 0);
		long startTimestamp = cal.getTimeInMillis();
		cal.set(2011, 12, 16, 0, 0, 0);
		long endTimestamp = cal.getTimeInMillis();

		StartParameter parameter = new StartParameter();
		City city = new City("Berlin",
			3375222,
			80,
			true,
			true,
			new Coordinate(52.516667, 13.4));
		parameter.setCity(city);
		parameter.setWeatherEventHelperList(null);
		parameter.setAggregatedWeatherDataEnabled(true);

		/*
		 * Mock of the Weather Controller
		 */
		weather = EasyMock.createNiceMock(WeatherController.class);
		System.out.println("Neue Zeit: " + testTimestamp);
		Coordinate testPosition = new Coordinate(1.0, 10.0);

		final Random random = new Random();

		testTimestamp = startTimestamp + (long) (random.nextDouble() * (endTimestamp - startTimestamp));

		// Mock the requests to the Weather Controller
		EasyMock.expect(weather.getReferencePosition()).andStubReturn(testPosition);

		EasyMock.expect(weather.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT, testTimestamp, testPosition))
				.andStubReturn(random.nextInt(1000));
		EasyMock.expect(weather.getValue(WeatherParameterEnum.TEMPERATURE, testTimestamp, testPosition)).andStubReturn(
				random.nextInt(40) * Math.signum(random.nextInt()));
		EasyMock.expect(weather.getValue(WeatherParameterEnum.WIND_STRENGTH, testTimestamp, testPosition))
				.andStubReturn(random.nextInt(13));
		EasyMock.expect(weather.getValue(WeatherParameterEnum.LIGHT_INTENSITY, testTimestamp, testPosition))
				.andStubReturn(random.nextInt(15000));

		EasyMock.replay(weather);

		// Test class
		trafficGovernor = new FuzzyTrafficGovernor();
		trafficGovernor.setWeatherController(weather);
	}

	@Test
	public void testGetPercentageOfActiveCars() throws IOException {
		// test whether is smaller or equal than 100
		assertTrue(trafficGovernor.getPercentageOfActiveCars(testTimestamp) <= 1);
		// test whether is greater or equal than 100
		assertTrue(trafficGovernor.getPercentageOfActiveCars(testTimestamp) >= 0);
		// test whether same parameters lead to same values

		final double real = trafficGovernor.getPercentageOfActiveCars(testTimestamp);

		assertEquals(real, trafficGovernor.getPercentageOfActiveCars(testTimestamp), 0.0);

		LOG.debug("" + real);
	}

	@Test
	public void testGetPercentageOfActiveBicycles() throws IOException {

		// test whether is smaller or equal than 100
		assertTrue(trafficGovernor.getPercentageOfActiveBicycles(testTimestamp) <= 1);
		// test whether is greater or equal than 100
		assertTrue(trafficGovernor.getPercentageOfActiveBicycles(testTimestamp) >= 0);
		// test whether same parameters lead to same values

		assertEquals(trafficGovernor.getPercentageOfActiveBicycles(testTimestamp),
				trafficGovernor.getPercentageOfActiveBicycles(testTimestamp), 0.0);

		LOG.debug("" + trafficGovernor.getPercentageOfActiveBicycles(testTimestamp));
	}

	@Test
	public void testGetPercentageOfActiveTrucks() throws IOException {

		// test whether is smaller or equal than 100
		assertTrue(trafficGovernor.getPercentageOfActiveTrucks(testTimestamp) <= 1);
		// test whether is greater or equal than 100
		assertTrue(trafficGovernor.getPercentageOfActiveTrucks(testTimestamp) >= 0);
		// test whether same parameters lead to same values

		assertEquals(trafficGovernor.getPercentageOfActiveTrucks(testTimestamp),
				trafficGovernor.getPercentageOfActiveTrucks(testTimestamp), 0.0);

		LOG.debug("" + trafficGovernor.getPercentageOfActiveTrucks(testTimestamp));
	}

	@Test
	public void testGetAverageBusManning() throws IOException {

		// test whether is smaller or equal than 100
		assertTrue(trafficGovernor.getAverageBusManningPercentage(testTimestamp) <= 1);
		// test whether is greater or equal than 100
		assertTrue(trafficGovernor.getAverageBusManningPercentage(testTimestamp) >= 0);
		// test whether same parameters lead to same values

		assertEquals(trafficGovernor.getAverageBusManningPercentage(testTimestamp),
				trafficGovernor.getAverageBusManningPercentage(testTimestamp), 0.0);

		LOG.debug("" + trafficGovernor.getAverageBusManningPercentage(testTimestamp));
	}
}
