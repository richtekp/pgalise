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

import org.w3c.dom.Document;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.sensorFramework.SensorHelper;
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
public class ExtendedXMLVehicleFactory implements CarFactory, BusFactory, TruckFactory, MotorcycleFactory,
		BicycleFactory {

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
	public ExtendedXMLVehicleFactory(RandomSeedService randomSeedService, InputStream input,
			TrafficGraphExtensions trafficGraphExtensions) {
		if (input == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("input"));
		}

		this.randomSeedService = randomSeedService;
		this.xmlFile = input;

		// read XML
		Document doc = XMLAbstractFactory.readXMLInputstream(input);

		// create factories
		this.bicycleFactory = new ExtendedXMLBicycleFactory(randomSeedService, doc, trafficGraphExtensions);
		this.busFactory = new ExtendedXMLBusFactory(randomSeedService, doc, trafficGraphExtensions);
		this.truckFactory = new ExtendedXMLTruckFactory(randomSeedService, doc, trafficGraphExtensions);
		this.motorcycleFactory = new ExtendedXMLMotorcycleFactory(randomSeedService, doc, trafficGraphExtensions);
		this.carFactory = new ExtendedXMLCarFactory(randomSeedService, doc, trafficGraphExtensions);
	}

	@Override
	public Vehicle<BicycleData> createBicycle( String typeId, SensorHelper gpsSensor) {
		return this.bicycleFactory.createBicycle( typeId, gpsSensor);
	}

	@Override
	public Vehicle<BusData> createBus( String typeId, SensorHelper gpsSensor, SensorHelper infraredSensor) {
		return this.busFactory.createBus( typeId, gpsSensor, infraredSensor);
	}

	@Override
	public Vehicle<CarData> createCar( String typeId, Color color, SensorHelper gpsSensor) {
		return this.carFactory.createCar( typeId, color, gpsSensor);
	}

	@Override
	public Vehicle<MotorcycleData> createMotorcycle( String typeId, Color color, SensorHelper gpsSensor) {
		return this.motorcycleFactory.createMotorcycle( typeId, color, gpsSensor);
	}

	@Override
	public Vehicle<BicycleData> createRandomBicycle( SensorHelper gpsSensor) {
		return this.bicycleFactory.createRandomBicycle( gpsSensor);
	}

	@Override
	public Vehicle<BusData> createRandomBus( SensorHelper gpsSensor, SensorHelper infraredSensor) {
		return this.busFactory.createRandomBus( gpsSensor, infraredSensor);
	}

	@Override
	public Vehicle<CarData> createRandomCar( SensorHelper gpsSensor) {
		return this.carFactory.createRandomCar( gpsSensor);
	}

	@Override
	public Vehicle<MotorcycleData> createRandomMotorcycle( SensorHelper gpsSensor) {
		return this.motorcycleFactory.createRandomMotorcycle( gpsSensor);
	}

	@Override
	public Vehicle<TruckData> createRandomTruck( SensorHelper gpsSensor) {
		return this.truckFactory.createRandomTruck( gpsSensor);
	}

	@Override
	public Vehicle<TruckData> createTruck( String typeId, Color color, int trailercount, SensorHelper gpsSensor) {
		return this.truckFactory.createTruck( typeId, color, trailercount, gpsSensor);
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
