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
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.controller.ServerConfiguration;

@Lock(LockType.READ)
@Local
@Singleton(name = "de.pgalise.simulation.service.ServiceDictionary")
/**
 * Default implementation of the ServiceDictionary.
 * @author mustafa
 *
 */
public final class DefaultServiceDictionary implements ServiceDictionary {
	private static final Logger log = LoggerFactory.getLogger(DefaultServiceDictionary.class);

	private Map<String, Controller> controllers;
	private Map<String, Object> services;

	@EJB
	private ServerConfigurationReader serverConfigReader;

	public DefaultServiceDictionary() throws NamingException {
		controllers = new HashMap<>();
		services = new HashMap<>();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void init(ServerConfiguration serverConfig) {
		List<ServiceHandler> list = new ArrayList<>();
		controllers.clear();
		services.clear();
		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getSearchedName() {
				return WEATHER_CONTROLLER;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getSearchedName(), server));
				controllers.put(getSearchedName(), service);
			}
		});

		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getSearchedName() {
				return ENERGY_CONTROLLER;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getSearchedName(), server));
				controllers.put(getSearchedName(), service);
			}
		});

		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getSearchedName() {
				return TRAFFIC_CONTROLLER;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getSearchedName(), server));
				controllers.put(getSearchedName(), service);
			}
		});

		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getSearchedName() {
				return STATIC_SENSOR_CONTROLLER;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getSearchedName(), server));
				controllers.put(getSearchedName(), service);
			}
		});

		/*
		 * Services
		 */
		list.add(new ServiceHandler<RandomSeedService>() {

			@Override
			public String getSearchedName() {
				return RANDOM_SEED_SERVICE;
			}

			@Override
			public void handle(String server, RandomSeedService service) {
				log.info(String.format("Using %s on server %s", getSearchedName(), server));
				services.put(getSearchedName(), service);
			}
		});

		list.add(new ServiceHandler<ConfigReader>() {

			@Override
			public String getSearchedName() {
				return CONFIG_READER_SERVICE;
			}

			@Override
			public void handle(String server, ConfigReader service) {
				log.info(String.format("Using %s on server %s", getSearchedName(), server));
				services.put(getSearchedName(), service);
			}
		});

		list.add(new ServiceHandler<Controller>() {

			@Override
			public String getSearchedName() {
				return SIMULATION_CONTROLLER;
			}

			@Override
			public void handle(String server, Controller service) {
				log.info(String.format("Using %s on server %s", getSearchedName(), server));
				controllers.put(getSearchedName(), service);
			}
		});

		serverConfigReader.read(serverConfig, list);
	}

	@Override
	public Collection<Controller> getControllers() {
		return controllers.values();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends Controller> C getController(Class<C> clazz) {
		return (C) controllers.get(clazz.getName());
	}

	@Override
	public RandomSeedService getRandomSeedService() {
		return (RandomSeedService) services.get(RANDOM_SEED_SERVICE);
	}

	@Override
	public ConfigReader getGlobalConfigReader() {
		return (ConfigReader) services.get(CONFIG_READER_SERVICE);
	}
}
