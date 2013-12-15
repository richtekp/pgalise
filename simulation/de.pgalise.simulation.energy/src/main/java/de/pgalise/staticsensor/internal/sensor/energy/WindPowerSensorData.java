/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.staticsensor.internal.sensor.energy;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class WindPowerSensorData extends SensorData  {
	private static final long serialVersionUID = 1L;

	/**
	 * Density of the air (kg/m^3)
	 */
	private double airDensity;

	/**
	 * Returns the air density.
	 * 
	 * @return airDensity
	 */
	public double getAirDensity() {
		return this.airDensity;
	}

	/**
	 * Sets the air density.
	 * 
	 * @param airDensity
	 *            air density
	 */
	public void setAirDensity(double airDensity) {
		this.airDensity = airDensity;
	}
}
