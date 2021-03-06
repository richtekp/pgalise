/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.controlCenter.ctrl.MainCtrlUtils;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import java.util.concurrent.ExecutionException;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class TopoRadarDialogCtrl extends BaseSensorDialogCtrl {

  private static final long serialVersionUID = 1L;
  @EJB
  private TrafficGraph trafficGraph;

  /**
   * Creates a new instance of TopoRadarDialogCtrl
   */
  public TopoRadarDialogCtrl() {
  }

  public void saveSensor() throws SensorException, InterruptedException, ExecutionException {
    TrafficNode node = trafficGraph.getNodeClosestTo(getCoordinate());
    getSensorManagerController().createSensor(new TopoRadarSensor(
      getIdGenerator().
      getNextId(),
      MainCtrlUtils.OUTPUT,
      node,
      null));
  }

}
