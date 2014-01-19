/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Motorcycle;
import de.pgalise.simulation.traffic.entity.MotorcycleData;

/**
 *
 * @author richter
 */
public class DefaultMotorcycle extends ExtendedMotorizedVehicle<MotorcycleData>
  implements Motorcycle {

  private static final long serialVersionUID = 1L;

  protected DefaultMotorcycle() {
  }

  public DefaultMotorcycle(Long id,
    MotorcycleData data,
    TrafficGraphExtensions trafficGraphExtensions) {
    super(id,
      data,
      trafficGraphExtensions);
  }

}
