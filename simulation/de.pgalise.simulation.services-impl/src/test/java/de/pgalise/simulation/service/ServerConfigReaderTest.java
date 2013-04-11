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
 
package de.pgalise.simulation.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.configReader.Identifier;
import de.pgalise.simulation.service.internal.manager.DefaultServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.shared.controller.ServerConfiguration.Entity;
import de.pgalise.util.generic.MutableBoolean;

/**
 * Tests the ServerConfigReader
 * 
 * @author Mustafa
 * @version 1.0 (Feb 6, 2013)
 */
@Ignore
public class ServerConfigReaderTest {
	@SuppressWarnings("rawtypes")
	@Test
	public void readLocalTest() {
		ConfigReader configReader = EasyMock.createNiceMock(ConfigReader.class);
		// services unter dieser adresse werden lokal aufgelöst, alle anderen ferngesteuert
		EasyMock.expect(configReader.getProperty(Identifier.SERVER_HOST)).andReturn("127.0.0.1:8081");
		EasyMock.replay(configReader);

		ServerConfigurationReader reader = new DefaultServerConfigurationReader(configReader);
		List<ServiceHandler> handlers = new ArrayList<>();
		final MutableBoolean b = new MutableBoolean();
		b.setValue(false);
		handlers.add(new ServiceHandler<RandomSeedService>() {
			@Override
			public String getSearchedName() {
				return ServiceDictionary.RANDOM_SEED_SERVICE;
			}

			@Override
			public void handle(String server, RandomSeedService service) {
				b.setValue(true);
			}

		});
		reader.read(getServerConfiguration(), handlers);
		System.out.println(b.getValue());

		assertTrue(b.getValue());
	}

	/**
	 * Read configurations
	 * 
	 * @return ServerConfiguration
	 */
	public ServerConfiguration getServerConfiguration() {
		ServerConfiguration conf = new ServerConfiguration();
		// local services
		List<Entity> entities = new ArrayList<>();
		entities.add(new Entity(RandomSeedService.class.getName()));
		// adresse stimmt nicht mit der lokalen überein (sagt der configReader oben),
		// d.h. services werden über ferngesteuert aufgelöst
		conf.getConfiguration().put("127.0.0.1:8081", entities);

		return conf;
	}

	/**
	 * Start
	 * 
	 * @param args
	 * @throws NamingException
	 */
	public static void main(String args[]) throws NamingException {
		ServerConfigReaderTest a = new ServerConfigReaderTest();
		a.readLocalTest();

	}
}
