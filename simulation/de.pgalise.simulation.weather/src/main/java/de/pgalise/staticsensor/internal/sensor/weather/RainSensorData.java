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
public class RainSensorData extends SensorData {
	private static final long serialVersionUID = 1L;

	/**
	 * Precipitation amount
	 */
	private double precipitationAmount;

	/**
	 * Returns the precipitation amount.
	 * 
	 * @return precipitationAmount
	 */
	public double getPrecipitationAmount() {
		return this.precipitationAmount;
	}

	/**
	 * Sets the precipitation amount.
	 * 
	 * @param precipitationAmount
	 *            precipitation amount
	 */
	public void setPrecipitationAmount(double precipitationAmount) {
		this.precipitationAmount = precipitationAmount;
	}
	
}
