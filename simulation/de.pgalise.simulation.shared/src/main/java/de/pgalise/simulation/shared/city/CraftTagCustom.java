/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.AbstractCustomTag;
import de.pgalise.simulation.shared.city.CraftTag;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class CraftTagCustom extends AbstractCustomTag implements CraftTag {
	private static final long serialVersionUID = 1L;

	public CraftTagCustom(String value) {
		super(value);
	}
	
}
