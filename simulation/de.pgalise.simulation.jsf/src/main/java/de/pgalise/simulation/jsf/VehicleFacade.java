/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.jsf;

import de.pgalise.simulation.traffic.entity.TrafficTrip;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author richter
 */
public class VehicleFacade extends AbstractFacade<Vehicle> {
	@PersistenceContext(unitName = "pgalise-jsf")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public VehicleFacade() {
		super(Vehicle.class);
	}
}
