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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.dataloader.ServiceWeather;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;

/**
 * This class loads the weather data from a xml file. <br />
 * <br />
 * The file weatherloader.properties provides the default file path of the {@link XMLFileWeatherLoader} in more detail.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (17.09.2012)
 */
public final class XMLFileWeatherLoader implements WeatherLoader {

	/**
	 * File extension of the file
	 */
	public static final String FILE_EXTENSION = ".xml";

	/**
	 * Prefix of the file
	 */
	public static final String PREFIX = "weather_";

	/**
	 * File path for property file
	 */
	private static final String PROPERTIES_FILE_PATH = "/weatherloader.properties";

	/**
	 * File path to the xml file
	 */
	private String filePath = "";

	/**
	 * Constructor
	 */
	public XMLFileWeatherLoader() {
		// Read props
		Properties prop = null;
		try (InputStream propInFile = XMLFileWeatherLoader.class
				.getResourceAsStream(XMLFileWeatherLoader.PROPERTIES_FILE_PATH)) {
			prop = new Properties();
			prop.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Set xml file path
		this.filePath = prop.getProperty("station_data_xml_filepath");
	}

	@Override
	public boolean checkStationDataForDay(long timestamp) {
		File file = new File(this.getFilePath(timestamp));
		return (file.exists()) ? true : false;
	}

	/**
	 * Returns the file path
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return file path
	 */
	public String getFilePath(long timestamp) {
		DateFormat formatter = new SimpleDateFormat("YYYY_MM_dd");
		String dateString = formatter.format(timestamp);

		return this.filePath + XMLFileWeatherLoader.PREFIX + dateString + XMLFileWeatherLoader.FILE_EXTENSION;
	}

	@Override
	public ServiceWeather loadCurrentServiceWeatherData(long timestamp, City city) throws NoWeatherDataFoundException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public ServiceWeather loadForecastServiceWeatherData(long timestamp, City city) throws NoWeatherDataFoundException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public WeatherMap loadStationData(long timestamp) throws NoWeatherDataFoundException {
		// Deserialize
		WeatherMap map = null;
		long loadeddate;
		try (XMLDecoder dec = new XMLDecoder(new FileInputStream(this.getFilePath(timestamp)))) {
			// Load map
			loadeddate = (long) dec.readObject();
			map = (WeatherMap) dec.readObject();
		} catch (IOException e) {
			throw new NoWeatherDataFoundException();
		}

		// Check if the date is correct
		if (loadeddate == timestamp) {
			return map;
		} else {
			throw new NoWeatherDataFoundException();
		}
	}

	/**
	 * Saves weather informations to XML
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @param map
	 *            WeatherMap
	 */
	public void saveWeatherMapToXML(WeatherMap map, long timestamp) {
		// Serialize
		try (XMLEncoder enc = new XMLEncoder(new FileOutputStream(this.getFilePath(timestamp)))) {
			// Save map
			enc.writeObject(timestamp);
			enc.writeObject(map);
		} catch (IOException e) {
			throw new IllegalArgumentException("timestamp");
		}
	}

	@Override
	public void setLoadOption(boolean takeNormalData) {
		// Do nothing
	}

}
