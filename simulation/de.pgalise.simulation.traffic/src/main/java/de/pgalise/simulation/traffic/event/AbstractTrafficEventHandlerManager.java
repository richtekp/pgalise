/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.event;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.internal.event.AbstractEventHandlerManager;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author richter
 * @param <H>
 * @param <E>
 */
public abstract class AbstractTrafficEventHandlerManager<H extends TrafficEventHandler<E>, E extends TrafficEvent<?>>
  extends AbstractEventHandlerManager<H, E> {

  private RandomSeedService randomSeedService;
  private TrafficControllerLocal trafficServerLocal;
  private Output tcpIpOutput;

  public AbstractTrafficEventHandlerManager(RandomSeedService randomSeedService,
    TrafficControllerLocal trafficServerLocal,
    Output tcpIpOutput) {
    this.randomSeedService = randomSeedService;
    this.trafficServerLocal = trafficServerLocal;
    this.tcpIpOutput = tcpIpOutput;
  }

  @Override
  public <J extends H> void init(InputStream config,
    ClassLoader clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    super.init(config,
      clazz);
    for (TrafficEventHandler<E> handler : getHandlers()) {
      handler.init(randomSeedService,
        trafficServerLocal,
        tcpIpOutput);
    }
  }

  @Override
  public boolean responsibleFor(H handler,
    E event) {
    return handler.getResponsibleServer().equals(this.trafficServerLocal);
  }

}
