/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

/**
 *
 * @author richter
 */
public enum LeisureTagEnum implements LeisureTag {
	;
	
	private final String stringValue;

	private LeisureTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
