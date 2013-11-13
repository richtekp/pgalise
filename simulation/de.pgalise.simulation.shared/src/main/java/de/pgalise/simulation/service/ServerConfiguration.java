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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The ServerConfiguration is used to distribute services in the simulation 
 * on different servers by mapping one or more services (entities) to a specific server address.
 * @see de.pgalise.simulation.service.ServiceDictionary
 * @see Entity
 * @author Mustafa
 */
public class ServerConfiguration implements Serializable {	
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -8052273029578796442L;
	/**
	 * Map with keys = ip:port and entities
	 */
	private Map<String, List<ServerConfigurationEntity>> configuration;
	
	public final static ServerConfiguration DEFAULT_SERVER_CONFIGURATION = new ServerConfiguration(ServiceDictionary.SERVICES);

	/**
	 * Default constructor
	 */
	public ServerConfiguration() {
		configuration = new HashMap<>();
	}

	public ServerConfiguration(
		Set<String> serviceNames) {
		this();
		List<ServerConfigurationEntity> entities = new ArrayList<>(ServiceDictionary.SERVICES.size());
		for(String serviceName : serviceNames) {
			entities.add(new ServerConfigurationEntity(serviceName));
		} 
		configuration.put("127.0.0.1:8081",
			entities);
	}

	public void setConfiguration(Map<String, List<ServerConfigurationEntity>> configuration) {
		this.configuration = configuration;
	}

	/**
	 * a {@link Map} mapping path specifications in the form of <tt><IP>:<PORT></tt> to a list of {@link ServerConfigurationEntity}s
	 * @return 
	 */
	public Map<String, List<ServerConfigurationEntity>> getConfiguration() {
		return configuration;
	}
}
