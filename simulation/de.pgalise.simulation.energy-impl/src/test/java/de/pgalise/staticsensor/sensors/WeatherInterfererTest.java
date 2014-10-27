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
package de.pgalise.staticsensor.sensors;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.AnemometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.BarometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.CompositeWeatherInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.HygrometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.LuxmeterWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.PyranometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.RainsensorWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.ThermometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.WeatherBaseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.WindFlagWhiteNoiseInterferer;
import de.pgalise.testutils.TestUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the all {@link WeatherInterferer}
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Nov 12, 2012)
 */
@LocalClient
@ManagedBean
public class WeatherInterfererTest {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(
		WeatherInterfererTest.class);

	/**
	 * Weather controller
	 */
	public static WeatherController weather;

	/**
	 * Test timestamp
	 */
	public static long testTimestamp;

	/**
	 * Test position
	 */
	public static BaseCoordinate testPosition;

	/**
	 * Test value
	 */
	public static double testValue;

	/**
	 * Random seed service
	 */
	@EJB
	private RandomSeedService service;
  @EJB
  private IdGenerator idGenerator;

	@Before
	public void setUp() throws Exception {
    TestUtils.getContainer().getContext().bind("inject",
      this);
    
		// Test timestamp
		Calendar cal = new GregorianCalendar();
		cal.set(2011,
			2,
			1,
			14,
			0,
			0);
		testTimestamp = cal.getTimeInMillis();

		// Test position
		testPosition = new BaseCoordinate( 1.0,
			10.0);

		/*
		 * Mock of the Weather Controller
		 */
		weather = EasyMock.createNiceMock(WeatherController.class);

		// Mock the requests to the Weather Controller
		EasyMock.expect(weather.getValue(WeatherParameterEnum.WIND_VELOCITY,
			testTimestamp,
			testPosition)).andReturn(
				3.594);
		EasyMock.expect(weather.getValue(WeatherParameterEnum.AIR_PRESSURE,
			testTimestamp,
			testPosition)).andReturn(
				1032.0);
		EasyMock.expect(weather.getValue(WeatherParameterEnum.RELATIV_HUMIDITY,
			testTimestamp,
			testPosition))
			.andReturn(92.364);
		EasyMock.expect(weather.getValue(WeatherParameterEnum.LIGHT_INTENSITY,
			testTimestamp,
			testPosition)).andReturn(
				11952.411);
		EasyMock.expect(weather.getValue(WeatherParameterEnum.RADIATION,
			testTimestamp,
			testPosition)).andReturn(113.0);
		EasyMock.expect(weather.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT,
			testTimestamp,
			testPosition))
			.andReturn(1.0);
		EasyMock.expect(weather.getValue(WeatherParameterEnum.WIND_DIRECTION,
			testTimestamp,
			testPosition)).andReturn(
				302.0);
		EasyMock.expect(weather.getValue(WeatherParameterEnum.TEMPERATURE,
			testTimestamp,
			testPosition)).andReturn(
				3.679);
		EasyMock.expect(weather.getValue(WeatherParameterEnum.TEMPERATURE,
			testTimestamp,
			testPosition)).andReturn(
				3.679);

		EasyMock.replay(weather);
	}

	@Test
	public void testAnemometerWhiteNoiseInterferer() {
		WeatherBaseInterferer testclass = new AnemometerWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		testValue = weather.getValue(WeatherParameterEnum.WIND_VELOCITY,
			testTimestamp,
			testPosition).doubleValue();

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue,
			testPosition,
			testTimestamp);

		log.debug(
			"WIND_VELOCITY - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testBarometerWhiteNoiseInterferer() {
		WeatherBaseInterferer testclass = new BarometerWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		testValue = weather.getValue(WeatherParameterEnum.AIR_PRESSURE,
			testTimestamp,
			testPosition).doubleValue();

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue,
			testPosition,
			testTimestamp);

		log.
			debug("AIR_PRESSURE - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testHygrometerWhiteNoiseInterferer() {
		WeatherBaseInterferer testclass = new HygrometerWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		testValue = weather.getValue(WeatherParameterEnum.RELATIV_HUMIDITY,
			testTimestamp,
			testPosition).doubleValue();

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue,
			testPosition,
			testTimestamp);

		log.debug(
			"RELATIV_HUMIDITY - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testLuxmeterWhiteNoiseInterferer() {
		WeatherBaseInterferer testclass = new LuxmeterWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		testValue = weather.getValue(WeatherParameterEnum.LIGHT_INTENSITY,
			testTimestamp,
			testPosition).doubleValue();

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue,
			testPosition,
			testTimestamp);

		log.debug(
			"LIGHT_INTENSITY - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testPyranometerWhiteNoiseInterferer() {
		WeatherBaseInterferer testclass = new PyranometerWhiteNoiseInterferer(
			service);
		testclass.setChangeProbability(1.0);

		testValue = weather.getValue(WeatherParameterEnum.RADIATION,
			testTimestamp,
			testPosition).doubleValue();

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue,
			testPosition,
			testTimestamp);

		log.debug("RADIATION - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testRainsensorWhiteNoiseInterferer() {
		WeatherBaseInterferer testclass = new RainsensorWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		testValue = weather.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT,
			testTimestamp,
			testPosition)
			.doubleValue();

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue,
			testPosition,
			testTimestamp);

		log.debug(
			"PRECIPITATION_AMOUNT - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testWindFlagWhiteNoiseInterferer() {
		WeatherBaseInterferer testclass = new WindFlagWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		testValue = weather.getValue(WeatherParameterEnum.WIND_DIRECTION,
			testTimestamp,
			testPosition).doubleValue();

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue,
			testPosition,
			testTimestamp);

		log.debug(
			"WIND_DIRECTION - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testThermometerWhiteNoiseInterferer() {
		WeatherBaseInterferer testclass = new ThermometerWhiteNoiseInterferer(
			service);
		testclass.setChangeProbability(1.0);

		testValue = weather.getValue(WeatherParameterEnum.TEMPERATURE,
			testTimestamp,
			testPosition).doubleValue();

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue,
			testPosition,
			testTimestamp);

		log.debug("TEMPERATURE - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testCompositeWeatherInterferer() {
		WeatherBaseInterferer interferer1 = new ThermometerWhiteNoiseInterferer(
			service);
		interferer1.setChangeProbability(1.0);

		List<WeatherInterferer> list = new ArrayList<>();
		list.add(interferer1);
		WeatherInterferer testclass = new CompositeWeatherInterferer(list);

		testValue = weather.getValue(WeatherParameterEnum.TEMPERATURE,
			testTimestamp,
			testPosition).doubleValue();

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue,
			testPosition,
			testTimestamp);

		log.debug("Composite - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

}
