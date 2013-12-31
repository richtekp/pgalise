/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.staticsensor.internal.sensor.energy.PhotovoltaikSensor;
import java.util.concurrent.ExecutionException;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@ViewScoped
public class PhotovoltaikDialogCtrl extends BaseEnergyWeatherSensorDialogCtrl {

	private int chosenArea;

	public PhotovoltaikDialogCtrl() {
	}

	public void setChosenArea(int chosenArea) {
		this.chosenArea = chosenArea;
	}

	public int getChosenArea() {
		return chosenArea;
	}

	public void saveSensor() throws SensorException, InterruptedException, ExecutionException {
		getSensorManagerController().createSensor(new PhotovoltaikSensor(getIdGenerator().
			getNextId(),
			getOutput(),
			getCoordinate(),
			getWeatherController(),
			getEnergyController(),
			getRandomSeedService(),
			chosenArea,
			null));
	}
}
