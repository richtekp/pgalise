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
 
package de.pgalise.simulation.internal;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.SensorRegistry;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.staticsensor.SensorFactory;
import javax.persistence.EntityManager;

/**
 * The Front Controller exists on every server and inits the {@link SensorRegistry} and the {@link ServiceDictionary}.
 * @author Mustafa
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.FrontController")
public class DefaultFrontController extends AbstractController implements Controller {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultFrontController.class);
	private static final String NAME = "FrontController";
	
	@EJB
	private SensorRegistry sensorRegistry;
	
	private EntityManager sensorPersistenceService;

	@EJB
	private ServiceDictionary serviceDictionary;
	
	@EJB
	private SensorFactory sensorFactory;

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		log.info("Initializing local ServiceDictionary");
		serviceDictionary.init(param.getServerConfiguration());
		
		/* Init sensor registry: */
		this.sensorRegistry.init();		
	}

	@Override
	protected void onReset() {
		sensorRegistry.removeAllSensors();
	}

	@Override
	protected void onStart(StartParameter param) {
	}

	@Override
	protected void onStop() {
		sensorRegistry.setSensorsActivated(false);
	}

	@Override
	protected void onUpdate(EventList simulationEventList) {
		sensorRegistry.update(simulationEventList);
	}

	@Override
	protected void onResume() {
		sensorRegistry.setSensorsActivated(true);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
