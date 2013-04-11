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
 
package de.pgalise.weathercollector.weatherservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.pgalise.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.weathercollector.model.City;
import de.pgalise.weathercollector.model.ServiceDataHelper;
import de.pgalise.weathercollector.util.Log;
import de.pgalise.weathercollector.weatherstation.WeatherStationManager;

/**
 * Uses strategies to read informations from weather services
 * 
 * @author Andreas Rehfeldt
 * @version 1.2 (Apr 15, 2012)
 */
public class WeatherServiceContext {

	/**
	 * Path to the file with strategies
	 */
	public static final String FILEPATH = "/weatherservices.xml";

	/**
	 * All available strategies
	 */
	private List<ServiceStrategy> strategies = new ArrayList<>();

	/**
	 * Current strategy
	 */
	private ServiceStrategy strategy;

	/**
	 * Constructor
	 */
	public WeatherServiceContext() {
		// Read the strategies from a file
		try {
			this.strategies.addAll(this.loadStrategiesFromFile());
		} catch (Exception e) {
			Log.writeLog(e.getMessage(), Level.WARNING);
			e.printStackTrace();
		}
	}

	/**
	 * Returns the best ServiceData
	 * 
	 * @param city
	 *            City
	 * @return Best ServiceData
	 */
	public ServiceDataHelper getBestWeather(City city) {

		// ServiceData objects
		ServiceDataHelper bestWeather = null;
		ServiceDataHelper tempWeather = null;

		// Deploy all strategies
		for (ServiceStrategy strategy : this.strategies) {
			try {
				// Set current strategy
				this.strategy = strategy;

				// --- Get informations ---
				tempWeather = this.strategy.getWeather(city);

				// --- Complete informations ---
				bestWeather = (bestWeather == null) ? tempWeather : ServiceStrategyLib.completeWeather(bestWeather,
						tempWeather);

			} catch (ReadServiceDataException e) {
				e.printStackTrace();
				continue;
			}
		}

		return bestWeather;
	}

	/**
	 * Returns the weather informations with the help of a random strategy for the given city
	 * 
	 * @param city
	 *            City
	 * @param strategyIndex
	 *            Index of the strategy
	 * @return ServiceData object
	 * @throws ReadServiceDataException
	 *             Data can not be read by strategy
	 */
	public ServiceDataHelper getSingleWeather(City city, int strategyIndex) throws ReadServiceDataException {
		if ((strategyIndex < 0) || (strategyIndex >= this.strategies.size())) {
			// Get random
			this.strategy = this.getRandomStrategy();
		} else {
			// Get index
			this.strategy = this.strategies.get(strategyIndex);
		}

		// Return informations
		return this.strategy.getWeather(city);
	}

	public List<ServiceStrategy> getStrategies() {
		return this.strategies;
	}

	/**
	 * Returns a random strategy
	 * 
	 * @return random ServiceStrategy
	 */
	private ServiceStrategy getRandomStrategy() {
		if (this.strategies.size() > 0) {
			return this.strategies.get(new Random().nextInt(this.strategies.size()));
		} else {
			return null;
		}
	}

	/**
	 * Loads the strategies for the weather stations
	 * 
	 * @return list with available strategies
	 */
	@SuppressWarnings("unchecked")
	private List<ServiceStrategy> loadStrategiesFromFile() {
		List<ServiceStrategy> list = new ArrayList<>();

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
				ServiceStrategy strategy = ((Class<ServiceStrategy>) Class.forName(classname)).newInstance();
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
