/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;

/**
 *
 * @author richter
 */
public class MotorizedVehicleData extends VehicleData {

	public MotorizedVehicleData(int length,
		int wheelbase1,
		int wheelbase2,
		int axleCount,
		VehicleTypeEnum type,
		SensorHelper gpsSensor) {
		super(length,
			wheelbase1,
			wheelbase2,
			axleCount,
			type,
			gpsSensor);
	}
	
}
