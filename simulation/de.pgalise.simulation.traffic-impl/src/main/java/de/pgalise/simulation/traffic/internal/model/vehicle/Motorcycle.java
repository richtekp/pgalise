/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleData;

/**
 *
 * @author richter
 */
public class Motorcycle extends BaseVehicle<MotorcycleData> {

	protected Motorcycle() {
	}
	
	public Motorcycle(Long id,
		String name,
		MotorcycleData data,
		TrafficGraphExtensions trafficGraphExtensions) {
		super(id,
			name,
			data,
			trafficGraphExtensions);
	}
	
}
