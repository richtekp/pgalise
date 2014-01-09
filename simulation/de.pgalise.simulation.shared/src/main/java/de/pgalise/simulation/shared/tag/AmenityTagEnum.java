/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public enum AmenityTagEnum implements AmenityTag {
	KINDERGARTEN("kindergarten"), PHARMACY("pharmarcy"), RESTAURANT("restaurant"), PARKING("parking"), BICYKLE_PARKING("bicycle_parking"), CAR_RENTAL("car_rental");
	
	private final String stringValue;

	private AmenityTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
