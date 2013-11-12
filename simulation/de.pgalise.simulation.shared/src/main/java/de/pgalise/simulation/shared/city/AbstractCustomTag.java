/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Embeddable;

/**
 * encapsulates a custom tag value to force efficient handling of otherwise computation intensive string based tags 
 * @author richter
 */
@Embeddable
public abstract class AbstractCustomTag implements BaseTag {
	private static final long serialVersionUID = 1L;
	private String value;
	private final static Set<String> VALUES = new HashSet<>();

	protected AbstractCustomTag() {
	}

	public AbstractCustomTag(String value) {
		if(VALUES.contains(value)) {
			throw new IllegalStateException("value "+value+" already used. Reuse the instance of the created tag");
		}
		this.value = value;
		VALUES.add(value);
	}
	
	@Override
	public String getStringValue() {
		return value;
	}
	
}
