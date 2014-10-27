/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.event;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandlerManager;

/**
 *
 * @author richter
 */
public class AbstractVehicleEventHandlerManager extends AbstractTrafficEventHandlerManager<VehicleEventHandler<VehicleEvent>, VehicleEvent>
  implements VehicleEventHandlerManager {

  public AbstractVehicleEventHandlerManager(RandomSeedService randomSeedService,
    TrafficControllerLocal trafficServerLocal,
    Output tcpIpOutput) {
    super(randomSeedService,
      trafficServerLocal,
      tcpIpOutput);
  }

}
