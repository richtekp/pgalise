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
 
package de.pgalise.simulation.sensorFramework.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorRegistry;
import de.pgalise.simulation.sensorFramework.persistence.SensorPersistenceService;
import de.pgalise.simulation.shared.event.SimulationEventList;

/**
 * A SensorDomain is supposed to administrate Sensors. Sensors can be added and removed in the SensorDomain.
 * 
 * @author Marcus
 * @version 1.0
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.sensorFramework.SensorRegistry")
@Local
public class DefaultSensorRegistry implements SensorRegistry {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultSensorRegistry.class);

	/**
	 * Service for saving the sensors.
	 */
	@EJB
	private SensorPersistenceService persistenceService;

	/**
	 * Map with IDs to sensors
	 */
	private final Map<Integer, Sensor> sensorIdsToSensors = new LinkedHashMap<Integer, Sensor>();

	/**
	 * Map with sensors to IDs
	 */
	private final Map<Sensor, Integer> sensorsToSensorIds = new LinkedHashMap<Sensor, Integer>();

	/**
	 * Default
	 */
	public DefaultSensorRegistry() {

	}

	/**
	 * Constructor
	 * 
	 * @param persistenceService
	 *            Service for saving the sensors.
	 */
	public DefaultSensorRegistry(final SensorPersistenceService persistenceService) {
		this(persistenceService, new HashSet<Sensor>());
	}

	/**
	 * Constructor
	 * 
	 * @param persistenceService
	 *            Service for saving the sensors.
	 * @param sensors
	 *            Set of sensors
	 */
	public DefaultSensorRegistry(final SensorPersistenceService persistenceService, final Set<Sensor> sensors) {
		if (persistenceService == null) {
			throw new IllegalArgumentException("persistenceService");
		}
		this.persistenceService = persistenceService;

		// now we can add the committed sensors
		this.addSensors(sensors);
	}

	@Override
	public synchronized Sensor addSensor(final Sensor sensor) {
		if (sensor == null) {
			throw new IllegalArgumentException("sensor must not be NULL");
		}
		log.debug("Sensor " + sensor.getSensorId() + " added to the SensorRegistry");
		this.addSensorWithoutSave(sensor);
		this.persistenceService.saveSensor(sensor);
		return sensor;
	}

	@Override
	public synchronized void addSensors(final Set<Sensor> sensors) {
		for (final Sensor sensor : sensors) {
			this.addSensor(sensor);
		}
	}

	@Override
	public Sensor getSensor(final int sensorId) {
		return this.sensorIdsToSensors.get(sensorId);
	}

	@Override
	public Iterator<Sensor> iterator() {
		return this.sensorsToSensorIds.keySet().iterator();
	}

	@Override
	public int nextAvailableSensorId() {
		return this.persistenceService.nextAvailableSensorId();
	}

	@Override
	public final int numberOfSensors() {
		return this.sensorsToSensorIds.size();
	}

	@Override
	public final synchronized void removeAllSensors() {
		Set<Integer> sensorIds = new HashSet<>();

		// Save all IDs
		for (final Sensor sensor : this) {
			sensorIds.add(sensor.getSensorId());
		}

		// Delete all IDs
		for (Integer id : sensorIds) {
			this.removeSensor(id);
		}
	}

	@Override
	public final synchronized Sensor removeSensor(final int sensorId) {
		final Sensor result = this.sensorIdsToSensors.remove(sensorId);
		if (result != null) {
			this.sensorsToSensorIds.remove(result);

			// delete the sensor from persistence
			log.debug("Sensor is to be removed from Database (id:" + sensorId + ")");
			this.persistenceService.deleteSensor(sensorId);
		}

		// return the removed sensor or null
		return result;
	}

	@Override
	public final synchronized Sensor removeSensor(final Sensor sensor) {
		// Delete the Sensor from RAM
		return this.removeSensor(sensor.getSensorId());
	}

	@Override
	public final synchronized void setSensorsActivated(final boolean activated) {
		for (final Sensor sensor : this) {
			sensor.setActivated(activated);
		}
	}

	/**
	 * Updates all sensors in the SensorDomain
	 * 
	 * @param eventList
	 *            the simulation event used by the sensors
	 */
	@Override
	public synchronized final void update(final SimulationEventList eventList) {
		for (final Sensor sensor : this) {
			sensor.update(eventList);
		}
	}

	/**
	 * Adds a sensor to the sensor domain without to save
	 * 
	 * @param sensor
	 *            Sensor
	 * @return created sensor
	 */
	private final void addSensorWithoutSave(final Sensor sensor) {
		this.sensorIdsToSensors.put(sensor.getSensorId(), sensor);
		this.sensorsToSensorIds.put(sensor, sensor.getSensorId());
	}

	@Override
	public void init() {
	}
}
