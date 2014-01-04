/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.AbstractCustomTag;
import de.pgalise.simulation.shared.city.EmergencyServiceTag;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class EmergencyServiceTagCustom  extends AbstractCustomTag implements EmergencyServiceTag {
	private static final long serialVersionUID = 1L;

	protected EmergencyServiceTagCustom() {
	}

	public EmergencyServiceTagCustom(String value) {
		super(value);
	}
	
}
