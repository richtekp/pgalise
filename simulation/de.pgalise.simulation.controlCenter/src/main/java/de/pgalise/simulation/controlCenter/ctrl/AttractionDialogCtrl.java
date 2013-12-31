/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@ViewScoped
public class AttractionDialogCtrl implements Serializable {
	private double chosenLatitude;
	private double chosenLongitude;
	private Date chosenStartTimestamp;
	private int chosenDurationMinutes;
	private RandomVehiclesCtrl randomVehiclesCtrl;

	public AttractionDialogCtrl() {
	}

	public void setChosenLongitude(double chosenLongitude) {
		this.chosenLongitude = chosenLongitude;
	}

	public void setChosenLatitude(double chosenLatitude) {
		this.chosenLatitude = chosenLatitude;
	}

	public double getChosenLongitude() {
		return chosenLongitude;
	}

	public double getChosenLatitude() {
		return chosenLatitude;
	}

	public void setChosenStartTimestamp(Date chosenStartTimestamp) {
		this.chosenStartTimestamp = chosenStartTimestamp;
	}

	public Date getChosenStartTimestamp() {
		return chosenStartTimestamp;
	}

	public void setChosenDurationMinutes(int chosenDurationMinutes) {
		this.chosenDurationMinutes = chosenDurationMinutes;
	}

	public int getChosenDurationMinutes() {
		return chosenDurationMinutes;
	}

	public void setRandomVehiclesCtrl(RandomVehiclesCtrl randomVehiclesCtrl) {
		this.randomVehiclesCtrl = randomVehiclesCtrl;
	}

	public RandomVehiclesCtrl getRandomVehiclesCtrl() {
		return randomVehiclesCtrl;
	}
}
