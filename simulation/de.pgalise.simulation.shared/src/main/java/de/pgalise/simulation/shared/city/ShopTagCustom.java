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
public class ShopTagCustom extends AbstractCustomTag implements ShopTag {
	private static final long serialVersionUID = 1L;

	public ShopTagCustom(String value) {
		super(value);
	}
	
}
