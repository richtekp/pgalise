/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.tag;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author richter
 */
@XmlSeeAlso({PublicTransportTagCustom.class, PublicTransportTagEnum.class})
public interface PublicTransportTag extends BaseTag {
	
}
