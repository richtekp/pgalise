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
@XmlSeeAlso({ServiceTagCustom.class, ServiceTagEnum.class})
public interface ServiceTag  extends BaseTag {
	
}