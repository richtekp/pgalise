/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class WindPowerSensorDialogCtrl extends BaseSensorDialogCtrl {
	private int rotorLength;
	private float activityValue;

	/**
	 * Creates a new instance of WindPowerSensorDialogCtrl
	 */
	public WindPowerSensorDialogCtrl() {
	}

	public void setActivityValue(float activityValue) {
		this.activityValue = activityValue;
	}

	public float getActivityValue() {
		return activityValue;
	}

	public void setRotorLength(int rotorLength) {
		this.rotorLength = rotorLength;
	}

	public int getRotorLength() {
		return rotorLength;
	}
	
}
