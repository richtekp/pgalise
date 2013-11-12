/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

/**
 *
 * @author richter
 */
public enum ServiceTagEnum implements ServiceTag {

	;

	private ServiceTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}
	
	private String stringValue;

	@Override
	public String getStringValue() {
		return stringValue;
	}
	
}
