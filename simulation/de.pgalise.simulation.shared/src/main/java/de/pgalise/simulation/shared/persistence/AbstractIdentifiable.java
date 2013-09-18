/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.persistence;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * superclass for all persistence related classes which need an {@link Id}. It 
 * assumes that the space of {@link Long#MAX_VALUE } + abs({@link Long#MIN_VALUE}) is never exhausted and doesn't provide any action to react to this situation (due to performance reasons).
 * @author richter
 */
@MappedSuperclass
public abstract class AbstractIdentifiable implements Serializable, Identifiable {
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractIdentifiable.class);
	
	@Id
	private Long id;
	private static final Set<Long> usedIds = new HashSet<>(16);

	protected AbstractIdentifiable() {
//		synchronized(usedIds) {
			this.id = UUID.randomUUID().getMostSignificantBits();
			LOGGER.debug("id: "+id);
			while(usedIds.contains(id)) {
				this.id = UUID.randomUUID().getMostSignificantBits();
				LOGGER.debug("id="+id+";usedIds="+usedIds);
			}
			usedIds.add(id);
//		}
	}

	public AbstractIdentifiable(Long id) {
		if(id == null) {
			throw new IllegalArgumentException("id mustn't be null");
		}
		if(usedIds.contains(id)) {
			throw new IllegalArgumentException("id is already in use");
		}
		this.id = id;
		usedIds.add(id);
	}

	@Override
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
