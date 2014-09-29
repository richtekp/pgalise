/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;

/**
 *
 * @param <D>
 * @author richter
 */
public abstract class AbstractTrafficEventHandler<D extends VehicleData, E extends TrafficEvent>
  implements TrafficEventHandler<E> {

  private RandomSeedService randomSeedService;

  /**
   * Traffic server
   */
  private TrafficControllerLocal<E> responsibleServer;

  /**
   * is set via {@link #init(de.pgalise.simulation.service.InitParameter) }
   */
  private Output output;

  public Output getOutput() {
    return output;
  }

  public void init(InitParameter initParameter) {
    this.output = initParameter.getOutput();
  }

  public AbstractTrafficEventHandler() {
  }

  public AbstractTrafficEventHandler(RandomSeedService randomSeedService,
    TrafficControllerLocal<E> responsibleServer,
    Output output) {
    this.randomSeedService = randomSeedService;
    this.responsibleServer = responsibleServer;
    this.output = output;
  }

  @Override
  public void init(RandomSeedService randomSeedService,
    TrafficControllerLocal responsibleServer,
    Output output) {
    this.randomSeedService = randomSeedService;
    this.responsibleServer = responsibleServer;
    this.output = output;
  }

  public RandomSeedService getRandomSeedService() {
    return randomSeedService;
  }

  @Override
  public TrafficControllerLocal<E> getResponsibleServer() {
    return responsibleServer;
  }

  public void setResponsibleServer(TrafficControllerLocal<E> responsibleServer) {
    this.responsibleServer = responsibleServer;
  }

}
