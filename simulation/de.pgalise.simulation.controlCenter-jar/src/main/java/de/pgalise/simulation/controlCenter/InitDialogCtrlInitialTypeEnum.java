/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author richter
 */
@ManagedBean
@ApplicationScoped
public enum InitDialogCtrlInitialTypeEnum implements InitDialogCtrlInitialType {
	CREATE("create"), RECENT("recent"),IMPORT("import");
	
	private final String stringValue;

	private InitDialogCtrlInitialTypeEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
