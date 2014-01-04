/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.staticsensor;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.event.Event;

/**
 *
 * @author richter
 */
public interface StaticSensor<E extends Event, X extends SensorData> extends Sensor<E,X> {
	JaxRSCoordinate getPosition();
	
	void setPosition(JaxRSCoordinate position);
}
