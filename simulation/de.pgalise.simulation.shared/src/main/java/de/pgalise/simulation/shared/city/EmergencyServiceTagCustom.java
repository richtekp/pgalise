/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.AbstractCustomTag;
import de.pgalise.simulation.shared.city.EmergencyServiceTag;
import javax.persistence.Embeddable;

/**
 *
 * @author richter
 */
public class EmergencyServiceTagCustom  extends AbstractCustomTag implements EmergencyServiceTag {

	protected EmergencyServiceTagCustom() {
	}

	public EmergencyServiceTagCustom(String value) {
		super(value);
	}
	
}