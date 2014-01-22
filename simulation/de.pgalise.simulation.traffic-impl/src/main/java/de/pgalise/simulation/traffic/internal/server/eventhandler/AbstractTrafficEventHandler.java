/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
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
  private TrafficServerLocal<E> responsibleServer;

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
    TrafficServerLocal<E> responsibleServer,
    Output output) {
    this.randomSeedService = randomSeedService;
    this.responsibleServer = responsibleServer;
    this.output = output;
  }

  @Override
  public void init(RandomSeedService randomSeedService,
    TrafficServerLocal responsibleServer,
    Output output) {
    this.randomSeedService = randomSeedService;
    this.responsibleServer = responsibleServer;
    this.output = output;
  }

  public RandomSeedService getRandomSeedService() {
    return randomSeedService;
  }

  @Override
  public TrafficServerLocal<E> getResponsibleServer() {
    return responsibleServer;
  }

  public void setResponsibleServer(TrafficServerLocal<E> responsibleServer) {
    this.responsibleServer = responsibleServer;
  }

}
