/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.server.sensor;

import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 *
 * @author richter
 */
public interface StaticTrafficSensor extends Identifiable {

	/**
	 * Register vehicle on the node
	 *
	 * @param vehicle
	 *            Vehicle
	 */
	void vehicleOnNodeRegistered(Vehicle<?> vehicle);
	
}
