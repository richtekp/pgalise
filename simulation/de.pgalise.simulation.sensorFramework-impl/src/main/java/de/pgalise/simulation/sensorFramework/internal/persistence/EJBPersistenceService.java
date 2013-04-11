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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.persistence.SensorPersistenceService;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.configReader.Identifier;

/**
 * 
 * @author marcus
 * @author mustafa
 *
 */
@Lock(LockType.READ)
@Local
@Singleton(name="de.pgalise.sensorFramework.persistence.SensorPersistenceService")
public class EJBPersistenceService implements SensorPersistenceService {
	private static final Logger log = LoggerFactory.getLogger(EJBPersistenceService.class);
	
	@PersistenceContext(unitName="SENSOR_DATA")
	private EntityManager em;
	
	private SensorPersistenceService persistenceService;
	
	@EJB
	private ConfigReader configReader;
	
	@PostConstruct
	public void postConstruct() {
		if(configReader.getProperty(Identifier.MOCK_PERSISTENCE_SERVICE).equals("true")) {
			persistenceService = PERSISTENCE_MOCK;
			log.info("Using mocked PersistenceService");
		}
		else {
			persistenceService = new DBPersistenceService(em);
		}
	}	
	
	@Override
	public void clear() {
		persistenceService.clear();
	}

	@Override
	public void deleteSensor(int sensorId) {
		persistenceService.deleteSensor(sensorId);
	}

	@Override
	public void deleteSensor(Sensor sensor) throws IllegalArgumentException,
			UnsupportedOperationException {
		persistenceService.deleteSensor(sensor);
	}

	@Override
	public int nextAvailableSensorId() {
		return persistenceService.nextAvailableSensorId();
	}

	@Override
	public int numberOfSensors() {
		return persistenceService.numberOfSensors();
	}

	@Override
	public void saveSensor(Sensor sensor) throws UnsupportedOperationException {
		persistenceService.saveSensor(sensor);
	}
	
	/**
	 * Mock of the persistence service
	 */
	private static SensorPersistenceService PERSISTENCE_MOCK = new SensorPersistenceService() {

		@Override
		public void clear() {
		}

		@Override
		public void deleteSensor(int sensorId) {
		}

		@Override
		public void deleteSensor(Sensor sensor) throws IllegalArgumentException, UnsupportedOperationException {
		}

		@Override
		public int nextAvailableSensorId() {
			return 0;
		}

		@Override
		public int numberOfSensors() {
			return 0;
		}

		@Override
		public void saveSensor(Sensor sensor) throws UnsupportedOperationException {
		}

	};
}
