/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

/**
 *
 * @author richter
 */
public enum LanduseTagEnum implements LanduseTag {
	INDUSTRY("industry"), FARMLAND("farmland"), FARMYARD("farmyard"), RETAIL("retail"), MILITARY("military"), RESIDENTIAL("residential"), COMMERCIAL("commercial");
	
	private String stringValue;

	private LanduseTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}
	
	@Override
	public String getStringValue() {
		return stringValue;
	}
}
