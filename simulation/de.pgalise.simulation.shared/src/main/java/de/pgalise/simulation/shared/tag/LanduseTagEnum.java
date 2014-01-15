/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

/**
 *
 * @author richter
 */
public enum LanduseTagEnum {

	INDUSTRY("industry"), FARMLAND("farmland"), FARMYARD("farmyard"), RETAIL(
		"retail"), MILITARY("military"), RESIDENTIAL("residential"), COMMERCIAL(
			"commercial");

	private String stringValue;

	private LanduseTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getStringValue() {
		return stringValue;
	}
}
