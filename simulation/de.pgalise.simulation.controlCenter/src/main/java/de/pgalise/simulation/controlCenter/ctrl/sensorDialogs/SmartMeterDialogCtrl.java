/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.staticsensor.internal.sensor.energy.SmartMeterSensor;
import java.util.concurrent.ExecutionException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class SmartMeterDialogCtrl extends BaseEnergyWeatherSensorDialogCtrl {
	private static final long serialVersionUID = 1L;
	private int measureRadius;

	/**
	 * Creates a new instance of SmartMeterDialogCtrl
	 */
	public SmartMeterDialogCtrl() {
	}

	public void setMeasureRadius(int measureRadius) {
		this.measureRadius = measureRadius;
	}

	public int getMeasureRadius() {
		return measureRadius;
	}

	public void saveSensor() throws SensorException, InterruptedException, ExecutionException {
		getSensorManagerController().createSensor(new SmartMeterSensor(getIdGenerator().
			getNextId(),
			getOutput(),
			getCoordinate(),
			getWeatherController(),
			getEnergyController(),
			getRandomSeedService(),
			measureRadius,
			null));
	}
	
}
