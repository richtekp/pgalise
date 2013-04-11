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
 
package de.pgalise.simulation.sensorFramework.internal.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Table;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.persistence.SensorPersistenceService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;

/**
 * Implementation for saving {@link Sensor}s to a database.
 * 
 * @author Marcus
 * @version 1.0 (Oct 31, 2012)
 */
public final class DBPersistenceService implements SensorPersistenceService {
	EntityManager em;

	/**
	 * Info class dor {@link Sensor}s
	 * 
	 * @author Marcus
	 * @version 1.0 (Aug 29, 2012)
	 */
	@Entity
	@Table(name = "PGALISE.SENSOR")
	private final static class SensorInfo {

		/**
		 * Latitude
		 */
		@Column(name = "LATITUDE")
		private double latitude;

		/**
		 * Longitude
		 */
		@Column(name = "LONGITUDE")
		private double longitude;

		/**
		 * Sensor ID
		 */
		@Id
		@Column(name = "SENSOR_ID")
		private int sensorId;

		/**
		 * SensorType ID
		 */
		@Column(name = "SENSOR_TYPE_ID")
		private int sensorTypeId;

		/**
		 * Private constructor
		 */
		private SensorInfo() {
		}

		/**
		 * Creates a {@link SensorInfo} from the passed {@link Sensor}
		 * 
		 * @param sensor
		 *            the {@link Sensor} for which this {@link SensorInfo} shall be created
		 * @throws IllegalArgumentException
		 *             if argument 'sensor' is 'null'
		 */
		private SensorInfo(final Sensor sensor) throws IllegalArgumentException {
			if (sensor == null) {
				throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensor"));
			}
			this.sensorId = sensor.getSensorId();
			this.sensorTypeId = sensor.getSensorType().ordinal();
			this.longitude = sensor.getPosition().getX();
			this.latitude = sensor.getPosition().getY();
		}
	}

	/**
	 * Creates a {@link DBPersistenceService} with default configuration of our database. The configurations are loaded
	 * from a properties file.
	 */
	public DBPersistenceService(EntityManager em) {
		this.em = em;
	}

	/**
	 * Removes all stored {@link Sensor}s from the database
	 */
	@Override
	public void clear() {
		em.createQuery("DELETE FROM DBPersistenceService$SensorInfo").executeUpdate();
	}

	/**
	 * Deletes the {@link Sensor} with the passed sensorId from the database.
	 * 
	 * @param the
	 *            sensorId of the {@link Sensor} to be deleted from database
	 * @throws UnsupportedOperationException
	 *             if the passed {@link Sensor} isn't stored in database
	 */
	@Override
	public void deleteSensor(final int sensorId) throws UnsupportedOperationException {
		try {
			SensorInfo sensorInfo = em.find(SensorInfo.class, sensorId);
			if (sensorInfo == null) {
				System.out.println("SensorInfo is null: id=" + sensorId);
			}
			em.remove(sensorInfo);
		} catch (final RuntimeException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * Deletes the passed {@link Sensor} from the database by invoking 'deleteSensor' with its extracted sensorId.
	 * 
	 * @param sensor
	 *            the {@link Sensor} to be deleted from the database
	 * @throws IllegalArgumentException
	 *             if argument 'sensor' is 'null'
	 * @throws UnsupportedOperationException
	 *             if the passed {@link Sensor} isn't stored in database
	 */
	@Override
	public void deleteSensor(final Sensor sensor) throws IllegalArgumentException, UnsupportedOperationException {
		if (sensor == null) {
			throw new IllegalArgumentException("sensor must not be NULL");
		}
		this.deleteSensor(sensor.getSensorId());
	}

	/**
	 * Returns the next available sensorId in the database.
	 * 
	 * @return the next available sensorId in the database
	 */
	@Override
	public int nextAvailableSensorId() {
		final Number result = (Number) em.createQuery("SELECT MAX(d.sensorId) FROM DBPersistenceService$SensorInfo d")
				.getSingleResult();
		return result.intValue() + 1;

	}

	/**
	 * Returns the number of {@link Sensor}s stored in database.
	 * 
	 * @return the number of {@link Sensor}s stored in database
	 */
	@Override
	public int numberOfSensors() {
		final Number result = (Number) em.createQuery("SELECT COUNT(d) FROM DBPersistenceService$SensorInfo d")
				.getSingleResult();
		return result.intValue();
	}

	/**
	 * Saves the passed {@link Sensor} to the underlying database.
	 * 
	 * @param sensor
	 *            the {@link Sensor} to be saved
	 * @throws UnsupportedOperationException
	 *             if the sensorId is already existent
	 */
	@Override
	public void saveSensor(final Sensor sensor) throws UnsupportedOperationException {
		if (sensor == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensor"));
		}
		try {
			final SensorInfo sensorInfo = new SensorInfo(sensor);
			em.persist(sensorInfo);
		} catch (final RuntimeException ex) {
			ex.printStackTrace();
			throw new UnsupportedOperationException();
		}
	}
}
