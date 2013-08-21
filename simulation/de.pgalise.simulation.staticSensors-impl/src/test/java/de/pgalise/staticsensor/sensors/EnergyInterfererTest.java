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

import com.vividsolutions.jts.geom.Coordinate;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.staticsensor.sensor.energy.EnergyInterferer;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.CompositeEnergyInterferer;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.EnergyBaseInterferer;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.PhotovoltaikWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.SmartMeterWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.WindPowerWhiteNoiseInterferer;
import javax.vecmath.Vector2d;

/**
 * Tests the all {@link EnergyInterferer}
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Nov 12, 2012)
 */
public class EnergyInterfererTest {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(EnergyInterfererTest.class);

	/**
	 * Test timestamp
	 */
	public static long testTimestamp;

	/**
	 * Test position
	 */
	public static Coordinate testPosition;

	/**
	 * Test value
	 */
	public static double testValue;

	/**
	 * Random seed service
	 */
	public static final RandomSeedService service = new DefaultRandomSeedService();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// Start
		Calendar cal = new GregorianCalendar();

		// Test timestamp
		cal.set(2011, 2, 1, 14, 0, 0);
		testTimestamp = cal.getTimeInMillis();

		// Test position
		testPosition = new Coordinate(1.0, 10.0);
	}

	@Test
	public void testWindFlagWhiteNoiseInterferer() {
		EnergyBaseInterferer testclass = new WindPowerWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		testValue = 100.0;

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue, testPosition, testTimestamp);

		log.debug("WindPower - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testSmartMeterWhiteNoiseInterferer() {
		EnergyBaseInterferer testclass = new SmartMeterWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		testValue = 100.0;

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue, testPosition, testTimestamp);

		log.debug("SmartMeter - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testPhotovoltaikWhiteNoiseInterferer() {
		EnergyBaseInterferer testclass = new PhotovoltaikWhiteNoiseInterferer(service);
		testclass.setChangeProbability(1.0);

		testValue = 100.0;

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue, testPosition, testTimestamp);

		log.debug("Photovoltaik - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

	@Test
	public void testCompositeEnergyInterferer() {
		EnergyBaseInterferer interferer1 = new PhotovoltaikWhiteNoiseInterferer(service);
		interferer1.setChangeProbability(1.0);

		List<EnergyInterferer> list = new ArrayList<>();
		list.add(interferer1);
		EnergyInterferer testclass = new CompositeEnergyInterferer(list);

		testValue = 100.0;

		/*
		 * Test value
		 */
		double result = testclass.interfere(testValue, testPosition, testTimestamp);

		log.debug("Composite - Reference: " + testValue + " - Changed: " + result);

		assertTrue(testValue != result);
	}

}
