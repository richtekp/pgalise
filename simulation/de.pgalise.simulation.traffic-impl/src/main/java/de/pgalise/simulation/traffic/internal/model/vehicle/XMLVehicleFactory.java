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

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import java.awt.Color;
import java.io.InputStream;

import org.w3c.dom.Document;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Bicycle;
import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Bus;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.Car;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import de.pgalise.simulation.traffic.model.vehicle.Motorcycle;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Truck;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;

/**
 * Factory for all vehicles in the simulation. Reads a given XML file and gives
 * the individual root nodes to the specific vehicle factories.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 24, 2012)
 */
public class XMLVehicleFactory extends AbstractVehicleFactory implements
	CarFactory, BusFactory, TruckFactory, MotorcycleFactory, BicycleFactory {
	private static final long serialVersionUID = 1L;

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
	private final InputStream xmlInputStream;

	/**
	 * Constructor
	 *
	 * @param randomSeedService Random Seed Service
	 * @param idGenerator
	 * @param xmlInputStream Input stream to the XML file
	 * @param trafficGraphExtensions
	 */
	public XMLVehicleFactory(RandomSeedService randomSeedService,  IdGenerator idGenerator,
		TrafficGraphExtensions trafficGraphExtensions,
		InputStream xmlInputStream) {
		super(
			trafficGraphExtensions,
			idGenerator,
			randomSeedService);
		if (xmlInputStream == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"input"));
		}
		this.xmlInputStream = xmlInputStream;

		// read XML
		Document doc = AbstractXMLVehicleFactory.readXMLInputstream(xmlInputStream);

		// create factories
		this.bicycleFactory = new XMLBicycleFactory(idGenerator,
			trafficGraphExtensions,
			randomSeedService,
			doc);
		this.busFactory = new XMLBusFactory(idGenerator,
			trafficGraphExtensions,
			randomSeedService,
			doc);
		this.truckFactory = new XMLTruckFactory(idGenerator,
			trafficGraphExtensions,
			randomSeedService,
			doc);
		this.motorcycleFactory = new XMLMotorcycleFactory(idGenerator,
			trafficGraphExtensions,
			randomSeedService,
			doc);
		this.carFactory = new XMLCarFactory(idGenerator,
			trafficGraphExtensions,
			randomSeedService,
			doc);
	}

	@Override
	public Bicycle createBicycle(Output output) {
		return this.bicycleFactory.createBicycle(output);
	}

	@Override
	public Bus createBus(Output output) {
		return this.busFactory.createBus(output);
	}

	@Override
	public Car createCar(Output output) {
		return this.carFactory.createCar(output);
	}

	@Override
	public Motorcycle createMotorcycle(
		Output output) {
		return this.motorcycleFactory.createMotorcycle(
			output);
	}

	@Override
	public Bicycle createRandomBicycle(Output output) {
		return this.bicycleFactory.createRandomBicycle(output);
	}

	@Override
	public Bus createRandomBus(Output output) {
		return this.busFactory.createRandomBus(output);
	}

	@Override
	public Car createRandomCar(Output output) {
		return this.carFactory.createRandomCar(output);
	}

	@Override
	public Motorcycle createRandomMotorcycle(Output output) {
		return this.motorcycleFactory.createRandomMotorcycle(output);
	}

	@Override
	public Truck createRandomTruck(Output output) {
		return this.truckFactory.createRandomTruck(output);
	}

	@Override
	public Truck createTruck(Color color,
		int trailercount,
		Output output) {
		return this.truckFactory.createTruck(color,
			trailercount,
			output);
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
		return this.xmlInputStream;
	}

}
