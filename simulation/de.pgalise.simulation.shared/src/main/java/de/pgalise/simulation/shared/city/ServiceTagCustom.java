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
public class ServiceTagCustom extends AbstractCustomTag implements ServiceTag {
	private static final long serialVersionUID = 1L;

	public ServiceTagCustom(String value) {
		super(value);
	}
	
}
