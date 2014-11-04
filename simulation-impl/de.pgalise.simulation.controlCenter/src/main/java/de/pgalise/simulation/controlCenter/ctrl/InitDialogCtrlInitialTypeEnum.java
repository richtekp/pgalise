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
public enum InitDialogCtrlInitialTypeEnum implements InitDialogCtrlInitialType {
//	CREATE, RECENT,IMPORT;
	
	create("create"), recent("recent"),impor("import");
	
	private final String stringValue;

	private InitDialogCtrlInitialTypeEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
