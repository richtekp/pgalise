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
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorRegistry;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * A SensorDomain is supposed to administrate Sensors. Sensors can be added and removed in the SensorDomain.
 * 
 * @author Marcus
 * @version 1.0
 */
@Lock(LockType.READ)
@Singleton(mappedName = "de.pgalise.simulation.sensorFramework.SensorRegistry", name = "de.pgalise.simulation.sensorFramework.SensorRegistry")
@Local
public class DefaultSensorRegistry implements SensorRegistry {
	private static Set<Long> usedIds = new HashSet<>(16);

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultSensorRegistry.class);

	/**
	 * Service for saving the sensors.
	 */
	private EntityManager persistenceService;

	/**
	 * Default
	 */
	protected DefaultSensorRegistry() {

	}

	/**
	 * Constructor
	 * 
	 * @param persistenceService
	 *            Service for saving the sensors.
	 */
	public DefaultSensorRegistry(final EntityManager persistenceService) {
		this(persistenceService, new HashSet<Sensor<?,?>>());
	}

	/**
	 * Constructor
	 * 
	 * @param persistenceService
	 *            Service for saving the sensors.
	 * @param sensors
	 *            Set of sensors
	 */
	public DefaultSensorRegistry(final EntityManager persistenceService, final Set<Sensor<?,?>> sensors) {
		if (persistenceService == null) {
			throw new IllegalArgumentException("persistenceService");
		}
		this.persistenceService = persistenceService;

		// now we can add the committed sensors
		this.addSensors(sensors);
	}

	@Override
	public synchronized Sensor<?,?> addSensor(final Sensor<?,?> sensor) {
		if (sensor == null) {
			throw new IllegalArgumentException("sensor must not be NULL");
		}
		log.debug("Sensor " + sensor.getId()+ " added to the SensorRegistry");
		this.persistenceService.persist(sensor);
		return sensor;
	}

	@Override
	public synchronized void addSensors(final Set<Sensor<?,?>> sensors) {
		for (final Sensor<?,?> sensor : sensors) {
			this.addSensor(sensor);
		}
	}

	@Override
	public Iterator<Sensor<Event,?>> iterator() {
		Query query = this.persistenceService.createQuery(String.format("SELECT * FROM %s s", Sensor.class.getName()));
		return query.getResultList().iterator();
	}

	@Override
	public long nextAvailableSensorId() {
		long retValue = UUID.randomUUID().getMostSignificantBits();
		while(usedIds.contains(retValue)) {
			retValue = UUID.randomUUID().getMostSignificantBits();
		}
		usedIds.add(retValue);
		return retValue;
	}

	@Override
	public long numberOfSensors() {
		Query query = this.persistenceService.createQuery(String.format("SELECT COUNT(*) FROM %s s", Sensor.class.getName()));
		return (long) query.getSingleResult();
	}

	@Override
	public synchronized void removeAllSensors() {
		Set<Long> sensorIds = new HashSet<>();

		// Save all IDs
		for (final Sensor<?,?> sensor : this) {
			this.removeSensor(sensor);
		}
	}

	@Override
	public synchronized Sensor<?,?> removeSensor(final Sensor<?,?> sensor) {
		// Delete the Sensor from RAM
		final Sensor<?,?> result = this.persistenceService.find(Sensor.class, sensor.getId());
		if (result != null) {
			log.debug("Sensor is to be removed from Database (id:" + sensor.getId() + ")");
			this.persistenceService.remove(result);

			// delete the sensor from persistence
		}

		// return the removed sensor or null
		return result;
	}

	@Override
	public synchronized void setSensorsActivated(final boolean activated) {
		for (final Sensor<?,?> sensor : this) {
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
	public synchronized void update(final EventList<Event> eventList) {
		for (final Sensor<Event,?> sensor : this) {
			sensor.update(eventList);
		}
	}

	@Override
	public void init() {
	}

	@Override
	public Sensor<?,?> getSensor(
		Sensor<?,?> sensorId) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public <S extends Sensor<?, ?>> Set<S> getAllSensors(Class<S> clazz) {
		 TypedQuery<S> query = this.persistenceService.createQuery(
			String.format("SELECT s FROM %s s", clazz.getName()),
			clazz
		);
		 return new HashSet<>(query.getResultList());
	}
}
