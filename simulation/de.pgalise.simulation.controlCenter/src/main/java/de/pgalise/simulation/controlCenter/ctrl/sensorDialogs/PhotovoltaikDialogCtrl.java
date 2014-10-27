/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.controlCenter.ctrl.MainCtrlUtils;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.staticsensor.internal.sensor.energy.PhotovoltaikSensor;
import java.util.concurrent.ExecutionException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@ViewScoped
public class PhotovoltaikDialogCtrl extends BaseEnergyWeatherSensorDialogCtrl {

  private static final long serialVersionUID = 1L;

  private int chosenArea;
  private int randomSensorCount = 100;

  public PhotovoltaikDialogCtrl() {
  }

  public void setChosenArea(int chosenArea) {
    this.chosenArea = chosenArea;
  }

  public int getChosenArea() {
    return chosenArea;
  }

  public void saveSensor() throws SensorException, InterruptedException, ExecutionException {
    getSensorManagerController().createSensor(new PhotovoltaikSensor(
      getIdGenerator().
      getNextId(),
      MainCtrlUtils.OUTPUT,
      getCoordinate(),
      getWeatherController(),
      getEnergyController(),
      getRandomSeedService(),
      chosenArea,
      null));
  }

  public void generateRandomSensors() {

  }
}
