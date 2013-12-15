/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.TruckData;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;

/**
 *
 * @author richter
 */
public class Truck extends BaseVehicle<TruckData> {
	private static final long serialVersionUID = 1L;

	public Truck() {
	}

	public Truck(Long id, String name, TruckData data, TrafficGraphExtensions trafficGraphExtensions) {
		super(id,
			name,
			data,
			trafficGraphExtensions);
	}

	
	
}
