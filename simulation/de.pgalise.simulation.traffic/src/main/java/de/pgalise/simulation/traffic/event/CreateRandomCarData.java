/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.event;

import de.pgalise.simulation.traffic.VehicleInformation;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;

/**
 *
 * @author richter
 */
public class CreateRandomCarData extends CreateRandomVehicleData {
	private static final long serialVersionUID = 1L;
	private GpsSensor gpsSensor;

	public CreateRandomCarData(GpsSensor gpsSensor,
		VehicleInformation vehicleInformation) {
		super(vehicleInformation);
		this.gpsSensor = gpsSensor;
	}

	public void setGpsSensor(GpsSensor gpsSensor) {
		this.gpsSensor = gpsSensor;
	}

	public GpsSensor getGpsSensor() {
		return gpsSensor;
	}
}
