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
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 * Implements a factory for {@link Bus}. The vehicles are loaded by a XML file.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 24, 2012)
 */
public class XMLBusFactory extends XMLAbstractFactory<BusData> implements BusFactory<DefaultTrafficNode<BusData>,DefaultTrafficEdge<BusData>, BaseVehicle<BusData>> {

	/**
	 * Constructor
	 * 
	 * @param randomSeedService
	 *            Random Seed Service
	 * @param document
	 *            Document of the XML data
	 */
	public XMLBusFactory(RandomSeedService randomSeedService, Document document,
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
	public XMLBusFactory(RandomSeedService randomSeedService, InputStream stream,
			TrafficGraphExtensions trafficGraphExtensions) {
		super(randomSeedService, stream, trafficGraphExtensions);
	}

	/**
	 * Create new BicycleData
	 */
	@Override
	public BusData getRandomVehicleData() {
		BusData referenceData = super.getRandomVehicleData();

		return new BusData(referenceData);
	}

	@Override
	public BaseVehicle<BusData> createRandomBus( SensorHelper helper, SensorHelper infraredSensor) {
		BusData data = getRandomVehicleData();
		data.setGpsSensorHelper(helper);
		data.setInfraredSensorHelper(infraredSensor);
		return new DefaultMotorizedVehicle<>( "bus" + getNextCounter(), data, this.trafficGraphExtensions);
	}

	@Override
	public BaseVehicle<BusData> createBus(  SensorHelper helper, SensorHelper infraredSensor) {
		return createRandomBus(helper,
			infraredSensor);
	}

	@Override
	protected Map<String, BusData> readVehicleData(Document doc) {
		Map<String, BusData> vehicles = new HashMap<>();

		// Get strategies node
		NodeList nList = doc.getElementsByTagName("bus");

		// Get all strategies
		for (int i = 0; i < nList.getLength(); i++) {
			/* Vehicle item */
			Node vehicleItem = nList.item(i);

			NodeList vehicleChildrens = vehicleItem.getChildNodes();

			// Init variables
			String typeid = "" + COUNTER, description = null;
			int length = 0, axleCount = 0, wheelbase1 = 0, wheelbase2 = 0, height = 0, maxSpeed = 0, weight = 0, width = 0, maxPassengerCount = 0;
			double power = 0;

			// Get properties of the vehicle
			for (int y = 0; y < vehicleChildrens.getLength(); y++) {
				/* Property item */
				Node propertyItem = vehicleChildrens.item(y);
				String nodeName = propertyItem.getNodeName();

				/* Check */
				if (nodeName.equals("typeid")) {
					typeid = propertyItem.getTextContent();
				} else if (nodeName.equals("wheelbase1")) {
					wheelbase1 = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("wheelbase2")) {
					wheelbase2 = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("length")) {
					length = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("width")) {
					width = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("height")) {
					height = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("weight")) {
					weight = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("power")) {
					power = Double.parseDouble(propertyItem.getTextContent());
				} else if (nodeName.equals("maxSpeed")) {
					maxSpeed = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("axleCount")) {
					axleCount = Integer.parseInt(propertyItem.getTextContent());
				} else if (nodeName.equals("description")) {
					description = propertyItem.getTextContent();
				} else if (nodeName.equals("maxPassengerCount")) {
					maxPassengerCount = Integer.parseInt(propertyItem.getTextContent());
				}
			}

			// Add new vehicle
			vehicles.put(typeid, new BusData(wheelbase1, wheelbase2, length, width, height, weight, power, maxSpeed,
					description, maxPassengerCount, 0, axleCount, null, null));
		}

		// Returns
		return vehicles;
	}

}
