/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;
import javax.ejb.EJB;

/**
 *
 * @author richter
 */
public class BaseEnergyWeatherSensorDialogCtrl extends BaseSensorDialogCtrl {

  private static final long serialVersionUID = 1L;
  @EJB
  private WeatherControllerLocal weatherController;
  @EJB
  private EnergyControllerLocal energyController;

  /**
   * Creates a new instance of BaseEnergyWeatherSensorDialog
   */
  public BaseEnergyWeatherSensorDialogCtrl() {
  }

  /**
   * @return the weatherController
   */
  public WeatherController getWeatherController() {
    return weatherController;
  }

  /**
   * @param weatherController the weatherController to set
   */
  public void setWeatherController(
    WeatherControllerLocal weatherController) {
    this.weatherController = weatherController;
  }

  /**
   * @return the energyController
   */
  public EnergyControllerLocal getEnergyController() {
    return energyController;
  }

  /**
   * @param energyController the energyController to set
   */
  public void setEnergyController(
    EnergyControllerLocal energyController) {
    this.energyController = energyController;
  }

}
