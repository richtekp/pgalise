/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.city;

/**
 *
 * @author richter
 */
public enum PublicTransportTagEnum implements PublicTransportTag {
	STOP_POSITION("stop_position");
	
	private final String stringValue;

	private PublicTransportTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
