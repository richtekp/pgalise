/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.graphextension.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.BusData;

/**
 *
 * @author richter
 */
public class Bus extends BaseVehicle<BusData> {

	protected Bus() {
	}

	public Bus(String name,
		BusData data,
		DefaultTrafficGraphExtensions<BusData> trafficGraphExtensions) {
		super(name,
			data,
			trafficGraphExtensions);
	}
	
}
