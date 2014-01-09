/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

import de.pgalise.simulation.shared.tag.AbstractCustomTag;
import de.pgalise.simulation.shared.tag.LeisureTag;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public class LeisureTagCustom extends AbstractCustomTag implements LeisureTag {

	private static final long serialVersionUID = 1L;
	private final static Set<String> USED_VALUES = new HashSet<>();

	public LeisureTagCustom(String value) {
		super(value);
	}

	@Override
	protected Set<String> getUSED_VALUES() {
		return USED_VALUES;
	}

}
