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
 
package de.pgalise.simulation.playground.determinism;

import java.io.File;
import java.io.FileWriter;

/**
 * Write test data
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Feb 13, 2013)
 */
public class TestWriter {

	/**
	 * File path to the info file
	 */
	private static final String OUTPUT_FILE = "./test.txt";

	/**
	 * Current instance
	 */
	private static TestWriter instance = null;

	/**
	 * Default constructor
	 */
	public TestWriter() {
		deleteOldFile(OUTPUT_FILE);
	}

	/**
	 * Delete the file
	 * 
	 * @param filepath
	 *            File path
	 */
	public void deleteOldFile(String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * Append new lines
	 */
	public static void append(Object obj) {
		append(obj.toString());
	}

	/**
	 * Append new lines
	 */
	public static void append(String line) {
		// Write file
		try (FileWriter fileWriter = new FileWriter(OUTPUT_FILE, true)) {
			/* Start Parameter */
			fileWriter.write(line + "\n");

			// fileWriter.write("\n");

			// write
			fileWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static TestWriter getInstance() {
		if (instance == null) {
			instance = new TestWriter();
		}

		return instance;
	}

}
