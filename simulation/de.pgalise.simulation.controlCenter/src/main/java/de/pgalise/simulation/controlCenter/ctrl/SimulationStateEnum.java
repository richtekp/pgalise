/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

/**
 *
 * @author richter
 */
public enum SimulationStateEnum implements SimulationState {
	RUNNING("running"), PAUSED("paused"), STOPPED("stopped");
	
	private final String stringValue;

	private SimulationStateEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
