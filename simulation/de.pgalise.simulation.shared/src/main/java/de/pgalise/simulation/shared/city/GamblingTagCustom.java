/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.AbstractCustomTag;
import de.pgalise.simulation.shared.city.GamblingTag;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class GamblingTagCustom extends AbstractCustomTag implements GamblingTag{
	private static final long serialVersionUID = 1L;

	public GamblingTagCustom(String value) {
		super(value);
	}
	
}
