/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.util.GTFS.service.BusService;

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
