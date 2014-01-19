/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

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
  public Bicycle createBicycle() {
    return bicycleFactory.createBicycle();
  }

  @Override
  public Bicycle createRandomBicycle() {
    return bicycleFactory.createRandomBicycle();
  }

  @Override
  public Bicycle createBicycle(Set<TrafficEdge> edges) {
    return bicycleFactory.createBicycle(edges);
  }

  @Override
  public Bicycle createRandomBicycle(Set<TrafficEdge> edges) {
    return bicycleFactory.createRandomBicycle(edges);
  }

  @Override
  public Bus createBus() {
    return busFactory.createBus();
  }

  @Override
  public Bus createRandomBus() {
    return busFactory.createRandomBus();
  }

  @Override
  public Car createRandomCar() {
    return carFactory.createRandomCar();
  }

  @Override
  public Car createCar() {
    return carFactory.createCar();
  }

  @Override
  public Car createCar(Set<TrafficEdge> edges) {
    return carFactory.createCar(edges);
  }

  @Override
  public Car createRandomCar(Set<TrafficEdge> edges) {
    return carFactory.createRandomCar(edges);
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
    int trailercount) {
    return truckFactory.createTruck(color,
      trailercount);
  }

  @Override
  public Truck createRandomTruck() {
    return truckFactory.createRandomTruck();
  }

}
