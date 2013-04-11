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
 
package de.pgalise.ssf.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.csv.CSVSensorOutput;

/**
 * Tests the CSVSensorOutput class
 * 
 * @author Andreas
 * @version 1.0
 */
public class CSVSensorOutputTest {

	/**
	 * Test class
	 */
	private static Output output;

	/**
	 * Test string
	 */
	private static String TESTSTRING = "TEST";

	/**
	 * Test file
	 */
	private static final String OUTPUT_FILE = "./testoutput.csv";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CSVSensorOutputTest.output = new CSVSensorOutput(CSVSensorOutputTest.OUTPUT_FILE);
	}

	/**
	 * Deletes the test file
	 */
	private static void deleteFile() {
		File file = new File(CSVSensorOutputTest.OUTPUT_FILE);
		if (file.exists()) {
			file.delete();
		}
	}

	@After
	public void tearDown() throws Exception {
		CSVSensorOutputTest.deleteFile();
	}

	@Test
	public void testTransmit() throws IOException {
		/*
		 * Save stream
		 */
		CSVSensorOutputTest.output.beginTransmit();
		CSVSensorOutputTest.output.transmitString(CSVSensorOutputTest.TESTSTRING);
		CSVSensorOutputTest.output.endTransmit();

		/*
		 * Evaluation
		 */
		try (BufferedReader reader = new BufferedReader(new FileReader(CSVSensorOutputTest.OUTPUT_FILE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				StringTokenizer st1 = new StringTokenizer(line, ";");
				Assert.assertEquals(CSVSensorOutputTest.TESTSTRING, st1.nextToken());
			}
		}
	}

}
