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
public class WindFlagSensorData extends SensorData {
	private static final long serialVersionUID = 1L;

	/**
	 * Wind direction
	 */
	private double windDirection;

	/**
	 * Returns the wind direction.
	 * 
	 * @return windDirection
	 */
	public double getWindDirection() {
		return this.windDirection;
	}

	/**
	 * Sets the wind direction.
	 * 
	 * @param windDirection
	 *            wind direction
	 */
	public void setWindDirection(double windDirection) {
		this.windDirection = windDirection;
	}
	
}
