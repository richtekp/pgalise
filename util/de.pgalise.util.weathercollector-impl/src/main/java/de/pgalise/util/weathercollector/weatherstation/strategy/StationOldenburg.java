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
 
package de.pgalise.util.weathercollector.weatherstation.strategy;

import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataNormal;
import de.pgalise.simulation.weather.model.StationData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import de.pgalise.util.weathercollector.exceptions.SaveStationDataException;
import de.pgalise.util.weathercollector.util.Converter;
import de.pgalise.util.weathercollector.weatherstation.StationStrategy;
import de.pgalise.util.weathercollector.weatherstation.WeatherStationSaver;
import java.sql.Date;
import java.sql.Time;
import javax.measure.Measure;
import javax.measure.unit.SI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads the informations of the weather station in Oldenburg (Oldb) and saves them with the help of the given
 * WeatherStationSaver
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Jun 16, 2012)
 */
public class StationOldenburg implements StationStrategy {
	private final static Logger LOGGER = LoggerFactory.getLogger(StationOldenburg.class);

	/**
	 * File type
	 */
	private static final String FILETYP = ".txt";

	/**
	 * URL prefix
	 */
	private static final String URLPREFIX = "http://www.uni-oldenburg.de/dezernat4/wetter/ausgabe.php?datei=Wetter";

	/**
	 * Default constructor
	 */
	public StationOldenburg() {

	}

	@Override
	public void saveWeather(WeatherStationSaver saver) {
		Set<StationData> list = null;
		int month = -1; // To identify the previous month

		try {
			boolean result = false;
			do {
				// Reduce month
				month++;

				// Get URL
				String url = this.getURLForFile(month);
				LOGGER.debug("Lesen der Datei: " + url, Level.INFO);

				// Read the rows
				list = this.readLinesFromFile(url);

				// Save station data
				result = saver.saveStationDataSet(list);
			} while (result);
		} catch (SaveStationDataException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the URL of the txt for the weather station for the given month. The year is limited to 2000.
	 * 
	 * @param reduction
	 *            Declaration of the month to reduce of the current date
	 * @return URL of the txt file
	 */
	private String getURLForFile(int reduction) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, -reduction);

		// Month
		int month = cal.get(Calendar.MONTH) + 1;

		// Year
		int year = cal.get(Calendar.YEAR) - 2000; // ASSUMPTION: NOT MORE THAN 2000

		return URLPREFIX + Converter.getNumberWithNull(year) + Converter.getNumberWithNull(month) + FILETYP;
	}

	/**
	 * Returns the StationData object of the row
	 * 
	 * @param line
	 *            Row of the txt file
	 * @return StationData object
	 */
	private StationData getWeatherFromLine(String line) {
		// Nothing in the row?
		if ((line == null) || line.equals("") || (line.split("\t").length == 0)) {
			return null;
		}

		StationData weather;
		String[] weatherLine = line.split("\t");

		// Formatter for commas
		NumberFormat formatter = NumberFormat.getNumberInstance(Locale.GERMANY);

		try {
			weather = new StationDataNormal(
				Converter.convertDate(weatherLine[0], "dd.MM.yyyy"),
			Converter.convertTime(weatherLine[1], "h:mm:ss"),
			Integer.parseInt(weatherLine[10]),
			Integer.parseInt(weatherLine[5]),
			formatter.parse(weatherLine[4]).floatValue(),
			Measure.valueOf(formatter.parse(weatherLine[3]).floatValue(),SI.CELSIUS),
			formatter.parse(weatherLine[9]).floatValue(),
			Integer.parseInt(weatherLine[8]),
			formatter.parse(weatherLine[6]).floatValue(),
			formatter.parse(weatherLine[7]).floatValue(),
			formatter.parse(weatherLine[2]).floatValue());
		} catch (ParseException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			return null;
		}

		return weather;
	}

	/**
	 * Reads the rows of the txt file of the given URL.
	 * 
	 * @param fileurl
	 *            URL to the txt file
	 * @return List with StationData objects
	 * @throws IOException
	 *             Will be thrown if the file can not be read
	 */
	private Set<StationData> readLinesFromFile(String fileurl) throws IOException {
		// Sorted list (sort by time and date)
		Set<StationData> list = new TreeSet<StationData>();
		URL url = new URL(fileurl);

		try (InputStream inputStream = url.openStream()) {
			// Read txt file
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
			String line = null;

			while ((line = in.readLine()) != null) {
				// Read weather informations
				StationData weather = this.getWeatherFromLine(line);
				if (weather != null) {
					list.add(weather);
				}
			}

			// Close stream
			if (in.ready()) {
				in.close();
			}
		}

		return list;
	}

}
