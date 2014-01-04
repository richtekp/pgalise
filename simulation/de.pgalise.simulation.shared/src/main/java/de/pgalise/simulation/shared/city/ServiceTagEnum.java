/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public enum ServiceTagEnum implements ServiceTag {

	;
	
	private final String stringValue;

	private ServiceTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
	
}
