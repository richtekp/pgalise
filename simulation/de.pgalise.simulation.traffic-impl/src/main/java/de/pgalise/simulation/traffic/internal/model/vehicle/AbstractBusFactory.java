/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Bus;
import de.pgalise.simulation.traffic.entity.BusData;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;
import javax.ejb.EJB;

/**
 *
 * @author richter
 */
public class AbstractBusFactory extends AbstractVehicleFactory implements
  BusFactory {

  private InfraredInterferer infraredInterferer;

  private TrafficGraphExtensions trafficGraphExtensions;
  @EJB
  private IdGenerator idGenerator;

  public AbstractBusFactory() {
  }

  public AbstractBusFactory(InfraredInterferer infraredInterferer,
    TrafficGraphExtensions trafficGraphExtensions) {
    this.infraredInterferer = infraredInterferer;
    this.trafficGraphExtensions = trafficGraphExtensions;
  }

  @Override
  public Bus createBus(Output output) {
    return createRandomBus(output);
  }

  @Override
  public Bus createRandomBus(Output output) {
    GpsSensor gpsSensor = new GpsSensor(getIdGenerator().getNextId(),
      output,
      null,
      retrieveGpsInterferer());
    InfraredSensor infraredSensor = new InfraredSensor(getIdGenerator().
      getNextId(),
      output,
      null,
      null,
      infraredInterferer);
    BusData busData = new BusData(idGenerator.getNextId(),
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      null,
      9,
      10,
      11,
      gpsSensor,
      infraredSensor);
    return new DefaultBus(getIdGenerator().getNextId(),
      "random bus",
      busData,
      trafficGraphExtensions);
  }

}
