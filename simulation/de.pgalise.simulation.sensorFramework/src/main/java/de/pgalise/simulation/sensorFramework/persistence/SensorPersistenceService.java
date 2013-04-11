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
 
package de.pgalise.simulation.sensorFramework.persistence;

import de.pgalise.simulation.sensorFramework.Sensor;

/**
 * interface for persisting {@link Sensor}s.
 * 
 * @author Marcus
 * @version 1.0 (Aug 29, 2012)
 */
public interface SensorPersistenceService {

	/**
	 * Deletes all {@link Sensor}s from data source.
	 */
	void clear();

	/**
	 * Deletes the {@link Sensor} by finding it by the sensorId.
	 * 
	 * @param sensorId
	 *            the sensorId from the sensor to be deleted
	 */
	void deleteSensor(final int sensorId);

	/**
	 * Deletes the passed {@link Sensor}.
	 * 
	 * @param sensor
	 *            the {@link Sensor} to be deleted
	 * @throws IllegalArgumentException
	 *             if argument 'sensor' is 'null'
	 * @throws UnsupportedOperationException
	 *             may be thrown if the passed {@link Sensor} couldn't have been
	 *             deleted
	 */
	void deleteSensor(final Sensor sensor) throws IllegalArgumentException, UnsupportedOperationException;

	/**
	 * Returns the next available sensorId.
	 * 
	 * @return the next available sensorId
	 */
	int nextAvailableSensorId();

	/**
	 * Returns the number of {@link Sensor}s saved in the data source.
	 * 
	 * @return the number of {@link Sensor}s saved in the data source
	 */
	int numberOfSensors();

	/**
	 * Saves the passed {@link Sensor} to data source.
	 * 
	 * @param sensor
	 *            the {@link Sensor} to be saved
	 * @throws UnsupportedOperationException
	 *             if the sensorId is already existent
	 */
	void saveSensor(final Sensor sensor) throws UnsupportedOperationException;
}
