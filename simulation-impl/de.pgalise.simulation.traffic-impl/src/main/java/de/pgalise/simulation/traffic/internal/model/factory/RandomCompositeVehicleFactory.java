/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.model.factory.CompositeVehicleFactory;
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
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleFactory;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
@Local(CompositeVehicleFactory.class)
public class RandomCompositeVehicleFactory extends AbstractCompositeVehicleFactory implements
  CompositeVehicleFactory {

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
  private final Set<VehicleFactory> FACTORIES = 
          new HashSet<VehicleFactory>(Arrays.asList(bicycleFactory, busFactory, carFactory, motorcycleFactory, truckFactory));

  public RandomCompositeVehicleFactory() {
  }

  @Override
  public Bicycle createBicycle(Output output) {
    return bicycleFactory.createVehicle(output);
  }

  @Override
  public Bicycle createRandomBicycle(Output output) {
    return bicycleFactory.createVehicle(output);
  }

  @Override
  public Bicycle createBicycle(Set<TrafficEdge> edges,
    Output output) {
    return bicycleFactory.createVehicle(edges,
      output);
  }

  @Override
  public Bicycle createRandomBicycle(Set<TrafficEdge> edges,
    Output output) {
    return bicycleFactory.createVehicle(edges,
      output);
  }

  @Override
  public Bus createBus(Output output) {
    return busFactory.createVehicle(output);
  }

  @Override
  public Bus createRandomBus(Output output) {
    return busFactory.createVehicle(output);
  }

  @Override
  public Car createRandomCar(Output output) {
    return carFactory.createVehicle(output);
  }

  @Override
  public Car createCar(Output output) {
    return carFactory.createVehicle(output);
  }

  @Override
  public Car createCar(Set<TrafficEdge> edges,
    Output output) {
    return carFactory.createVehicle(edges,
      output);
  }

  @Override
  public Car createRandomCar(Set<TrafficEdge> edges,
    Output output) {
    return carFactory.createVehicle(edges,
      output);
  }

  @Override
  public Motorcycle createMotorcycle(Set<TrafficEdge> edges, Output output) {
    return motorcycleFactory.createVehicle(edges, output);
  }

  @Override
  public Motorcycle createRandomMotorcycle(Set<TrafficEdge> edges, Output output) {
    return motorcycleFactory.createVehicle(edges, output);
  }

  @Override
  public Motorcycle createMotorcycle(Output output) {
    return motorcycleFactory.createVehicle( output);
  }

  @Override
  public Motorcycle createRandomMotorcycle(Output output) {
    return motorcycleFactory.createVehicle(output);
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
    return truckFactory.createVehicle(output);
  }

  @Override
  protected Set<VehicleFactory> getVehicleFactories() {
    return FACTORIES;
  }

}
