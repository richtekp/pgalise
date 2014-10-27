/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.event;

import de.pgalise.simulation.traffic.VehicleInformation;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;

/**
 *
 * @author richter
 */
public class CreateRandomBusData extends CreateRandomCarData {
	private static final long serialVersionUID = 1L;
	private InfraredSensor infraredSensor;

	public CreateRandomBusData(InfraredSensor infraredSensor,
		GpsSensor gpsSensor,
		VehicleInformation vehicleInformation) {
		super(gpsSensor,
			vehicleInformation);
		this.infraredSensor = infraredSensor;
	}

	public void setInfraredSensor(InfraredSensor infraredSensor) {
		this.infraredSensor = infraredSensor;
	}

	public InfraredSensor getInfraredSensor() {
		return infraredSensor;
	}
}
