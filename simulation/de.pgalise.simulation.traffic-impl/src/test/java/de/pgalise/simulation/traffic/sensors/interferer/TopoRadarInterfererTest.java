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
//import static org.junit.Assert.assertTrue;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.List;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import de.pgalise.simulation.service.RandomSeedService;
//import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.CompositeTopoRadarInterferer;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.TopoRadarBaseInterferer;
//import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.TopoRadarWhiteNoiseInterferer;
//import de.pgalise.simulation.traffic.server.sensor.interferer.TopoRadarInterferer;
//
///**
// * Tests the all GPSInterferer
// * 
// * @author Andreas Rehfeldt
// * @version 1.0 (Nov 12, 2012)
// */
//public class TopoRadarInterfererTest {
//
//	/**
//	 * Logger
//	 */
//	private static final Logger log = LoggerFactory.getLogger(TopoRadarInterfererTest.class);
//
//	/**
//	 * Test timestamp
//	 */
//	public static long testTimestamp;
//
//	/**
//	 * Test value
//	 */
//	public static int testWheelBase = 6000;
//
//	/**
//	 * Test value
//	 */
//	public static int testAxleCount = 3;
//
//	/**
//	 * Test value
//	 */
//	public static int testLength = 12000;
//
//	/**
//	 * Random seed service
//	 */
//	public static final RandomSeedService service = new DefaultRandomSeedService();
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		// Test timestamp
//		Calendar cal = new GregorianCalendar();
//		cal.set(2011, 2, 1, 14, 0, 0);
//		testTimestamp = cal.getTimeInMillis();
//	}
//
//	@Test
//	public void testGpsAtmosphereInterferer() {
//		TopoRadarBaseInterferer testclass = new TopoRadarWhiteNoiseInterferer(service);
//		testclass.setChangeProbability(1.0);
//
//		/*
//		 * Test value
//		 */
//		int[] result = testclass.interfere(testAxleCount, testLength, testWheelBase, testWheelBase, testTimestamp);
//
//		log.debug("Reference: " + testAxleCount + " - Changed: " + result[0]);
//		log.debug("Reference: " + testLength + " - Changed: " + result[1]);
//		log.debug("Reference: " + testWheelBase + " - Changed: " + result[2]);
//
//		assertTrue(testAxleCount != result[0]);
//		assertTrue(testLength != result[1]);
//		assertTrue(testWheelBase != result[2]);
//	}
//
//	@Test
//	public void testCompositeGpsInterferer() {
//		TopoRadarBaseInterferer interferer1 = new TopoRadarWhiteNoiseInterferer(service);
//		interferer1.setChangeProbability(1.0);
//
//		List<TopoRadarInterferer> list = new ArrayList<>();
//		list.add(interferer1);
//		TopoRadarInterferer testclass = new CompositeTopoRadarInterferer(list);
//
//		/*
//		 * Test value
//		 */
//		int[] result = testclass.interfere(testAxleCount, testLength, testWheelBase, testWheelBase, testTimestamp);
//
//		log.debug("Reference: " + testAxleCount + " - Changed: " + result[0]);
//		log.debug("Reference: " + testLength + " - Changed: " + result[1]);
//		log.debug("Reference: " + testWheelBase + " - Changed: " + result[2]);
//
//		assertTrue(testAxleCount != result[0]);
//		assertTrue(testLength != result[1]);
//		assertTrue(testWheelBase != result[2]);
//	}
//
//}
