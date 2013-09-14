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
 
package de.pgalise.util.weathercollector.weatherservice;

import de.pgalise.simulation.shared.city.City;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses strategies to read informations from weather services
 * 
 * @author Andreas Rehfeldt
 * @version 1.2 (Apr 15, 2012)
 */
public class DefaultWeatherServiceContext implements WeatherServiceContext<DefaultServiceDataHelper> {
	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultWeatherServiceContext.class);

	/**
	 * Path to the file with strategies
	 */
	public static final String FILEPATH = "/weatherservices.xml";

	/**
	 * All available strategies
	 */
	private Set<ServiceStrategy<DefaultServiceDataHelper>> strategies;

	/**
	 * Current strategy
	 */
	private ServiceStrategy<DefaultServiceDataHelper> strategy;

	/**
	 * Constructor
	 */
	public DefaultWeatherServiceContext() {
		this(loadStrategiesFromFile());
	}
	
	public DefaultWeatherServiceContext(Set<ServiceStrategy<DefaultServiceDataHelper>> serviceStrategys) {
		this.strategies = serviceStrategys;
	}

	/**
	 * Returns the best ServiceData
	 * 
	 * @param city
	 *            City
	 * @return Best ServiceData
	 */
	@Override
	public DefaultServiceDataHelper getBestWeather(City city, DatabaseManager databaseManager) {

		// ServiceData objects
		DefaultServiceDataHelper bestWeather = null;
		DefaultServiceDataHelper tempWeather;

		// Deploy all strategies
		for (ServiceStrategy<DefaultServiceDataHelper> strategy0 : this.strategies) {
			try {
				// Set current strategy
				this.strategy = strategy0;

				// --- Get informations ---
				tempWeather = this.strategy.getWeather(city, databaseManager);

				// --- Complete informations ---
				bestWeather = (bestWeather == null) ? tempWeather : ServiceStrategyLib.completeWeather(bestWeather,
						tempWeather);

			} catch (ReadServiceDataException e) {
				LOGGER.warn("see nested exception",e);
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
	 * @param databaseManager 
	 * @return ServiceData object
	 * @throws ReadServiceDataException
	 *             Data can not be read by strategy
	 */
	@Override
	public DefaultServiceDataHelper getSingleWeather(City city, DatabaseManager databaseManager) throws ReadServiceDataException {
		this.strategy = this.getRandomStrategy();

		// Return informations
		return this.strategy.getWeather(city, databaseManager);
	}

	public Set<ServiceStrategy<DefaultServiceDataHelper>> getStrategies() {
		return this.strategies;
	}

	/**
	 * Returns a random strategy
	 * 
	 * @return random ServiceStrategy
	 */
	private ServiceStrategy<DefaultServiceDataHelper> getRandomStrategy() {
		
		if (this.strategies.size() > 0) {
			return new LinkedList<>(this.strategies).get(new Random().nextInt(this.strategies.size()));
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
	private static Set<ServiceStrategy<DefaultServiceDataHelper>> loadStrategiesFromFile() {
		Set<ServiceStrategy<DefaultServiceDataHelper>> list = new HashSet<>(3);

		try (InputStream propInFile = DefaultWeatherServiceContext.class.getResourceAsStream(FILEPATH)) {
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
				ServiceStrategy<DefaultServiceDataHelper> strategy = ((Class<ServiceStrategy>) Class.forName(classname)).newInstance();
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
