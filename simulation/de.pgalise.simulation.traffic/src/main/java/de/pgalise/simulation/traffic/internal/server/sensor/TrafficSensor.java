/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.sensor;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 *
 * @author richter
 */
public interface TrafficSensor<X extends SensorData> extends Sensor<TrafficEvent<?>,X> {
	
}
