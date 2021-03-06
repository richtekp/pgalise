package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.traffic.service.BusService;

/**
 *
 * @author richter
 */
public class CreateBussesEventHandlerInitParameter extends InitParameter {

  private static final long serialVersionUID = 1L;

  private BusService busService;

  public CreateBussesEventHandlerInitParameter(BusService busService,
    Output output) {
    super(output);
    this.busService = busService;
  }

  public void setBusService(BusService busService) {
    this.busService = busService;
  }

  public BusService getBusService() {
    return busService;
  }
}
