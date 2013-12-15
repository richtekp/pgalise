/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.staticsensor.internal.sensor.weather;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class ThermometerData extends SensorData {
	private static final long serialVersionUID = 1L;

	/**
	 * Temperature
	 */
	private double temperature;

	/**
	 * Returns the temperature.
	 * 
	 * @return temperature
	 */
	public double getTemperature() {
		return this.temperature;
	}

	/**
	 * Sets the temperature.
	 * 
	 * @param temperature
	 *            Temperature
	 */
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	
}
