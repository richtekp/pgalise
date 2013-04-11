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
 
package de.pgalise.simulation.energy.internal.profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import de.pgalise.simulation.energy.profile.EnergyProfile;
import de.pgalise.simulation.energy.profile.EnergyProfileLoaderLocal;

/**
 * Loads energy profiles from a csv file. The files can be changed in the folder 'profile'.
 * 
 * @author Andreas Rehfeldt
 */
public class CSVProfileLoader implements EnergyProfileLoaderLocal {

	/**
	 * One hour in millis
	 */
	public static final long HOUR = 3600000L;

	/**
	 * Default
	 */
	public CSVProfileLoader() {}
	
	/**
	 * Returns a timestamp (long) to the given string
	 * 
	 * @param timeString
	 *            Timestamp string
	 * @param pattern
	 *            Pattern for the string
	 * @return Timestamp in millis
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static long convertTime(String timeString, String pattern) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.parse(timeString).getTime();
	}

	@Override
	public EnergyProfile loadProfile(long startTimestamp, long endTimestamp, InputStream energyProfileInputStream)
			throws IOException {

		// Create profile
		EnergyProfile profile = new EnergyProfile(startTimestamp, endTimestamp, this.loadDataFromCSV(startTimestamp,
				endTimestamp, energyProfileInputStream));

		// Return
		return profile;
	}

	/**
	 * Loads the data from the given file
	 * 
	 * @param end
	 *            Timestamp of the last entry
	 * @param start
	 *            Timestamp of the first entry
	 * @param energyProfileInputStream
	 *            CSV file
	 * @return map with timestamp and data
	 * @throws IOException
	 *             File can not be read
	 */
	private Map<Long, Double> loadDataFromCSV(long start, long end, InputStream energyProfileInputStream)
			throws IOException {
		Map<Long, Double> profiledata = new HashMap<>();

		// Create reader
		try (CSVReader reader = new CSVReader(new InputStreamReader(energyProfileInputStream, "UTF-8"), ';')) {
			String[] nextLine;
			double value = 0;
			long time = 0;
			while ((nextLine = reader.readNext()) != null) {

				// Read line
				try {
					value = Double.parseDouble(nextLine[2]);
					time = convertTime(nextLine[0] + " " + nextLine[1], "dd.MM.yy HH:mm:ss");
				} catch (NumberFormatException | ParseException e) {
					e.printStackTrace();
					continue;
				}

				// Add to map
				if ((time >= start) && (time < end)) {
					profiledata.put(time, value);
				}
			}
		}

		// Return
		return profiledata;
	}
}
