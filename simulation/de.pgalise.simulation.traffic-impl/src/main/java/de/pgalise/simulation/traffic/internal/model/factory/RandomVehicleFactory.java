/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
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
import de.pgalise.simulation.traffic.model.vehicle.VehicleFactory;
import java.awt.Color;
import java.util.Set;
import javax.ejb.Stateful;
import javax.ejb.Local;
import javax.ejb.EJB;

/**
 *
 * @author richter
 */
@Stateful
@Local(VehicleFactory.class)
public class RandomVehicleFactory extends AbstractVehicleFactory implements
  VehicleFactory {

  @EJB
  private BicycleFactory bicycleFactory;
  @EJB
  private BusFactory busFactory;
  @EJB
  private CarFactory carFactory;
  @EJB
  private MotorcycleFactory motorcycleFactory;
  @EJB
  private TruckFactory truckFactory;

  @Override
  public Bicycle createBicycle(Output output) {
    return bicycleFactory.createBicycle(output);
  }

  @Override
  public Bicycle createRandomBicycle(Output output) {
    return bicycleFactory.createRandomBicycle(output);
  }

  @Override
  public Bicycle createBicycle(Set<TrafficEdge> edges,
    Output output) {
    return bicycleFactory.createBicycle(edges,
      output);
  }

  @Override
  public Bicycle createRandomBicycle(Set<TrafficEdge> edges,
    Output output) {
    return bicycleFactory.createRandomBicycle(edges,
      output);
  }

  @Override
  public Bus createBus(Output output) {
    return busFactory.createBus(output);
  }

  @Override
  public Bus createRandomBus(Output output) {
    return busFactory.createRandomBus(output);
  }

  @Override
  public Car createRandomCar(Output output) {
    return carFactory.createRandomCar(output);
  }

  @Override
  public Car createCar(Output output) {
    return carFactory.createCar(output);
  }

  @Override
  public Car createCar(Set<TrafficEdge> edges,
    Output output) {
    return carFactory.createCar(edges,
      output);
  }

  @Override
  public Car createRandomCar(Set<TrafficEdge> edges,
    Output output) {
    return carFactory.createRandomCar(edges,
      output);
  }

  @Override
  public Motorcycle createMotorcycle() {
    return motorcycleFactory.createMotorcycle();
  }

  @Override
  public Motorcycle createRandomMotorcycle() {
    return motorcycleFactory.createRandomMotorcycle();
  }

  @Override
  public Truck createTruck(Color color,
    int trailercount,
    Output output
  ) {
    return truckFactory.createTruck(color,
      trailercount,
      output);
  }

  @Override
  public Truck createRandomTruck(Output output) {
    return truckFactory.createRandomTruck(output);
  }

}
