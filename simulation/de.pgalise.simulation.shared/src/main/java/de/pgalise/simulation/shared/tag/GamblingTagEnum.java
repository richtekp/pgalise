/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

/**
 *
 * @author richter
 */
public enum GamblingTagEnum implements GamblingTag {
	;
	
	private final String stringValue;

	private GamblingTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
