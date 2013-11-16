/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.service;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.ServerConfiguration;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.ServiceDictionary;
import static de.pgalise.simulation.service.ServiceDictionary.CONFIG_READER_SERVICE;
import static de.pgalise.simulation.service.ServiceDictionary.RANDOM_SEED_SERVICE;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
public abstract class AbstractServiceDictionary implements ServiceDictionary {
	private static final Logger log = LoggerFactory.getLogger(AbstractServiceDictionary.class);

	public AbstractServiceDictionary() {
		services = new HashMap<>(7);
	}

	public AbstractServiceDictionary(
		ServerConfigurationReader<Service> serverConfigReader) {
		this();
		this.serverConfigReader = serverConfigReader;
	}

	@Override
	public void init(ServerConfiguration serverConfig) {
		List<ServiceHandler<Service>> list = new ArrayList<>(7);
		services.clear();
		initBeforeRead(list);
		serverConfigReader.read(serverConfig, list);
	}
	
	protected void initBeforeRead(List<ServiceHandler<Service>> list) {
		list.add(new ServiceHandler<Service>() {

			@Override
			public String getName() {
				return RANDOM_SEED_SERVICE;
			}

			@Override
			public void handle(String server, Service service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				services.put(getName(), service);
			}
		});
		list.add(new ServiceHandler<Service>() {

			@Override
			public String getName() {
				return CONFIG_READER_SERVICE;
			}

			@Override
			public void handle(String server, Service service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				services.put(getName(), service);
			}
		});
	}
	
	protected Map<String, Service> services;
	@EJB
	protected ServerConfigurationReader<Service> serverConfigReader;

	@Override
	public Collection<Service> getControllers() {
		return services.values();
	}

	@Override
  @SuppressWarnings(value = "unchecked")
	public <C extends Service> C getController(
		Class<? extends C> clazz) {
		Service retValue = services.get(clazz.getName());
		if (retValue == null) {
			throw new IllegalStateException("");
		}
		if (!clazz.isAssignableFrom(retValue.getClass())) {
			throw new IllegalStateException(String.format("%s has been mapped to wrong type",
				clazz));
		}
		return (C) retValue;
	}

	@Override
	public RandomSeedService getRandomSeedService() {
		Service retValue = services.get(RandomSeedService.class.getName());
		if (retValue == null) {
			throw new IllegalStateException(String.format("%s is not managed",
				RandomSeedService.class));
		}
		if (!(retValue instanceof RandomSeedService)) {
			throw new IllegalStateException(String.format("%s has been mapped to wrong type",
				RandomSeedService.class));
		}
		return (RandomSeedService) retValue;
	}

	@Override
	public ConfigReader getGlobalConfigReader() {
		Service retValue = services.get(ConfigReader.class.getName());
		if (!(retValue instanceof ConfigReader)) {
			throw new IllegalStateException(String.format("%s has been mapped to wrong type",
				ConfigReader.class));
		}
		return (ConfigReader) retValue;
	}

	public Map<String, Service> getServices() {
		return services;
	}

	protected void setServices(Map<String, Service> services) {
		this.services = services;
	}
	
}
