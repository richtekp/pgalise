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
public enum MapParsedStateEnum implements MapParsedState {
	IN_PROGRESS("in progress"), DONE("done");
	
	private final String stringValue;

	private MapParsedStateEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
