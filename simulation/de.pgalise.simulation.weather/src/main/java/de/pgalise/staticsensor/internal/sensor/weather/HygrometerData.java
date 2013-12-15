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
public class HygrometerData extends SensorData {
	private static final long serialVersionUID = 1L;
	/**
	 * Relativ humidity
	 */
	private double relativHumidity;

	/**
	 * Returns the relativ humidity.
	 * 
	 * @return relativHumidity
	 */
	public double getRelativHumidity() {
		return this.relativHumidity;
	}

	/**
	 * Sets the relativ humidity.
	 * 
	 * @param relativHumidity
	 *            relativ humidity
	 */
	public void setRelativHumidity(double relativHumidity) {
		this.relativHumidity = relativHumidity;
	}
}