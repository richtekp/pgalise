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
 
package de.pgalise.simulation.weather.internal.dataloader;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataMap;

/**
 * Tests the class to load station data from a xml file.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 26, 2012)
 */
public class XMLWeatherLoaderTest {

	/**
	 * False test timestamp
	 */
	public static long falseTimestamp;

	/**
	 * Test class
	 */
	public static XMLFileWeatherLoader testClass;

	/**
	 * Test timestamp
	 */
	public static long testTimestamp;

	/**
	 * Path of test file
	 */
	private static String testFilePath;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		XMLWeatherLoaderTest.testClass = new XMLFileWeatherLoader();

		// Test
		Calendar cal = new GregorianCalendar();
		cal.set(2010, 1, 1, 20, 0, 0);
		XMLWeatherLoaderTest.testTimestamp = cal.getTimeInMillis();

		cal.set(2010, 2, 1, 20, 0, 0);
		XMLWeatherLoaderTest.falseTimestamp = cal.getTimeInMillis();

		// Set test file path
		XMLWeatherLoaderTest.testFilePath = XMLWeatherLoaderTest.testClass
				.getFilePath(XMLWeatherLoaderTest.testTimestamp);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		XMLWeatherLoaderTest.deleteFile();
	}

	/**
	 * Deletes the test file
	 */
	private static void deleteFile() {
		File file = new File(XMLWeatherLoaderTest.testFilePath);
		if (file.exists()) {
			file.delete();
		}
	}

	@After
	public void tearDown() throws Exception {
		XMLWeatherLoaderTest.deleteFile();
	}

	@Test
	public void testCheckStationDataForDay() throws Exception {
		// XML not available
		Assert.assertTrue(!(XMLWeatherLoaderTest.testClass.checkStationDataForDay(XMLWeatherLoaderTest.testTimestamp)));

		/*
		 * Test preparations
		 */
		if (!(new File(XMLWeatherLoaderTest.testFilePath).exists())) {
			this.createTestFile();
		}

		// XML available
		Assert.assertTrue(XMLWeatherLoaderTest.testClass.checkStationDataForDay(XMLWeatherLoaderTest.testTimestamp));

		// XML not available - wrong day
		Assert.assertTrue(!XMLWeatherLoaderTest.testClass.checkStationDataForDay(XMLWeatherLoaderTest.falseTimestamp));
	}

	@Test
	public void testLoadStationData() throws Exception {
		WeatherMap map = null;

		// Test load - fails (no file)
		try {
			map = XMLWeatherLoaderTest.testClass.loadStationData(XMLWeatherLoaderTest.testTimestamp);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		// Map is not available
		Assert.assertNull(map);

		/*
		 * Test preparations
		 */
		if (!(new File(XMLWeatherLoaderTest.testFilePath).exists())) {
			this.createTestFile();
		}

		// Test load
		try {
			map = XMLWeatherLoaderTest.testClass.loadStationData(XMLWeatherLoaderTest.testTimestamp);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}

		// Map is available
		Assert.assertNotNull(map);
	}

	@Test
	public void testSaveWeatherMapToXML() {
		WeatherMap map = new StationDataMap();
		Weather weather = new Weather(System.currentTimeMillis(), 1, 1, 1.0f, 1.0f, 1, 1.0f, 1.0f, 1, 1.0f);
		map.put(System.currentTimeMillis(), weather);

		// Test save
		try {
			XMLWeatherLoaderTest.testClass.saveWeatherMapToXML(map, XMLWeatherLoaderTest.testTimestamp);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
	}

	/**
	 * Creates a xml file for testing
	 * 
	 * @throws Exception
	 */
	private void createTestFile() throws Exception {
		WeatherMap map = new StationDataMap();
		Weather weather = new Weather(System.currentTimeMillis(), 1, 1, 1.0f, 1.0f, 1, 1.0f, 1.0f, 1, 1.0f);
		map.put(System.currentTimeMillis(), weather);

		XMLWeatherLoaderTest.testClass.saveWeatherMapToXML(map, XMLWeatherLoaderTest.testTimestamp);
	}
}
