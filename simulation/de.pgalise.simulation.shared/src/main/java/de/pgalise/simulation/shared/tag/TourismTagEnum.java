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
public enum TourismTagEnum implements TourismTag {

	;

	private final String value;

	private TourismTagEnum(String value) {
		this.value = value;
	}
	
	@Override
	public String getStringValue() {
		return value;
	}
	
}
