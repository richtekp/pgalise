/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.staticsensor.sensor.weather;

import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;

/**
 *
 * @author richter
 */
public interface WeatherSensorController extends
	SensorManagerController<WeatherEvent, StartParameter, InitParameter, WeatherSensor<?>> {

}
