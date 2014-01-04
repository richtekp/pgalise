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
public class TourismTagCustom extends AbstractCustomTag implements TourismTag {
	private static final long serialVersionUID = 1L;

	public TourismTagCustom(String value) {
		super(value);
	}
	
}
