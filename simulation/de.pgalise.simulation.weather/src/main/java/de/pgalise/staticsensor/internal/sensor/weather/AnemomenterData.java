/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.staticsensor.internal.sensor.weather;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class AnemomenterData extends SensorData {
	private static final long serialVersionUID = 1L;
	/**
	 * wind velocity
	 */
	double windVelocity;
	
	public AnemomenterData() {
	}

	/**
	 * Returns the wind velocity.
	 * 
	 * @return windVelocity
	 */
	public double getWindVelocity() {
		return this.windVelocity;
	}

	/**
	 * Sets the wind velocity.
	 * 
	 * @param windVelocity
	 *            wind velocity
	 */
	public void setWindVelocity(double windVelocity) {
		this.windVelocity = windVelocity;
	}
	
}
