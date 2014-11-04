/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl.eventDialogs;

import de.pgalise.simulation.controlCenter.ctrl.BaseMapDialogCtrl;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.event.energy.PercentageChangeEnergyEvent;
import de.pgalise.simulation.weather.service.WeatherController;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@ViewScoped
public class EnergyEventDialogCtrl extends BaseMapDialogCtrl {
	private Date chosenStartDate;
	private int chosenDuration;
	private float chosenRatio;
	private float chosenRadius;
	@EJB
	private EnergyControllerLocal energyController;
	@EJB
	private IdGenerator idGenerator;

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

	public void setChosenRadius(float chosenRadius) {
		this.chosenRadius = chosenRadius;
	}

	public float getChosenRadius() {
		return chosenRadius;
	}
	
	public void saveEnergyEvent() {
		energyController.update(new EventList(idGenerator.getNextId(),
			new LinkedList<>(Arrays.asList(new PercentageChangeEnergyEvent(new BaseCoordinate( getCoordinate()),
						chosenDuration,
						chosenDuration,
						chosenDuration,
						chosenRatio))),
			chosenDuration));
	}
}
