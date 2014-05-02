/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.model.vehicle.BaseVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Bicycle;
import de.pgalise.simulation.traffic.model.vehicle.Bus;
import de.pgalise.simulation.traffic.model.vehicle.Car;
import de.pgalise.simulation.traffic.model.vehicle.Motorcycle;
import de.pgalise.simulation.traffic.model.vehicle.Truck;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleFactory;
import java.awt.Color;
import java.util.Set;

/**
 *
 * @author richter
 */
/*
implementation notes:
- this interface is necessary because CompositeVehicleFactory 
can't extend all factory interface (because multiple inheritances 
of generic argument is not allowed)
- implements VehicleFactory in order to provide method to 
create a random vehicle (remove if this causes trouble)
*/
public interface CompositeVehicleFactory extends BaseVehicleFactory, VehicleFactory<Vehicle> {
  
  public Bicycle createBicycle(Output output) ;

  public Bicycle createRandomBicycle(Output output) ;

  public Bicycle createBicycle(Set<TrafficEdge> edges,
    Output output);

  public Bicycle createRandomBicycle(Set<TrafficEdge> edges,
    Output output) ;

  public Bus createBus(Output output);

  public Bus createRandomBus(Output output) ;

  public Car createRandomCar(Output output) ;

  public Car createCar(Output output) ;

  public Car createCar(Set<TrafficEdge> edges,
    Output output) ;

  public Car createRandomCar(Set<TrafficEdge> edges,
    Output output);

  public Motorcycle createMotorcycle(Set<TrafficEdge> edges, Output output) ;

  public Motorcycle createMotorcycle(Output output) ;
  
  public Motorcycle createRandomMotorcycle(Set<TrafficEdge> edges, Output output) ;

  public Motorcycle createRandomMotorcycle(Output output) ;
  
  public Truck createTruck(Color color,
    int trailercount,
    Output output
  );

  public Truck createRandomTruck(Output output);
}
