/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.AbstractCustomTag;
import de.pgalise.simulation.shared.city.AmenityTag;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class AmenityTagCustom extends AbstractCustomTag implements AmenityTag {
	private static final long serialVersionUID = 1L;

	public AmenityTagCustom(String value) {
		super(value);
	}
	
}
