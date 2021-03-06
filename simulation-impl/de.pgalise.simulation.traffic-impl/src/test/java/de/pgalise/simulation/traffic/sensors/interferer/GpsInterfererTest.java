///* 
// * Copyright 2013 PG Alise (http://www.pg-alise.de/)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License. 
// */
// 
//package de.pgalise.simulation.traffic.sensors.interferer;
//
//import de.pgalise.simulation.shared.city.Coordinate;
//import static org.junit.Assert.assertTrue;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.List;
//
//import org.easymock.EasyMock;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import de.pgalise.simulation.service.RandomSeedService;
//import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.CompositeGpsInterferer;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsAtmosphereInterferer;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsBaseInterferer;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsClockInterferer;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsNoInterferer;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsReceiverInterferer;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsWhiteNoiseInterferer;
//import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
//import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
//import de.pgalise.simulation.weather.service.WeatherController;
//
///**
// * Tests the all GPSInterferer
// * 
// * @author Andreas Rehfeldt
// * @version 1.0 (Nov 12, 2012)
// */
//public class GpsInterfererTest {
//
//	/**
//	 * Logger
//	 */
//	private static final Logger log = LoggerFactory.getLogger(GpsInterfererTest.class);
//
//	/**
//	 * Weather controller
//	 */
//	public static WeatherController weather;
//
//	/**
//	 * Test timestamp
//	 */
//	public static long testTimestamp;
//
//	/**
//	 * Test position
//	 */
//	public static Coordinate testPosition;
//
//	/**
//	 * Vector Unit
//	 */
//	public static double vectorUnit;
//
//	/**
//	 * Random seed service
//	 */
//	public static final RandomSeedService service = new DefaultRandomSeedService();
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		// GPS Mapper
//
//		// Test timestamp
//		Calendar cal = new GregorianCalendar();
//		cal.set(2011, 2, 1, 14, 0, 0);
//		testTimestamp = cal.getTimeInMillis();
//
//		// Test position
//		testPosition = new Coordinate(1.0, 10.0);
//
//		/*
//		 * Mock of the Weather Controller
//		 */
//		weather = EasyMock.createNiceMock(WeatherController.class);
//
//		// Mock the requests to the Weather Controller
//		EasyMock.expect(weather.getValue(WeatherParameterEnum.RADIATION, testTimestamp, testPosition)).andReturn(3.679);
//
//		EasyMock.replay(weather);
//	}
//
//	@Test
//	public void testGpsAtmosphereInterferer() {
//		GpsBaseInterferer testclass = new GpsAtmosphereInterferer(service, weather);
//		testclass.setChangeProbability(1.0);
//
//		/*
//		 * Test value
//		 */
//		Coordinate result = testclass.interfere(testPosition, testPosition, testTimestamp);
//
//		log.debug("Reference: " + testPosition.getX() + " - Changed: " + result.getX());
//		log.debug("Reference: " + testPosition.getY() + " - Changed: " + result.getY());
//
//		assertTrue(testPosition.getX() != result.getX());
//		assertTrue(testPosition.getY() != result.getY());
//	}
//
//	@Test
//	public void testGpsNoInterferer() {
//		GpsInterferer testclass = new GpsNoInterferer();
//
//		/*
//		 * Test value
//		 */
//		Coordinate result = testclass.interfere(testPosition, testPosition, testTimestamp);
//
//		log.debug("Reference: " + testPosition.getX() + " - Changed: " + result.getX());
//		log.debug("Reference: " + testPosition.getY() + " - Changed: " + result.getY());
//
//		assertTrue(testPosition.getX() == result.getX());
//		assertTrue(testPosition.getY() == result.getY());
//	}
//
//	@Test
//	public void testGpsClockInterferer() {
//		GpsBaseInterferer testclass = new GpsClockInterferer(service);
//		testclass.setChangeProbability(1.0);
//
//		/*
//		 * Test value
//		 */
//		Coordinate result = testclass.interfere(testPosition, testPosition, testTimestamp);
//
//		log.debug("Reference: " + testPosition.getX() + " - Changed: " + result.getX());
//		log.debug("Reference: " + testPosition.getY() + " - Changed: " + result.getY());
//
//		assertTrue(testPosition.getX() != result.getX());
//		assertTrue(testPosition.getY() != result.getY());
//	}
//
//	@Test
//	public void testGpsReceiverInterferer() {
//		GpsBaseInterferer testclass = new GpsReceiverInterferer(service);
//		testclass.setChangeProbability(1.0);
//
//		/*
//		 * Test value
//		 */
//		Coordinate result = testclass.interfere(testPosition, testPosition, testTimestamp);
//
//		log.debug("Reference: " + testPosition.getX() + " - Changed: " + result.getX());
//		log.debug("Reference: " + testPosition.getY() + " - Changed: " + result.getY());
//
//		assertTrue(testPosition.getX() != result.getX());
//		assertTrue(testPosition.getY() != result.getY());
//	}
//
//	@Test
//	public void testGpsWhiteNoiseInterferer() {
//		GpsBaseInterferer testclass = new GpsWhiteNoiseInterferer(service);
//		testclass.setChangeProbability(1.0);
//
//		/*
//		 * Test value
//		 */
//		Coordinate result = null;
//
//		for (int i = 0; i < 20; i++) {
//			result = testclass.interfere(testPosition, testPosition, testTimestamp);
//
//			log.debug("Reference: " + testPosition.getX() + " - Changed: " + result.getX() + " - Difference:"
//					+ (testPosition.getX() - result.getX()) + " - Changed Meters:"
//					+ (testPosition.getX() - result.getX()) * vectorUnit);
//			log.debug("Reference: " + testPosition.getY() + " - Changed: " + result.getY() + " - Difference:"
//					+ (testPosition.getY() - result.getY()) + " - Changed Meters:"
//					+ (testPosition.getY() - result.getY()) * vectorUnit);
//		}
//
//		assertTrue(testPosition.getX() != result.getX());
//		assertTrue(testPosition.getY() != result.getY());
//	}
//
//	@Test
//	public void testCompositeGpsInterferer() {
//		GpsBaseInterferer interferer1 = new GpsReceiverInterferer(service);
//		interferer1.setChangeProbability(1.0);
//		GpsBaseInterferer interferer2 = new GpsClockInterferer(service);
//		interferer2.setChangeProbability(1.0);
//
//		List<GpsInterferer> list = new ArrayList<>();
//		list.add(interferer1);
//		list.add(interferer2);
//		GpsInterferer testclass = new CompositeGpsInterferer(list);
//		/*
//		 * Test value
//		 */
//		Coordinate result = testclass.interfere(testPosition, testPosition, testTimestamp);
//
//		log.debug("Reference: " + testPosition.getX() + " - Changed: " + result.getX() + " - Difference:"
//				+ (testPosition.getX() - result.getX()));
//		log.debug("Reference: " + testPosition.getY() + " - Changed: " + result.getY() + " - Difference:"
//				+ (testPosition.getX() - result.getX()));
//
//		assertTrue(testPosition.getX() != result.getX());
//		assertTrue(testPosition.getY() != result.getY());
//	}
//}
