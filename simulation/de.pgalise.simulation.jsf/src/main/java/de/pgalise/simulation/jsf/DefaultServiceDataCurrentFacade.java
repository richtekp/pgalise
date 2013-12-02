/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.jsf;

import de.pgalise.simulation.weather.model.DefaultServiceDataCurrent;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richter
 */
@Stateless
public class DefaultServiceDataCurrentFacade extends AbstractFacade<DefaultServiceDataCurrent> {
	@PersistenceContext(
  	unitName = "pgalise")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public DefaultServiceDataCurrentFacade() {
		super(DefaultServiceDataCurrent.class);
	}
	
}
