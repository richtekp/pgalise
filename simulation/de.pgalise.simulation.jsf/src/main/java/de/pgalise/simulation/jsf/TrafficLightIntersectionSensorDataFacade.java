/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.jsf;

import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightIntersectionSensorData;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richter
 */
@Stateless
public class TrafficLightIntersectionSensorDataFacade extends AbstractFacade<TrafficLightIntersectionSensorData> {
	@PersistenceContext(unitName = "pgalise-jsf")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TrafficLightIntersectionSensorDataFacade() {
		super(TrafficLightIntersectionSensorData.class);
	}
	
}
