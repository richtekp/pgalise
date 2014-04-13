/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.server.sensor;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 *
 * @author richter
 */
public interface StaticTrafficSensor<X extends SensorData> extends TrafficSensor<X>{
	BaseCoordinate getPosition();

	/**
	 * Register vehicle on the node
	 *
	 * @param vehicle
	 *            Vehicle
	 */
	void vehicleOnNodeRegistered(Vehicle<?> vehicle);
	
}
