/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.AbstractCustomTag;
import de.pgalise.simulation.shared.city.LeisureTag;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class LeisureTagCustom extends AbstractCustomTag implements LeisureTag {
	private static final long serialVersionUID = 1L;

	public LeisureTagCustom(String value) {
		super(value);
	}
	
}
