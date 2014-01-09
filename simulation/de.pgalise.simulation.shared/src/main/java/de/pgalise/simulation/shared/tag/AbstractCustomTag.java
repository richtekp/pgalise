/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

import java.util.Set;
import javax.persistence.Embeddable;

/**
 * encapsulates a custom tag value to force efficient handling of otherwise
 * computation intensive string based tags
 *
 * @author richter
 */
@Embeddable
public abstract class AbstractCustomTag implements BaseTag {

	private static final long serialVersionUID = 1L;
	private String value;

	protected AbstractCustomTag() {
	}

	public AbstractCustomTag(String value) {
		if (getUSED_VALUES().contains(value)) {
			throw new IllegalStateException(
				"value " + value + " already used. Reuse the instance of the created tag");
		}
		this.value = value;
		getUSED_VALUES().add(value);
	}

	@Override
	public String getStringValue() {
		return value;
	}

	protected abstract Set<String> getUSED_VALUES();

}
