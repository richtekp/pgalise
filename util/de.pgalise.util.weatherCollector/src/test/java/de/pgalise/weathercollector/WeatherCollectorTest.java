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
 
package de.pgalise.weathercollector;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.pgalise.weathercollector.app.WeatherCollector;

/**
 * Tests the weather collector
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
@Ignore
public class WeatherCollectorTest {

	/**
	 * Test mode on?
	 */
	public static boolean TEST_MODE = true;

	/**
	 * Test class
	 */
	public static WeatherCollector testclass = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testclass = new WeatherCollector(TEST_MODE);
	}

	@Test
	public void testCollectServiceData() {
		// Checks the function in testmode
		try {
			testclass.collectServiceData();
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCollectStationData() {
		// Checks the function in testmode
		try {
			testclass.collectStationData();
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

}
