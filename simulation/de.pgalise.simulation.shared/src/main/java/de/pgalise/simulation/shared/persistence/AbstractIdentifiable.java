/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.persistence;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 */
@MappedSuperclass
public class AbstractIdentifiable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id = nextId();
	private final static Set<Long> usedIds = new HashSet<>();
	
	private static long nextId() {
		long retValue = (int) (Math.random()*Integer.MAX_VALUE);
		while(usedIds.contains(retValue)) {
			retValue = (int) (Math.random()*Integer.MAX_VALUE);
		}
		usedIds.add(retValue);
		return retValue;
	}

	protected AbstractIdentifiable() {
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
	
}
