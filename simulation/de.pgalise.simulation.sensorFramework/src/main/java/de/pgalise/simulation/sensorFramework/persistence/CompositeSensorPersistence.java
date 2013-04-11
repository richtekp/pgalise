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

import java.util.HashSet;
import java.util.Set;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.exception.ExceptionMessages;

/**
 * {@link SensorPersistenceService} that can contain several other
 * {@link Output}s
 * 
 * @author Marcus
 * 
 */
public final class CompositeSensorPersistence implements SensorPersistenceService {

	/**
	 * {@link Set} eith {@link SensorPersistenceService}s
	 */
	private final Set<SensorPersistenceService> persistenceServices = new HashSet<SensorPersistenceService>();

	/**
	 * Creates a {@link CompositeSensorPersistence} with no attached
	 * {@link SensorPersistenceService}s.
	 */
	public CompositeSensorPersistence() {}

	/**
	 * Creates a {@link CompositeSensorPersistence} with the passed
	 * {@link SensorPersistenceService}s.
	 * 
	 * @param persistenceServices
	 *            a {@link Set} of {@link SensorPersistenceService}s that shall
	 *            be attached to this {@link CompositeSensorPersistence}
	 * @throws IllegalArgumentException
	 *             if argument 'persistenceServices' is 'null'
	 * @throws UnsupportedOperationException
	 *             if one of the {@link SensorPersistenceService}s is an
	 *             instance of {@link CompositeSensorPersistence}
	 */
	public CompositeSensorPersistence(final Set<SensorPersistenceService> persistenceServices) throws IllegalArgumentException, UnsupportedOperationException {
		if(persistenceServices == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("persistenceServices"));
		}
		for(final SensorPersistenceService persistenceService : persistenceServices) {
			this.attach(persistenceService);
		}
	}

	/**
	 * Attaches the passed {@link SensorPersistenceService} to this
	 * {@link CompositeSensorPersistence}. If the passed
	 * {@link SensorPersistenceService} is already attached it won't be attached
	 * any more.
	 * 
	 * @param persistenceService
	 *            the {@link SensorPersistenceService} to be attached to this
	 *            {@link CompositeSensorPersistence}
	 * @return 'true' if the passed {@link SensorPersistenceService} could have
	 *         been attached to this {@link CompositeSensorPersistence},
	 *         otherwise 'false'
	 * @throws IllegalArgumentException
	 *             if argument 'persistenceService' is 'null'
	 * @throws UnsupportedOperationException
	 *             if argument 'persistenceService' is an instance of
	 *             {@link CompositeSensorPersistence}
	 */
	public boolean attach(final SensorPersistenceService persistenceService) throws IllegalArgumentException, UnsupportedOperationException {
		if(persistenceService == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("persistenceService"));
		}
		if(persistenceService instanceof CompositeSensorPersistence) {
			throw new UnsupportedOperationException("Argument 'persistenceService' may not be a CompositeSensorPersistence.");
		}
		return this.persistenceServices.add(persistenceService);
	}

	/**
	 * Detaches the passed {@link SensorPersistenceService} from this
	 * {@link CompositeSensorPersistence}.
	 * 
	 * @param persistenceService
	 *            the {@link SensorPersistenceService} to detach from this
	 *            {@link CompositeSensorPersistence}
	 * @return 'true' if the passed {@link SensorPersistenceService} could be
	 *         detached from this {@link CompositeSensorPersistence}, otherwise
	 *         'false'
	 * @throws IllegalArgumentException
	 *             if argument 'persistenceService' is 'null'
	 */
	public boolean detach(final SensorPersistenceService persistenceService) {
		if(persistenceService == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("persistenceService"));
		}
		return this.persistenceServices.add(persistenceService);
	}

	/**
	 * Removes all attached {@link SensorPersistenceService}s of this
	 * {@link CompositeSensorPersistence}.
	 */
	public void clearAttached() {
		this.persistenceServices.clear();
	}

	/**
	 * Deletes all {@link Sensor}s from all attached
	 * {@link SensorPersistenceService}s.
	 */
	@Override
	public void clear() {
		for(final SensorPersistenceService persistenceService : this.persistenceServices) {
			persistenceService.clear();
		}
	}

	/**
	 * Deletes the {@link Sensor} from all attached
	 * {@link SensorPersistenceService}s by finding it by the sensorId.
	 * 
	 * @param sensorId
	 *            the sensorId from the sensor to be deleted
	 */
	@Override
	public void deleteSensor(final int sensorId) {
		for(final SensorPersistenceService persistenceService : this.persistenceServices) {
			persistenceService.deleteSensor(sensorId);
		}
	}

	/**
	 * Deletes the passed {@link Sensor} from all attached
	 * {@link SensorPersistenceService}s.
	 * 
	 * @param sensor
	 *            the {@link Sensor} to be deleted from all attached
	 *            {@link SensorPersistenceService}s.
	 * @throws IllegalArgumentException
	 *             if argument 'sensor' is 'null'
	 * @throws UnsupportedOperationException
	 *             may be thrown if the passed {@link Sensor} couldn't have been
	 *             deleted from one of the attached
	 *             {@link SensorPersistenceService}
	 */
	@Override
	public void deleteSensor(final Sensor sensor) {
		for(final SensorPersistenceService persistenceService : this.persistenceServices) {
			persistenceService.deleteSensor(sensor);
		}
	}

	/**
	 * This method isn't supported in {@link CompositeSensorPersistence}.
	 * 
	 * @throws UnsupportedOperationException
	 *             on each call
	 */
	@Override
	public int nextAvailableSensorId() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Method 'nextAvailableSensorId' isn't supported in CompositeSensorPersistence.");
	}

	/**
	 * This method isn't supported in {@link CompositeSensorPersistence}.
	 * 
	 * @throws UnsupportedOperationException
	 *             on each call
	 */
	@Override
	public int numberOfSensors() {
		throw new UnsupportedOperationException("Method 'nextAvailableSensorId' isn't supported in CompositeSensorPersistence.");
	}

	/**
	 * Saves the passed {@link Sensor} to all attached
	 * {@link SensorPersistenceService}s.
	 * 
	 * @param sensor
	 *            the {@link Sensor} to be saved to all attached
	 *            {@link SensorPersistenceService}s
	 * @throws UnsupportedOperationException
	 *             if the sensorId is already existent in one of the attached
	 *             {@link SensorPersistenceService}s
	 */
	@Override
	public void saveSensor(final Sensor sensor) {
		for(final SensorPersistenceService persistenceService : this.persistenceServices) {
			persistenceService.saveSensor(sensor);
		}
	}
}
