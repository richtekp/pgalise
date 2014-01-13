/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.tag.BaseTag;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 * @param <T>
 */
@MappedSuperclass
public abstract class Tag<T extends BaseTag> {

	@Embedded
	private T tagValue;

	public Tag(T tagValue) {
		this.tagValue = tagValue;
	}

	public T getTagValue() {
		return tagValue;
	}

	public void setTagValue(T tagValue) {
		this.tagValue = tagValue;
	}
}
