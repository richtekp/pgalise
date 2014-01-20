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
package de.pgalise.simulation.weathercollector.weatherstation;

import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets and saves informations of various weather stations
 *
 * @author Andreas Rehfeldt
 * @version 1.1 (Jun 16, 2012)
 */
@Stateful
public class DefaultWeatherStationManager implements WeatherStationManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(
		DefaultWeatherStationManager.class);

	/**
	 * Path to the file with strategies
	 */
	public static final String FILEPATH = "/weatherstations.xml";

	/**
	 * Class to save the informations
	 */
	@EJB
	private DatabaseManager saver;

	private Set<StationStrategy> stationStrategys;

	public DefaultWeatherStationManager() {
	}

	/**
	 * Constructor
	 *
	 *
	 * @param weatherStationSaver
	 */
	public DefaultWeatherStationManager(DatabaseManager weatherStationSaver) {
		this(weatherStationSaver,
			loadStrategiesFromFile());
	}

	public DefaultWeatherStationManager(DatabaseManager weatherStationSaver,
		Set<StationStrategy> stationStrategys) {
		this.saver = weatherStationSaver;
		this.stationStrategys = stationStrategys;
	}

	/**
	 * Saves all informations of the weather stations
	 */
	@Override
	public void saveInformations() {
		// Execute all strategies
		for (StationStrategy strategy : stationStrategys) {
			strategy.saveWeather(this.getSaver());
		}
	}

	/**
	 * Loads the strategies for the weather stations
	 *
	 * @return list with available strategies
	 */
	private static Set<StationStrategy> loadStrategiesFromFile() {
		Set<StationStrategy> list = new HashSet<>(3);

		try (InputStream propInFile = DefaultWeatherStationManager.class.
			getResourceAsStream(FILEPATH)) {
			// Read file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(propInFile);
			doc.getDocumentElement().normalize();

			// Get strategies node
			NodeList nList = doc.getElementsByTagName("strategy");

			// Get all strategies
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				String classname = nNode.getTextContent();

				// Add strategy
				Object strategyRaw = Class.forName(classname).newInstance();
				if (!(strategyRaw instanceof StationStrategy)) {
					throw new IllegalArgumentException(
						"file in %s contains invalid classname %s");
				}
				StationStrategy strategy = (StationStrategy) strategyRaw;
				list.add(strategy);
			}
		} catch (ParserConfigurationException | SAXException | IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(
				"Could not load the XML file for weather stations");
		}

		// Return list
		return list;
	}

	/**
	 * @return the saver
	 */
	public DatabaseManager getSaver() {
		return saver;
	}

	/**
	 * @param saver the saver to set
	 */
	public void setSaver(
		DatabaseManager saver) {
		this.saver = saver;
	}
}
