/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.model.vehicle.Bus;
import de.pgalise.simulation.traffic.entity.BusData;

/**
 *
 * @author richter
 */
public class DefaultBus extends ExtendedMotorizedVehicle<BusData> implements Bus {

  private static final long serialVersionUID = 1L;
  private InfraredSensor infraredSensor;

  protected DefaultBus() {
  }

  public DefaultBus(Long id,
    BusData data,
    TrafficGraphExtensions trafficGraphExtensions) {
    super(id,
      data,
      trafficGraphExtensions);
  }

  public void setInfraredSensor(InfraredSensor infraredSensor) {
    this.infraredSensor = infraredSensor;
  }

  @Override
  public InfraredSensor getInfraredSensor() {
    return infraredSensor;
  }

}
