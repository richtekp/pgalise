/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.jsf;

import de.pgalise.simulation.shared.city.NavigationEdge;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richter
 */
@Stateless
public class NavigationEdgeFacade extends AbstractFacade<NavigationEdge> {
	@PersistenceContext(
  	unitName = "pgalise")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public NavigationEdgeFacade() {
		super(NavigationEdge.class);
	}
	
}
