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
public enum LanduseTagEnum implements LanduseTag {

	INDUSTRY("industry"), FARMLAND("farmland"), FARMYARD("farmyard"), RETAIL(
		"retail"), MILITARY("military"), RESIDENTIAL("residential"), COMMERCIAL(
			"commercial");

	private String stringValue;

	private LanduseTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
