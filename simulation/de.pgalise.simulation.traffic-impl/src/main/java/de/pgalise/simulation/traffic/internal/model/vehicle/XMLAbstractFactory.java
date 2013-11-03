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
 
package de.pgalise.simulation.traffic.internal.model.vehicle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * Abstract XML factory for the vehicle factories.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 26, 2012)
 * @param <D>
 *            Extension of the VehicleData
 */
public abstract class XMLAbstractFactory<D extends VehicleData> {

	/**
	 * Random generator
	 */
	protected final Random random;

	/**
	 * Random Seed Service
	 */
	protected final RandomSeedService randomSeedService;

	/**
	 * Counter for the generated vehicles
	 */
	protected static int COUNTER = 0;

	/**
	 * Returns the next counter for a generated vehicle
	 * 
	 * @return counter + 1
	 */
	public static int getNextCounter() {
		return ++XMLAbstractFactory.COUNTER;
	}

	/**
	 * Map of loaded specific {@link VehicleData} and the vehicleTypeId
	 */
	private Map<String, T> vehicleData;

	/**
	 * TrafficGraphExtensions
	 */
	protected TrafficGraphExtensions trafficGraphExtensions;

	/**
	 * Constructor
	 * 
	 * @param stream
	 *            Input stream of the XML data
	 * @param randomSeedService
	 *            Random Seed Service
	 * @param trafficGraphExtensions
	 *            TrafficGraphExtensions
	 */
	public XMLAbstractFactory(RandomSeedService randomSeedService, InputStream stream,
			TrafficGraphExtensions trafficGraphExtensions) {
		this(randomSeedService, readXMLInputstream(stream), trafficGraphExtensions);
	}

	/**
	 * Constructor
	 * 
	 * @param randomSeedService
	 *            Random Seed Service
	 * @param document
	 *            Document of the XML data
	 * @param trafficGraphExtensions
	 *            TrafficGraphExtensions
	 */
	public XMLAbstractFactory(RandomSeedService randomSeedService, Document document,
			TrafficGraphExtensions trafficGraphExtensions) {
		if (randomSeedService == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("randomSeedService"));
		}
		if (document == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("document"));
		}

		// set random
		this.randomSeedService = randomSeedService;
		this.random = new Random(this.randomSeedService.getSeed(XMLAbstractFactory.class.getName()));

		// load data
		this.vehicleData = readVehicleData(document);
		this.trafficGraphExtensions = trafficGraphExtensions;
	}

	public Map<String, T> getVehicleData() {
		return this.vehicleData;
	}

	public void setVehicleData(Map<String, T> vehicleData) {
		this.vehicleData = vehicleData;
	}

	/**
	 * Returns a random {@link VehicleData} object of the loaded map
	 * 
	 * @return random {@link VehicleData}
	 */
	public T getRandomVehicleData() {
		if (vehicleData.isEmpty()) {
			throw new RuntimeException("No vehicles are loaded!");
		}

		// return random value
		List<T> valuesList = new ArrayList<>(vehicleData.values());
		int randomIndex = random.nextInt(valuesList.size());

		return valuesList.get(randomIndex);
	}

	/**
	 * Reads the input stream of the XML file
	 * 
	 * @param stream
	 *            input stream of the XML file
	 * @return XML document
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Document readXMLInputstream(InputStream stream) {
		if (stream == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("stream"));
		}

		Document doc = null;
		try {
			// Read file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(stream);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException(e.getMessage());
		}

		// Return
		return doc;
	}

	/**
	 * Reads the {@link VehicleData} informations from the XML document
	 * 
	 * @param doc
	 *            XML document
	 * @return Map with typeID and {@link VehicleData}
	 */
	protected abstract Map<String, T> readVehicleData(Document doc);

}
