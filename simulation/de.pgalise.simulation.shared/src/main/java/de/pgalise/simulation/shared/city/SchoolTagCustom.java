/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class SchoolTagCustom  extends AbstractCustomTag implements SchoolTag{
	private static final long serialVersionUID = 1L;

	public SchoolTagCustom(String value) {
		super(value);
	}
	
}
