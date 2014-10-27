/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.controlCenter.ctrl.MainCtrlUtils;
import de.pgalise.simulation.energy.sensor.EnergyInterferer;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.staticsensor.internal.sensor.energy.WindPowerSensor;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class WindPowerSensorDialogCtrl extends BaseEnergyWeatherSensorDialogCtrl {

  private static final long serialVersionUID = 1L;
  private int rotorLength;
  private float activityValue;
  @EJB
  private EnergyInterferer energyInterferer;

  /**
   * Creates a new instance of WindPowerSensorDialogCtrl
   */
  public WindPowerSensorDialogCtrl() {
  }

  public void setActivityValue(float activityValue) {
    this.activityValue = activityValue;
  }

  public float getActivityValue() {
    return activityValue;
  }

  public void setRotorLength(int rotorLength) {
    this.rotorLength = rotorLength;
  }

  public int getRotorLength() {
    return rotorLength;
  }

  public void saveSensor() throws SensorException {
    getSensorManagerController().createSensor(new WindPowerSensor(
      getIdGenerator().getNextId(),
      MainCtrlUtils.OUTPUT,
      getCoordinate(),
      getWeatherController(),
      getEnergyController(),
      getRandomSeedService(),
      rotorLength,
      activityValue,
      energyInterferer));
  }

}
