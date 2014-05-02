/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.model.factory.CompositeVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import static de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory.randInt;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleFactory;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author richter
 */
public abstract class AbstractCompositeVehicleFactory extends AbstractVehicleFactory implements CompositeVehicleFactory {

  public AbstractCompositeVehicleFactory() {
  }  
  
  public AbstractCompositeVehicleFactory(RandomSeedService randomSeedService,
    IdGenerator idGenerator,
    TrafficGraphExtensions trafficGraphExtensions) {
    super(
      trafficGraphExtensions,
      idGenerator,
      randomSeedService);
  }

  @Override
  public Vehicle createVehicle(Output output) {
    return createVehicle(null, output);
  }

  @Override
  public Vehicle createVehicle(Set<TrafficEdge> edges, Output output) {
    int index = randInt(0, getVehicleFactories().size());
    return new LinkedList<>(getVehicleFactories()).get(index).createVehicle(output);
  }
  
  protected abstract Set<VehicleFactory> getVehicleFactories();
}
