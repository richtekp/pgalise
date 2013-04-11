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
 
package de.pgalise.simulation.traffic.sensors.interferer;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.model.vehicle.XMLTruckFactory;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.CompositeInductionLoopInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopBaseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredBaseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.sensor.interferer.InductionLoopInterferer;
import de.pgalise.simulation.traffic.vehicle.XMLTruckFactoryTest;

/**
 * Tests the all GPSInterferer
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Nov 12, 2012)
 */
public class TrafficCountInterfererTest {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(TrafficCountInterfererTest.class);

	/**
	 * Test timestamp
	 */
	public static long testTimestamp;

	/**
	 * Test value
	 */
	public static int testValue = 1;

	/**
	 * Test vehicle
	 */
	public static Vehicle<? extends VehicleData> testVehicle;

	/**
	 * Path to the XML file
	 */
	public static final String FILEPATH = "/defaultvehicles.xml";

	/**
	 * Random seed service
	 */
	public static final RandomSeedService service = new DefaultRandomSeedService();

	@Before
	public void setUp() throws Exception {
		// Test timestamp
		Calendar cal = new GregorianCalendar();
		cal.set(2011, 2, 1, 14, 0, 0);
		testTimestamp = cal.getTimeInMillis();

		// Test car
		TruckFactory factory = new XMLTruckFactory(service, XMLTruckFactoryTest.class.getResourceAsStream(FILEPATH),
				new DefaultTrafficGraphExtensions(service));
		testVehicle = factory.createRandomTruck(UUID.randomUUID(), null);
		testVehicle.setVelocity(3);
	}

	@Test
	public void testInductionLoopWhiteNoiseInterferer() {
		InductionLoopBaseInterferer testclass = new InductionLoopWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		/*
		 * Test value
		 */
		int result = testclass.interfere(testVehicle.getData().getLength(), 50, testVehicle.getVelocity());

		log.debug("InductionLoop - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testInfraredWhiteNoiseInterferer() {
		InfraredBaseInterferer testclass = new InfraredWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		/*
		 * Test value
		 */
		int result = testclass.interfere(100);

		log.debug("Infrared - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testCompositeInterferer() {
		InductionLoopBaseInterferer interferer1 = new InductionLoopWhiteNoiseInterferer(service);
		interferer1.setChangeProbability(1.0);

		List<InductionLoopInterferer> list = new ArrayList<>();
		list.add(interferer1);

		InductionLoopInterferer testclass = new CompositeInductionLoopInterferer(list);

		/*
		 * Test value
		 */
		int result = testclass.interfere(testVehicle.getData().getLength(), 50, testVehicle.getVelocity());

		log.debug("Composite (InductionLoop) - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

}
