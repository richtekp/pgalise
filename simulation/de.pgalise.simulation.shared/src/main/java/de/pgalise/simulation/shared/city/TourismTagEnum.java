/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

/**
 *
 * @author richter
 */
public enum TourismTagEnum implements TourismTag {

	;

	private TourismTagEnum(String value) {
		this.value = value;
	}

	private String value;
	
	@Override
	public String getStringValue() {
		return value;
	}
	
}
