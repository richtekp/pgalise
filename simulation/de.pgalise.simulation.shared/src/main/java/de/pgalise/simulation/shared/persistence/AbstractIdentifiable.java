/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.persistence;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * superclass for all persistence related classes which need an {@link Id}
 * @author richter
 */
@MappedSuperclass
public abstract class AbstractIdentifiable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;
//	private static Set<Long> usedIds = new HashSet<>(16);

	protected AbstractIdentifiable() {
//		this.id = UUID.randomUUID().getMostSignificantBits();
//		while(usedIds.contains(id)) {
//			this.id = UUID.randomUUID().getMostSignificantBits();
//		}
//		usedIds.add(id);
	}

	public AbstractIdentifiable(Long id) {
		this.id = id;
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
