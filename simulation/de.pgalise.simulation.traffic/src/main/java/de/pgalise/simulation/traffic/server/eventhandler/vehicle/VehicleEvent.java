/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler.vehicle;

import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import java.util.Map;

/**
 * An event concerning one vehicle (general traffic event are described using
 * {@link TrafficEvent}
 *
 * @author richter
 */
public interface VehicleEvent extends TrafficEvent<VehicleEvent> {

  Vehicle<?> getVehicle();

  /**
   * @return shallow copy of currently driving vehicles
   */
  Scheduler getDrivingVehicles();

  TrafficGraph getGraph();

  TrafficGraphExtensions getTrafficGraphExtensions();

  Map<Long, VehicleEvent> getEventForVehicleMap();

}
