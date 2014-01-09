/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class WayTagCustom extends AbstractCustomTag implements WayTag {

	private static final long serialVersionUID = 1L;
	private final static Set<String> USED_VALUES = new HashSet<>();

	public WayTagCustom(String stringValue) {
		super(stringValue);
	}

	@Override
	protected Set<String> getUSED_VALUES() {
		return USED_VALUES;
	}
}
