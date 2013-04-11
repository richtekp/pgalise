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
import java.io.IOException;
import java.util.Date;

import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;

/**
 * Helper for scale tests of the simulation
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Feb 11, 2013)
 */
public class ScaleTestHelper {

	/**
	 * File path to the info file
	 */
	private static final String OUTPUT_FILE = "./simulationInfos.csv";

	/**
	 * Default constructor
	 */
	public ScaleTestHelper() {
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
	 * Creates a statistic of the two simulations
	 * 
	 * @param startParameter
	 *            Start parameter of the two simulation
	 * @param staticSensors
	 *            Number of static sensors of the two simulation
	 * @param initParameters1
	 *            Init parameter of the first simulation
	 * @param simulation1Duration
	 *            Duration of the first simulation in millis
	 * @param simulationEvents1
	 *            Number of vehicle sensors of the first simulation
	 * @param transmits1
	 *            Number of transmits of the first simulation
	 * @param initParameters2
	 *            Init parameter of the second simulation
	 * @param simulation2Duration
	 *            Duration of the second simulation in millis
	 * @param simulationEvents2
	 *            Number of vehicle sensors of the second simulation
	 * @param transmits2
	 *            Number of transmits of the second simulation
	 * @throws IOException
	 *             Can not write to file
	 */
	public void benchmark(StartParameter startParameter, int staticSensors, InitParameter initParameters1,
			long simulation1Duration, int simulationEvents1, int transmits1, InitParameter initParameters2,
			long simulation2Duration, int simulationEvents2, int transmits2) throws IOException {

		// Write file
		try (FileWriter fileWriter = new FileWriter(OUTPUT_FILE, true)) {
			/* Start Parameter */
			fileWriter.write("Start timestamp;" + new Date(initParameters1.getStartTimestamp()) + ";\n");
			fileWriter.write("End timestamp;" + new Date(initParameters1.getEndTimestamp()) + ";\n");
			fileWriter.write("City;" + startParameter.getCity().getName() + ";\n");
			fileWriter.write("Simulationinterval (s);" + (initParameters1.getInterval() / 1000) + ";\n");

			fileWriter.write("\n");

			/* Simulation 1 */
			fileWriter.write("Simulation 1;\n");
			fileWriter.write("Number of static sensors;" + staticSensors + ";\n");
			fileWriter.write("Number of vehicles;" + simulationEvents1 + ";\n");
			fileWriter.write("Duration (s);" + (simulation1Duration / 1000) + ";\n");
			fileWriter.write("Number of transmits;" + transmits1 + ";\n");

			fileWriter.write("\n");

			/* Simulation 2 */
			fileWriter.write("Simulation 2;\n");
			fileWriter.write("Number of static sensors;" + staticSensors + ";\n");
			fileWriter.write("Number of vehicles;" + simulationEvents2 + ";\n");
			fileWriter.write("Duration (s);" + (simulation2Duration / 1000) + ";\n");
			fileWriter.write("Number of transmits;" + transmits2 + ";\n");

			// write
			fileWriter.flush();
		}
	}
}
