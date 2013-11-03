/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.sensorFramework;

import java.util.Set;

import de.pgalise.simulation.service.SimulationComponent;
import de.pgalise.simulation.shared.event.Event;

/**
 * SensorRegistry
 * 
 * @author Marcus
 */
public interface SensorRegistry extends Iterable<Sensor<Event>>, SimulationComponent<Event> {

	/**
	 * Adds a sensor to the sensor domain
	 * 
	 * @param sensor
	 *            Sensor
	 * @return Sensor
	 */
	public Sensor<?> addSensor(Sensor<?> sensor);

	/**
	 * Adds a set of sensors to the sensor domain
	 * 
	 * @param sensors
	 *            Set with sensors
	 */
	public void addSensors(Set<Sensor<?>> sensors);

	/**
	 * Returns the sensor with the sensorID
	 * 
	 * @param sensorId
	 * @return sensor
	 */
	public Sensor<?> getSensor(Sensor<?> sensorId);

	/**
	 * Returns the next available id for a Sensor
	 * 
	 * @return the next available id for a Sensor
	 */
	public long nextAvailableSensorId();

	/**
	 * Returns the number of sensors in the SensorDomain
	 * 
	 * @return Number of sensors
	 */
	public long numberOfSensors();

	/**
	 * Removes all Sensors from the SensorDomain
	 */
	public void removeAllSensors();

	/**
	 * Removes a sensor from the domain
	 * 
	 * @param sensor
	 *            Sensor
	 * @return Removed sensor
	 */
	public Sensor<?> removeSensor(Sensor<?> sensor);

	/**
	 * Sets the activation state of all the sensors in the SensorDomain.
	 * 
	 * @param activated
	 *            true, when all sensors shall be activated, otherwise false
	 */
	public void setSensorsActivated(boolean activated);

	/**
	 * Inits the sensor registry.
	 * 
	 */
	public void init();
}
