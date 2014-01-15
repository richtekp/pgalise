/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * superclass for all persistence related classes which need an {@link Id}. It 
 * assumes that the space of {@link Long#MAX_VALUE } + abs({@link Long#MIN_VALUE}) is never exhausted and doesn't provide any action to react to this situation (due to performance reasons).
 * @author richter
 */
@MappedSuperclass
public abstract class Identifiable implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Set<Long> usedIds = new HashSet<>(16);
	@Id
	private Long id;
	
	public static Long generateId() {
		Long id;
		synchronized(usedIds) {
			id = UUID.randomUUID().getMostSignificantBits();
			while(usedIds.contains(id)) {
				id = UUID.randomUUID().getMostSignificantBits();
			}
			usedIds.add(id);
		}
		return id;
	}

	protected Identifiable() {
	}
	
	public Identifiable(Long id) {
		if(id == null) {
			throw new IllegalArgumentException("id mustn't be null");
		}
		this.id = id;
		usedIds.add(id);
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		String retValue = new ReflectionToStringBuilder(this).toString();
		return retValue;
	}
	
}
