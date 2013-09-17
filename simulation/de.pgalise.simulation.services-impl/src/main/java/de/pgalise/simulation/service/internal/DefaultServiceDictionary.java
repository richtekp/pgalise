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
 
package de.pgalise.simulation.service.internal;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.energy.EnergyController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.FrontController;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.staticsensor.StaticSensorController;
import de.pgalise.simulation.traffic.TrafficController;
import javax.ejb.Remote;

/**
 * Default implementation of the ServiceDictionary.
 * @author mustafa
 *
 */
@Lock(LockType.READ)
@Local
@Remote
@Singleton(name = "de.pgalise.simulation.service.ServiceDictionary", mappedName = "de.pgalise.simulation.service.ServiceDictionary")
public class DefaultServiceDictionary implements ServiceDictionary {
	private static final Logger log = LoggerFactory.getLogger(DefaultServiceDictionary.class);

	private Map<Class<? extends Controller>, Controller> controllers;
	private Map<Class<? extends Service>, Service> services;

	@EJB
	private ServerConfigurationReader<Controller> serverConfigReader;

	public DefaultServiceDictionary() throws NamingException {
		controllers = new HashMap<>(7);
		services = new HashMap<>(7);
	}

	@Override
	public void init(ServerConfiguration serverConfig) {
		List<ServiceHandler<Controller>> list = new ArrayList<>(7);
		controllers.clear();
		services.clear();
		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getName() {
				return ServiceDictionary.FRONT_CONTROLLER;
			}

			@Override
			public void handle(String server,
				Controller service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				controllers.put(FrontController.class, service);
			}
		});
		 list.add(new ServiceHandler<Controller>() {

			@Override
			public String getName() {
				return ServiceDictionary.ENERGY_CONTROLLER;
			}

			@Override
			public void handle(String server,
				Controller service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				controllers.put(EnergyController.class, service);
			}
		});
		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getName() {
				return TRAFFIC_CONTROLLER;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				controllers.put(TrafficController.class, service);
			}
		});
		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getName() {
				return STATIC_SENSOR_CONTROLLER;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				controllers.put(StaticSensorController.class, service);
			}
		});
		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getName() {
				return RANDOM_SEED_SERVICE;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				services.put(RandomSeedService.class, service);
			}
		});
		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getName() {
				return CONFIG_READER_SERVICE;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				services.put(ConfigReader.class, service);
			}
		});
		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getName() {
				return SIMULATION_CONTROLLER;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				controllers.put(SimulationController.class, service);
			}
		});

		serverConfigReader.read(serverConfig, list);
	}

	@Override
	public Collection<Controller> getControllers() {
		return controllers.values();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Controller> C getController(Class<C> clazz) {
		Controller retValue = controllers.get(clazz);
		if(!clazz.isAssignableFrom(retValue.getClass())) {
			throw new IllegalStateException(String.format("%s has been mapped to wrong type", clazz));
		}
		return (C) retValue;
	}

	@Override
	public RandomSeedService getRandomSeedService() {
		Service retValue = services.get(RandomSeedService.class);
		if(!(retValue instanceof RandomSeedService)) {
			throw new IllegalStateException(String.format("%s has been mapped to wrong type", RandomSeedService.class));
		}
		return (RandomSeedService) retValue;
	}

	@Override
	public ConfigReader getGlobalConfigReader() {
		Service retValue = services.get(ConfigReader.class);
		if(!(retValue instanceof ConfigReader)) {
			throw new IllegalStateException(String.format("%s has been mapped to wrong type", ConfigReader.class));
		}
		return (ConfigReader) retValue;
	}
}
