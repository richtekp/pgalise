/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.internal;

import de.pgalise.simulation.weather.model.StationDataAggregate;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richter
 */
@Stateless
public class StationDataAggregateFacade extends AbstractFacade<StationDataAggregate> {
	@PersistenceContext(
  	unitName = "de.pgalise.simulation_controlCenter_war_2.0-SNAPSHOTPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public StationDataAggregateFacade() {
		super(StationDataAggregate.class);
	}
	
}
