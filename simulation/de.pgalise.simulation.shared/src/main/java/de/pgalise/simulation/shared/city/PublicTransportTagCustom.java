/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.AbstractCustomTag;
import de.pgalise.simulation.shared.city.PublicTransportTag;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class PublicTransportTagCustom  extends AbstractCustomTag implements PublicTransportTag{
	private static final long serialVersionUID = 1L;

	public PublicTransportTagCustom(String value) {
		super(value);
	}
	
}
