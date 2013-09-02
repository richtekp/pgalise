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
 
package de.pgalise.weathercollector.weatherstation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.pgalise.weathercollector.util.DatabaseManager;
import de.pgalise.weathercollector.util.Log;
import javax.persistence.EntityManagerFactory;

/**
 * Gets and saves informations of various weather stations
 * 
 * @author Andreas Rehfeldt
 * @version 1.1 (Jun 16, 2012)
 */
public final class WeatherStationManager {

	/**
	 * Path to the file with strategies
	 */
	public static final String FILEPATH = "/weatherstations.xml";

	/**
	 * Class to save the informations
	 */
	public WeatherStationSaver saver;

	/**
	 * Option to enable the test mode (no database commits)
	 */
	public boolean testmode = false;

	/**
	 * Current strategy
	 */
	private StationStrategy strategy;

	/**
	 * Constructor
	 * 
	 * @param testmode
	 *            Option to enable the test mode (no database commits)
	 */
	public WeatherStationManager(EntityManagerFactory entityManagerFactory) {
		this.saver = DatabaseManager.getInstance(entityManagerFactory);
	}

	/**
	 * Saves all informations of the weather stations
	 */
	public void saveInformations() {
		// Read the strategies from a file
		List<StationStrategy> slist;
		try {
			slist = this.loadStrategiesFromFile();
		} catch (Exception e) {
			Log.writeLog(e.getMessage(), Level.WARNING);
			e.printStackTrace();
			return;
		}

		// Execute all strategies
		for (StationStrategy strategy : slist) {
			try {
				this.strategy = strategy;
				this.strategy.saveWeather(this.saver);

			} catch (Exception e) {
				Log.writeLog(e.getMessage(), Level.WARNING);
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * Loads the strategies for the weather stations
	 * 
	 * @return list with available strategies
	 */
	@SuppressWarnings("unchecked")
	private List<StationStrategy> loadStrategiesFromFile() {
		List<StationStrategy> list = new ArrayList<>();

		try (InputStream propInFile = WeatherStationManager.class.getResourceAsStream(FILEPATH)) {
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
				StationStrategy strategy = ((Class<StationStrategy>) Class.forName(classname)).newInstance();
				list.add(strategy);
			}
		} catch (ParserConfigurationException | SAXException | IOException | InstantiationException
				| IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException("Could not load the XML file for weather stations");
		}

		// Return list
		return list;
	}
}
