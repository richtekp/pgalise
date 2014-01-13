/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
@Embeddable
public enum AmenityTagEnum implements AmenityBaseTag {

	KINDERGARTEN("kindergarten"), PHARMACY("pharmarcy"), RESTAURANT("restaurant"), PARKING(
		"parking"), BICYKLE_PARKING("bicycle_parking"), CAR_RENTAL("car_rental");

	private String stringValue;

	private AmenityTagEnum() {
	}

	private AmenityTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
