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
import java.util.UUID;

import org.w3c.dom.Document;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.TruckData;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 * Factory for all vehicles in the simulation. Reads a given XML file and gives the individual root nodes to the
 * specific vehicle factories.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 24, 2012)
 */
public final class XMLVehicleFactory implements CarFactory, BusFactory, TruckFactory, MotorcycleFactory, BicycleFactory {

	/**
	 * Car factory
	 */
	private final CarFactory carFactory;

	/**
	 * Bus factory
	 */
	private final BusFactory busFactory;

	/**
	 * Truck factory
	 */
	private final TruckFactory truckFactory;

	/**
	 * Motorcycle factory
	 */
	private final MotorcycleFactory motorcycleFactory;

	/**
	 * Bicycle factory
	 */
	private final BicycleFactory bicycleFactory;

	/**
	 * XML File
	 */
	private final InputStream xmlFile;

	/**
	 * {@link RandomSeedService}
	 */
	private final RandomSeedService randomSeedService;

	/**
	 * Constructor
	 * 
	 * @param randomSeedService
	 *            Random Seed Service
	 * @param input
	 *            Input stream to the XML file
	 */
	public XMLVehicleFactory(RandomSeedService randomSeedService, InputStream input,
			TrafficGraphExtensions trafficGraphExtensions) {
		if (input == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("input"));
		}

		this.randomSeedService = randomSeedService;
		this.xmlFile = input;

		// read XML
		Document doc = XMLAbstractFactory.readXMLInputstream(input);

		// create factories
		this.bicycleFactory = new XMLBicycleFactory(randomSeedService, doc, trafficGraphExtensions);
		this.busFactory = new XMLBusFactory(randomSeedService, doc, trafficGraphExtensions);
		this.truckFactory = new XMLTruckFactory(randomSeedService, doc, trafficGraphExtensions);
		this.motorcycleFactory = new XMLMotorcycleFactory(randomSeedService, doc, trafficGraphExtensions);
		this.carFactory = new ExtendedXMLCarFactory(randomSeedService, doc, trafficGraphExtensions);
	}

	@Override
	public Vehicle<BicycleData> createBicycle(UUID id, String typeId, SensorHelper gpsSensor) {
		return this.bicycleFactory.createBicycle(id, typeId, gpsSensor);
	}

	@Override
	public Vehicle<BusData> createBus(UUID id, String typeId, SensorHelper gpsSensor, SensorHelper infraredSensor) {
		return this.busFactory.createBus(id, typeId, gpsSensor, infraredSensor);
	}

	@Override
	public Vehicle<CarData> createCar(UUID id, String typeId, Color color, SensorHelper gpsSensor) {
		return this.carFactory.createCar(id, typeId, color, gpsSensor);
	}

	@Override
	public Vehicle<MotorcycleData> createMotorcycle(UUID id, String typeId, Color color, SensorHelper gpsSensor) {
		return this.motorcycleFactory.createMotorcycle(id, typeId, color, gpsSensor);
	}

	@Override
	public Vehicle<BicycleData> createRandomBicycle(UUID id, SensorHelper gpsSensor) {
		return this.bicycleFactory.createRandomBicycle(id, gpsSensor);
	}

	@Override
	public Vehicle<BusData> createRandomBus(UUID id, SensorHelper gpsSensor, SensorHelper infraredSensor) {
		return this.busFactory.createRandomBus(id, gpsSensor, infraredSensor);
	}

	@Override
	public Vehicle<CarData> createRandomCar(UUID id, SensorHelper gpsSensor) {
		return this.carFactory.createRandomCar(id, gpsSensor);
	}

	@Override
	public Vehicle<MotorcycleData> createRandomMotorcycle(UUID id, SensorHelper gpsSensor) {
		return this.motorcycleFactory.createRandomMotorcycle(id, gpsSensor);
	}

	@Override
	public Vehicle<TruckData> createRandomTruck(UUID id, SensorHelper gpsSensor) {
		return this.truckFactory.createRandomTruck(id, gpsSensor);
	}

	@Override
	public Vehicle<TruckData> createTruck(UUID id, String typeId, Color color, int trailercount, SensorHelper gpsSensor) {
		return this.truckFactory.createTruck(id, typeId, color, trailercount, gpsSensor);
	}

	public BicycleFactory getBicycleFactory() {
		return this.bicycleFactory;
	}

	public BusFactory getBusFactory() {
		return this.busFactory;
	}

	public CarFactory getCarFactory() {
		return this.carFactory;
	}

	public MotorcycleFactory getMotorcycleFactory() {
		return this.motorcycleFactory;
	}

	public TruckFactory getTruckFactory() {
		return this.truckFactory;
	}

	public InputStream getXmlFile() {
		return this.xmlFile;
	}

	public RandomSeedService getRandomSeedService() {
		return randomSeedService;
	}

}
