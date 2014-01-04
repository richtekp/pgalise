/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Truck;
import de.pgalise.simulation.traffic.model.vehicle.TruckData;

/**
 *
 * @author richter
 */
public class DefaultTruck extends ExtendedMotorizedVehicle<TruckData> implements Truck {
	private static final long serialVersionUID = 1L;

	protected DefaultTruck() {
	}

	public DefaultTruck(Long id, String name, TruckData data, TrafficGraphExtensions trafficGraphExtensions) {
		super(id,
			name,
			data,
			trafficGraphExtensions);
	}
}
