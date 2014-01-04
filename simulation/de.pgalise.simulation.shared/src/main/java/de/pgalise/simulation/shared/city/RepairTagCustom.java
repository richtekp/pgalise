/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.AbstractCustomTag;
import de.pgalise.simulation.shared.city.RepairTag;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class RepairTagCustom extends AbstractCustomTag implements RepairTag {
	private static final long serialVersionUID = 1L;

	public RepairTagCustom(String value) {
		super(value);
	}
	
}
