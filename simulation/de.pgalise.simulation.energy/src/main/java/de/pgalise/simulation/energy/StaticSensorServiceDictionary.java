/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.energy;

import de.pgalise.simulation.service.ServiceDictionary;

/**
 *
 * @author richter
 */
public interface StaticSensorServiceDictionary extends ServiceDictionary {
	public static final String STATIC_SENSOR_CONTROLLER = EnergySensorController.class.getName();
}
