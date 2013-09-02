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
 
package de.pgalise.weathercollector.weatherstation.strategy;

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

import de.pgalise.weathercollector.exceptions.SaveStationDataException;
import de.pgalise.weathercollector.model.StationData;
import de.pgalise.weathercollector.util.Converter;
import de.pgalise.weathercollector.util.Log;
import de.pgalise.weathercollector.weatherstation.StationStrategy;
import de.pgalise.weathercollector.weatherstation.WeatherStationSaver;

/**
 * Reads the informations of the weather station in Oldenburg (Oldb) and saves them with the help of the given
 * WeatherStationSaver
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Jun 16, 2012)
 */
public final class StationOldenburg implements StationStrategy {

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
				Log.writeLog("Lesen der Datei: " + url, Level.INFO);

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

		StationData weather = new StationData();
		String[] weatherLine = line.split("\t");

		// Formatter for commas
		NumberFormat formatter = NumberFormat.getNumberInstance(Locale.GERMANY);

		try {
			// Date
			weather.setDate(Converter.convertDate(weatherLine[0], "dd.MM.yyyy"));

			// Time
			weather.setTime(Converter.convertTime(weatherLine[1], "h:mm:ss"));

			// Wind velocity
			weather.setWindVelocity(formatter.parse(weatherLine[2]).floatValue());

			// Temperature
			weather.setTemperature(formatter.parse(weatherLine[3]).floatValue());

			// Temperature
			weather.setPerceivedTemperature(formatter.parse(weatherLine[4]).floatValue());

			// Light intensity
			weather.setLightIntensity(Integer.parseInt(weatherLine[5]));

			// relative humidity
			weather.setRelativHumidity(formatter.parse(weatherLine[6]).floatValue());

			// wind direction
			weather.setWindDirection(Integer.parseInt(weatherLine[7]));

			// radiation
			weather.setRadiation(Integer.parseInt(weatherLine[8]));

			// precipication amount
			weather.setPrecipitationAmount(formatter.parse(weatherLine[9]).floatValue());

			// air pressure
			weather.setAirPressure(Integer.parseInt(weatherLine[10]));

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
