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
 
package de.pgalise.simulation.energy.test;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.energy.internal.profile.CSVProfileLoader;
import de.pgalise.simulation.energy.profile.EnergyProfile;
import de.pgalise.simulation.energy.profile.EnergyProfileLoader;

/**
 * Tests the CSVProfileLoader if it can load the h0 profile
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 27, 2012)
 */
public class CSVProfileLoaderTest {

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
	public static EnergyProfileLoader testclass;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CSVProfileLoaderTest.testclass = new CSVProfileLoader();

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 11, 31, 0, 0, 0);
		CSVProfileLoaderTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2013, 0, 1, 0, 0, 0);
		CSVProfileLoaderTest.endTimestamp = cal.getTimeInMillis();
	}

	@Test
	public void testLoadProfile() throws IOException {
		/*
		 * Test preparations
		 */
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 11, 31, 23, 45, 0);
		long testTime = cal.getTimeInMillis(); // Last entry
		testTime = testTime - (testTime % 1000);

		/*
		 * RUN TEST
		 */
		EnergyProfile profile = CSVProfileLoaderTest.testclass.loadProfile(CSVProfileLoaderTest.startTimestamp,
				CSVProfileLoaderTest.endTimestamp,
				CSVProfileLoader.class.getResourceAsStream("/profile/H0_Niedersachsen.csv"));

		/*
		 * Test 1: Load okay?
		 */
		Assert.assertNotNull(profile);

		/*
		 * Test 2: Data found?
		 */
		Assert.assertFalse(profile.getData().isEmpty());

		/*
		 * Test 3: tests the data
		 */
		Assert.assertEquals(new Double(0.029571078), profile.getData().get(testTime));
	}

}
