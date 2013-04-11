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

import java.awt.Color;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Motorcycle;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 * Implements a factory for {@link Motorcycle}. The vehicles are loaded by a XML file.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 24, 2012)
 */
public class XMLMotorcycleFactory extends XMLAbstractFactory<MotorcycleData> implements MotorcycleFactory {

	/**
	 * Constructor
	 * 
	 * @param randomSeedService
	 *            Random Seed Service
	 * @param document
	 *            Document of the XML data
	 */
	public XMLMotorcycleFactory(RandomSeedService randomSeedService, Document document,
			TrafficGraphExtensions trafficGraphExtensions) {
		super(randomSeedService, document, trafficGraphExtensions);
	}

	/**
	 * Constructor
	 * 
	 * @param stream
	 *            Input stream of the XML data
	 * @param randomSeedService
	 *            Random Seed Service
	 */
	public XMLMotorcycleFactory(RandomSeedService randomSeedService, InputStream stream,
			TrafficGraphExtensions trafficGraphExtensions) {
		super(randomSeedService, stream, trafficGraphExtensions);
	}

	@Override
	public Vehicle<MotorcycleData> createRandomMotorcycle(UUID id, SensorHelper helper) {
		MotorcycleData data = getRandomVehicleData();
		data.setGpsSensorHelper(helper);
		return new DefaultMotorizedVehicle<MotorcycleData>(id, updateMotorcycleData(data, Color.BLACK), trafficGraphExtensions);
	}

	@Override
	public Vehicle<MotorcycleData> createMotorcycle(UUID id, String typeId, Color color, SensorHelper helper) {
		MotorcycleData data = getVehicleData().get(typeId);
		data.setGpsSensorHelper(helper);
		return new DefaultMotorizedVehicle<MotorcycleData>(id, updateMotorcycleData(data, color), trafficGraphExtensions);
	}

	/**
	 * Updates the {@link MotorcycleData} with new information
	 * 
	 * @param data
	 *            loaded {@link MotorcycleData}
	 * @param color
	 *            new Color
	 * @return updated {@link MotorcycleData} object
	 */
	private MotorcycleData updateMotorcycleData(MotorcycleData data, Color color) {
		data.setColor(color);

		return data;
	}

	/**
	 * Create new MotorcycleData
	 */
	@Override
	public MotorcycleData getRandomVehicleData() {
		MotorcycleData referenceData = super.getRandomVehicleData();

		return new MotorcycleData(referenceData);
	}

	@Override
	protected Map<String, MotorcycleData> readVehicleData(Document doc) {
		Map<String, MotorcycleData> vehicles = new HashMap<>();

		// Get strategies node
		NodeList nList = doc.getElementsByTagName("motorcycle");

		// Get all strategies
		for (int i = 0; i < nList.getLength(); i++) {
			/* Vehicle item */
			Node vehicleItem = nList.item(i);

			NodeList vehicleChildrens = vehicleItem.getChildNodes();

			// Init variables
			Color color = Color.WHITE;
			String typeid = "" + COUNTER, description = null;
			int length = 0, wheelbase = 0, maxSpeed = 0, weight = 0, axleCount = 0;
			double horsePower = 0;

			// Get properties of the vehicle
			for (int y = 0; y < vehicleChildrens.getLength(); y++) {
				/* Property item */
				Node propertyItem = vehicleChildrens.item(y);
				String nodeName = propertyItem.getNodeName();

				/* Check */
				if (nodeName.equals("typeid")) {
					typeid = propertyItem.getTextContent();
				} else if (nodeName.equals("wheelbase")) {
					wheelbase = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("length")) {
					length = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("weight")) {
					weight = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("horsePower")) {
					horsePower = Double.parseDouble(propertyItem.getTextContent());
				} else if (nodeName.equals("maxSpeed")) {
					maxSpeed = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("axleCount")) {
					axleCount = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("description")) {
					description = propertyItem.getTextContent();
				}
			}

			// Add new vehicle
			vehicles.put(typeid, new MotorcycleData(color, weight, horsePower, length, maxSpeed, wheelbase, axleCount,
					description, null));
		}

		// Returns
		return vehicles;
	}

}
