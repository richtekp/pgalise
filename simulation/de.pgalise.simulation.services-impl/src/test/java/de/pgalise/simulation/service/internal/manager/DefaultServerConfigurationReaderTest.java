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
 
package de.pgalise.simulation.service.internal.manager;

import de.pgalise.it.TestUtils;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.ServiceDictionary;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


import static org.easymock.EasyMock.*;
import org.junit.Test;

import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.ServerConfigurationIdentifier;
import de.pgalise.simulation.service.internal.DefaultServiceDictionary;
import de.pgalise.simulation.service.internal.manager.DefaultServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.service.ServerConfiguration;
import de.pgalise.simulation.service.ServerConfigurationEntity;
import de.pgalise.util.generic.MutableBoolean;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import org.junit.BeforeClass;

/**
 * Tests the ServerConfigReader
 * 
 * @author Mustafa
 * @version 1.0 (Feb 6, 2013)
 */
public class DefaultServerConfigurationReaderTest {
	private static EJBContainer CONTAINER;
	
	@BeforeClass
	public static void setUpClass() {
		CONTAINER = TestUtils.getContainer();
	}

	@Test
	public void readLocalTest() throws NamingException {
		ConfigReader configReader = createNiceMock(ConfigReader.class);
		// services unter dieser adresse werden lokal aufgelöst, alle anderen ferngesteuert
		expect(configReader.getProperty(ServerConfigurationIdentifier.SERVER_HOST)).andReturn("127.0.0.1:8081");
		replay(configReader);

		ServerConfigurationReader<Service> reader = new DefaultServerConfigurationReader(configReader);
		List<ServiceHandler<Service>> handlers = new ArrayList<>();
		final MutableBoolean b = new MutableBoolean();
		b.setValue(false);
		handlers.add(new ServiceHandler<Service>() {
			@Override
			public String getName() {
				return ServiceDictionary.RANDOM_SEED_SERVICE;
			}

			@Override
			public void handle(String server, Service service) {
				b.setValue(true);
			}

		});
		reader.read(getServerConfigurationLocal(), handlers);
		assertTrue(b.getValue());
		
		reset(configReader);
		expect(configReader.getProperty(ServerConfigurationIdentifier.SERVER_HOST)).andReturn("127.0.0.1:8081");
		replay(configReader);
		ServiceDictionary serviceDictionary = new DefaultServiceDictionary();
		List<ServiceHandler<Service>> serviceHandlers = new ArrayList<>(7);
		final Map<Service, Boolean> checkMap = new HashMap<>(7);
		for(final Service service : serviceDictionary.getControllers()) {
			serviceHandlers.add(new ServiceHandler<Service>() {

				@Override
				public String getName() {
					return service.getClass().getName();
				}

				@Override
				public void handle(String server,
					Service service) {
					checkMap.put(service,
						Boolean.TRUE);
				}
			});
		}
		reader.read(getServerConfigurationLocal(),
			handlers);
		for(Service service : serviceDictionary.getControllers()) {
			Boolean checkValue = checkMap.get(service);
			assertNotNull(checkValue);
			assertTrue(checkValue);
		}
	}

	/**
	 * Read configurations
	 * 
	 * @return ServerConfiguration
	 */
	private ServerConfiguration getServerConfigurationLocal() {
		ServerConfiguration conf = new ServerConfiguration();
		// local services
		List<ServerConfigurationEntity> entities = new ArrayList<>();
		entities.add(new ServerConfigurationEntity(RandomSeedService.class.getName()));
		// adresse stimmt nicht mit der lokalen überein (sagt der configReader oben),
		// d.h. services werden über ferngesteuert aufgelöst
		conf.getConfiguration().put("127.0.0.1:8081", entities);
		
		return conf;
	}

}
