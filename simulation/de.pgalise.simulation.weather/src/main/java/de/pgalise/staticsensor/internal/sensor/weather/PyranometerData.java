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
public class PyranometerData extends SensorData {
	private static final long serialVersionUID = 1L;

	/**
	 * Radiation
	 */
	private double radiation;

	/**
	 * Returns the radiation.
	 * 
	 * @return radiation
	 */
	public double getRadiation() {
		return this.radiation;
	}

	/**
	 * Sets the radiation.
	 * 
	 * @param radiation
	 *            Radiation
	 */
	public void setRadiation(double radiation) {
		this.radiation = radiation;
	}
	
}
