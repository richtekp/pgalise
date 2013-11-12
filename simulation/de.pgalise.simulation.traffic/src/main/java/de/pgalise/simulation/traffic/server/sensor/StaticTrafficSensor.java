/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.server.sensor;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 *
 * @author richter
 */
public interface StaticTrafficSensor extends Sensor<TrafficEvent> {

	/**
	 * Register vehicle on the node
	 *
	 * @param vehicle
	 *            Vehicle
	 */
	void vehicleOnNodeRegistered(Vehicle<?> vehicle);
	
}
