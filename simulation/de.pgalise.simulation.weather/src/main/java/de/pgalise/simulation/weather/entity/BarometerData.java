/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.weather.entity;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.shared.entity.Identifiable;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class BarometerData extends SensorData {
	private static final long serialVersionUID = 1L;
	/**
	 * Air pressure
	 */
	private double airPressure;

	public BarometerData() {
	}

	/**
	 * Returns the air pressure.
	 * 
	 * @return airPressure
	 */
	public double getAirPressure() {
		return this.airPressure;
	}

	/**
	 * Sets the airPressure.
	 * 
	 * @param airPressure
	 *            air pressure
	 */
	public void setAirPressure(double airPressure) {
		this.airPressure = airPressure;
	}

	
}
