/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

/**
 *
 * @author richter
 */
public enum EmergencyServiceTagEnum implements EmergencyServiceTag {
	;
	
	private final String stringValue;

	private EmergencyServiceTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
