/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensor;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.staticsensor.internal.sensor.weather.WeatherStation;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class WeatherStationDialogCtrl extends BaseEnergyWeatherSensorDialogCtrl {
	private static final long serialVersionUID = 1L;
	@EJB
	private WeatherInterferer weatherInterferer;
	private List<WeatherSensor> selectedWeatherSensors;
	@EJB
	private SimulationController simulationController;

	/**
	 * Creates a new instance of WeatherStationDialogCtrl
	 */
	public WeatherStationDialogCtrl() {
	}

	public void setSelectedWeatherSensors(
		List<WeatherSensor> selectedWeatherSensors) {
		this.selectedWeatherSensors = selectedWeatherSensors;
	}

	public List<WeatherSensor> getSelectedWeatherSensors() {
		return selectedWeatherSensors;
	}
	
	public Set<WeatherSensor> retrieveWeatherSensors() {
		return simulationController.getWeatherSensorController().getAllManagedSensors();
	}
	
	public void saveSensor() throws SensorException {
		getSensorManagerController().createSensor(
			new WeatherStation(
				getIdGenerator().getNextId(),
				getOutput(),
				getCoordinate(),
				getWeatherController(),
				weatherInterferer,
				getChosenUpdateStep(),
				selectedWeatherSensors.toArray(new WeatherSensor[selectedWeatherSensors.size()])));
	}
	
}
