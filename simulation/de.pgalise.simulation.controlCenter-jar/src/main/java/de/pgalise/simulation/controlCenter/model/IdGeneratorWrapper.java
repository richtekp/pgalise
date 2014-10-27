/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.model;

import de.pgalise.simulation.service.IdGenerator;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

/**
 * a {@link ManagedBean} to expose {@link IdGenerator}
 * @author richter
 */
@ManagedBean
public class IdGeneratorWrapper {
	@EJB
	private IdGenerator idGenerator;

	public IdGeneratorWrapper() {
	}

	public IdGeneratorWrapper(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}
	
	public Long getNextId() {
		return idGenerator.getNextId();
	}
}
