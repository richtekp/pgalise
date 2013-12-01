/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.internal;

import de.pgalise.simulation.traffic.TrafficNode;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richter
 */
@Stateless
public class TrafficNodeFacade extends AbstractFacade<TrafficNode> {
	@PersistenceContext(
  	unitName = "de.pgalise.simulation_controlCenter_war_2.0-SNAPSHOTPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TrafficNodeFacade() {
		super(TrafficNode.class);
	}
	
}
