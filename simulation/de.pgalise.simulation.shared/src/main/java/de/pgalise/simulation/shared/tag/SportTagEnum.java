/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

import javax.persistence.Embeddable;

/**
 *
 * @author richter
 */
@Embeddable
public enum SportTagEnum implements SportTag {

	;
	
	private final String stringValue;

	private SportTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
