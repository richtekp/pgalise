/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.service;

import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensor;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorController;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
public class DefaultWeatherSensorController extends AbstractController<WeatherEvent, StartParameter, InitParameter>
  implements WeatherSensorController {

  private final Set<WeatherSensor<?>> sensors = new HashSet<>();

  public DefaultWeatherSensorController() {
  }

  @Override
  protected void onInit(InitParameter param) throws InitializationException {
    //nothing to do
  }

  /**
   * clears the collection of managed sensors
   */
  @Override
  protected void onReset() {
    sensors.clear();
  }

  @Override
  protected void onStart(StartParameter param) {
    //nothing to do
  }

  @Override
  protected void onStop() {
    //nothing to do
  }

  @Override
  protected void onResume() {
    //nothing to do
  }

  @Override
  protected void onUpdate(EventList<WeatherEvent> simulationEventList) {
    for (WeatherSensor<?> sensor : sensors) {
      sensor.update(simulationEventList);
    }
  }

  @Override
  public void createSensor(WeatherSensor<?> sensor) {
    this.sensors.add(sensor);
  }

  @Override
  public void createSensors(
    Set<WeatherSensor<?>> sensors) {
    for (WeatherSensor<?> sensor : sensors) {
      this.createSensor(sensor);
    }
  }

  @Override
  public void deleteSensor(WeatherSensor<?> sensor) {
    this.sensors.remove(sensor);
  }

  @Override
  public void deleteSensors(
    Set<WeatherSensor<?>> sensors) {
    for (WeatherSensor<?> sensor : sensors) {
      this.deleteSensor(sensor);
    }
  }

  @Override
  public boolean isActivated(WeatherSensor<?> sensor) {
    return sensor.isActivated();
  }

  @Override
  public Set<WeatherSensor<?>> getAllManagedSensors() {
    return sensors;
  }

}
