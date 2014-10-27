/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

/**
 *
 * @author richter
 */
public enum AmenityTagEnum {

	KINDERGARTEN("kindergarten"), PHARMACY("pharmarcy"), RESTAURANT("restaurant"), PARKING(
		"parking"), BICYKLE_PARKING("bicycle_parking"), CAR_RENTAL("car_rental");

	private String stringValue;

	private AmenityTagEnum() {
	}

	private AmenityTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getStringValue() {
		return stringValue;
	}
}
