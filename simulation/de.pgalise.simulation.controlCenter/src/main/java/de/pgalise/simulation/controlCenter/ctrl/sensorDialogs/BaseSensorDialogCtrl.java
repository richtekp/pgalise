/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.controlCenter.ctrl.BaseMapDialogCtrl;
import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.RandomSeedService;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class BaseSensorDialogCtrl extends BaseMapDialogCtrl {
	private static final long serialVersionUID = 1L;
	private int chosenUpdateStep;
	@EJB
	private SensorManagerController sensorManagerController;
	@EJB
	private TcpIpOutput output;
	@EJB
	private RandomSeedService randomSeedService;

	/**
	 * Creates a new instance of BaseSensorDialogCtrl
	 */
	public BaseSensorDialogCtrl() {
	}

	public void setChosenUpdateStep(int chosenUpdateStep) {
		this.chosenUpdateStep = chosenUpdateStep;
	}

	public int getChosenUpdateStep() {
		return chosenUpdateStep;
	}

	/**
	 * @return the sensorManagerController
	 */
	public SensorManagerController getSensorManagerController() {
		return sensorManagerController;
	}

	/**
	 * @param sensorManagerController the sensorManagerController to set
	 */
	public void setSensorManagerController(
		SensorManagerController sensorManagerController) {
		this.sensorManagerController = sensorManagerController;
	}

	/**
	 * @return the output
	 */
	public TcpIpOutput getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(
		TcpIpOutput output) {
		this.output = output;
	}

	/**
	 * @return the randomSeedService
	 */
	public RandomSeedService getRandomSeedService() {
		return randomSeedService;
	}

	/**
	 * @param randomSeedService the randomSeedService to set
	 */
	public void setRandomSeedService(
		RandomSeedService randomSeedService) {
		this.randomSeedService = randomSeedService;
	}
	
}
