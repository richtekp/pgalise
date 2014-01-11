/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.server.sensor;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 *
 * @author richter
 */
public interface StaticTrafficSensor<X extends SensorData> extends TrafficSensor<X>{
	JaxRSCoordinate getPosition();

	/**
	 * Register vehicle on the node
	 *
	 * @param vehicle
	 *            Vehicle
	 */
	void vehicleOnNodeRegistered(Vehicle<?> vehicle);
	
}
