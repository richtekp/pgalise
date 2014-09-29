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
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.InductionLoopInterferer;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@ViewScoped
public class IndutionLoopDialogCtrl extends BaseSensorDialogCtrl {

  private static final long serialVersionUID = 1L;
  @EJB
  private TrafficGraph trafficGraph;
  @EJB
  private InductionLoopInterferer inductionLoopInterferer;

  public IndutionLoopDialogCtrl() {
  }

  public void saveSensor() throws SensorException {
    TrafficNode node = trafficGraph.getNodeClosestTo(getCoordinate());
    getSensorManagerController().createSensor(new InductionLoopSensor(
      getIdGenerator().getNextId(),
      MainCtrlUtils.OUTPUT,
      node,
      inductionLoopInterferer));
  }
}
