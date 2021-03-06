/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.energy.entity;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class PhotovoltaikSensorData extends SensorData {

	private static final long serialVersionUID = 1L;

	public PhotovoltaikSensorData() {
	}

	/**
	 * Light intensity
	 */
	private double lightIntensity;

	/**
	 * Returns the used light intensity.
	 *
	 * @return lightIntensity
	 */
	public double getLightIntensity() {
		return this.lightIntensity;
	}

	/**
	 * Sets the light intensity.
	 *
	 * @param lightIntensity light intensity
	 */
	public void setLightIntensity(double lightIntensity) {
		this.lightIntensity = lightIntensity;
	}
}
