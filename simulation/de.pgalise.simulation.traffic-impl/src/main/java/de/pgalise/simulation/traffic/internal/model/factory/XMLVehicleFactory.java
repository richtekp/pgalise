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
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.service.IdGenerator;
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
import de.pgalise.simulation.traffic.model.vehicle.xml.VehicleDataList;
import java.awt.Color;
import java.io.InputStream;
import java.util.HashSet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
   * Constructor
   *
   * @param randomSeedService Random Seed Service
   * @param idGenerator
   * @param xmlInputStream Input stream to the XML file
   * @param trafficGraphExtensions
   */
  public XMLVehicleFactory(RandomSeedService randomSeedService,
    IdGenerator idGenerator,
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

    VehicleDataList vehicleDataList = readVehicleData(xmlInputStream);

    // create factories
    this.bicycleFactory = new XMLBicycleFactory(idGenerator,
      trafficGraphExtensions,
      randomSeedService,
      new HashSet<>(vehicleDataList.getBicycleData().getList()));
    this.busFactory = new XMLBusFactory(idGenerator,
      trafficGraphExtensions,
      randomSeedService,
      new HashSet<>(vehicleDataList.getBusData().getList()));
    this.truckFactory = new XMLTruckFactory(idGenerator,
      trafficGraphExtensions,
      randomSeedService,
      new HashSet<>(vehicleDataList.getTruckData().getList()));
    this.motorcycleFactory = new XMLMotorcycleFactory(idGenerator,
      trafficGraphExtensions,
      randomSeedService,
      new HashSet<>(vehicleDataList.getMotorcycleData().getList()));
    this.carFactory = new XMLCarFactory(idGenerator,
      trafficGraphExtensions,
      randomSeedService,
      new HashSet<>(vehicleDataList.getCarData().getList()));
  }

  @Override
  public Bicycle createBicycle() {
    return this.bicycleFactory.createBicycle();
  }

  @Override
  public Bus createBus() {
    return this.busFactory.createBus();
  }

  @Override
  public Car createCar() {
    return this.carFactory.createCar();
  }

  @Override
  public Motorcycle createMotorcycle() {
    return this.motorcycleFactory.createMotorcycle();
  }

  @Override
  public Bicycle createRandomBicycle() {
    return this.bicycleFactory.createRandomBicycle();
  }

  @Override
  public Bus createRandomBus() {
    return this.busFactory.createRandomBus();
  }

  @Override
  public Car createRandomCar() {
    return this.carFactory.createRandomCar();
  }

  @Override
  public Motorcycle createRandomMotorcycle() {
    return this.motorcycleFactory.createRandomMotorcycle();
  }

  @Override
  public Truck createRandomTruck() {
    return this.truckFactory.createRandomTruck();
  }

  @Override
  public Truck createTruck(Color color,
    int trailercount
  ) {
    return this.truckFactory.createTruck(color,
      trailercount);
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

  private VehicleDataList readVehicleData(InputStream doc) {

    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(VehicleDataList.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      VehicleDataList retValue = (VehicleDataList) jaxbUnmarshaller.
        unmarshal(doc);
      return retValue;
    } catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
  }

}
