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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficEdge;
import de.pgalise.simulation.traffic.internal.DefaultTrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 * Implements a factory for {@link Bike}. The vehicles are loaded by a XML file.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 24, 2012)
 */
public class ExtendedXMLBicycleFactory extends XMLAbstractFactory<BicycleData> implements BicycleFactory<DefaultTrafficNode<BicycleData>,DefaultTrafficEdge<BicycleData>, BaseVehicle<BicycleData>> {

	/**
	 * Constructor
	 * 
	 * @param randomSeedService
	 *            Random Seed Service
	 * @param document
	 *            Document of the XML data
	 */
	public ExtendedXMLBicycleFactory(RandomSeedService randomSeedService, Document document,
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
	public ExtendedXMLBicycleFactory(RandomSeedService randomSeedService, InputStream stream,
			TrafficGraphExtensions trafficGraphExtensions) {
		super(randomSeedService, stream, trafficGraphExtensions);
	}

	@Override
	public BaseVehicle<BicycleData> createRandomBicycle( SensorHelper helper) {
		BicycleData data = getRandomVehicleData();
		data.setGpsSensorHelper(helper);
		return new DefaultBicycle( "bicycle" + getNextCounter(), data, this.trafficGraphExtensions);
	}

	@Override
	public BaseVehicle<BicycleData> createBicycle( SensorHelper helper) {
		return createRandomBicycle(helper);
	}

	/**
	 * Create new BicycleData
	 */
	@Override
	public BicycleData getRandomVehicleData() {
		BicycleData referenceData = super.getRandomVehicleData();

		return new BicycleData(referenceData);
	}

	@Override
	protected Map<String, BicycleData> readVehicleData(Document doc) {
		Map<String, BicycleData> vehicles = new HashMap<>();

		// Get strategies node
		NodeList nList = doc.getElementsByTagName("bicycle");

		// Get all strategies
		for (int i = 0; i < nList.getLength(); i++) {
			/* Vehicle item */
			Node vehicleItem = nList.item(i);

			NodeList vehicleChildrens = vehicleItem.getChildNodes();

			// Init variables
			String typeid = "" + COUNTER, material = null, description = null;
			int size = 0, wheelbase = 0, maxSpeed = 0;
			double weight = 0;

			// Get properties of the vehicle
			for (int y = 0; y < vehicleChildrens.getLength(); y++) {
				/* Property item */
				Node propertyItem = vehicleChildrens.item(y);
				String nodeName = propertyItem.getNodeName();

				/* Check */
				if (nodeName.equals("typeid")) {
					typeid = propertyItem.getTextContent();
				} else if (nodeName.equals("description")) {
					description = propertyItem.getTextContent();
				} else if (nodeName.equals("wheelbase")) {
					wheelbase = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("size")) {
					size = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("weight")) {
					weight = Double.parseDouble(propertyItem.getTextContent());
				} else if (nodeName.equals("maxSpeed")) {
					maxSpeed = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("material")) {
					material = propertyItem.getTextContent();
				}
			}

			// Add new vehicle
			vehicles.put(typeid, new BicycleData(weight, maxSpeed, material, size, wheelbase, description, null));
		}

		// Returns
		return vehicles;
	}

}
