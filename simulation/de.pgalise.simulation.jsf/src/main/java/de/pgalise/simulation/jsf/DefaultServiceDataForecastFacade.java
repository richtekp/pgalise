/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.jsf;

import de.pgalise.simulation.weather.model.DefaultServiceDataForecast;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richter
 */
@Stateless
public class DefaultServiceDataForecastFacade extends AbstractFacade<DefaultServiceDataForecast> {
	@PersistenceContext(
  	unitName = "pgalise")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public DefaultServiceDataForecastFacade() {
		super(DefaultServiceDataForecast.class);
	}
	
}
