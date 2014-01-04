/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.AbstractCustomTag;
import de.pgalise.simulation.shared.city.AttractionTag;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class AttractionTagCustom extends AbstractCustomTag implements AttractionTag {
	private static final long serialVersionUID = 1L;

	public AttractionTagCustom(String value) {
		super(value);
	}
	
}
