/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.jsf;

import de.pgalise.simulation.shared.entity.Way;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richter
 */
@Stateless
public class WayFacade extends AbstractFacade<Way> {
	@PersistenceContext(unitName = "pgalise-jsf")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public WayFacade() {
		super(Way.class);
	}
	
}
