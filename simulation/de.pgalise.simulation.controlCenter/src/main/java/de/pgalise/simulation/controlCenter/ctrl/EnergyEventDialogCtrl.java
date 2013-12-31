/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@ViewScoped
public class EnergyEventDialogCtrl {
	private Date chosenStartDate;
	private int chosenDuration;
	private float chosenRatio;
	private float chosenValue;

	public EnergyEventDialogCtrl() {
	}

	public void setChosenStartDate(Date chosenStartDate) {
		this.chosenStartDate = chosenStartDate;
	}

	public Date getChosenStartDate() {
		return chosenStartDate;
	}

	public void setChosenDuration(int chosenDuration) {
		this.chosenDuration = chosenDuration;
	}

	public int getChosenDuration() {
		return chosenDuration;
	}

	public void setChosenRatio(float chosenRatio) {
		this.chosenRatio = chosenRatio;
	}

	public float getChosenRatio() {
		return chosenRatio;
	}

	public void setChosenValue(float chosenValue) {
		this.chosenValue = chosenValue;
	}

	public float getChosenValue() {
		return chosenValue;
	}
}
