/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author richter
 */
@XmlSeeAlso({TourismTagCustom.class, TourismTagEnum.class})
public interface TourismTag extends BaseTag {
	
}