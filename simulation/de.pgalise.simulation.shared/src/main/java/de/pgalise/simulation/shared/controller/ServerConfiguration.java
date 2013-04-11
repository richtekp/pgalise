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
 
package de.pgalise.simulation.shared.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<String, List<Entity>> configuration;

	/**
	 * Default constructor
	 */
	public ServerConfiguration() {
		configuration = new HashMap<>();
	}

	public void setConfiguration(Map<String, List<Entity>> configuration) {
		this.configuration = configuration;
	}

	public Map<String, List<Entity>> getConfiguration() {
		return configuration;
	}

	/**
	 * An Entity describes a service that is located on a specific server.
	 * @author Mustafa
	 */
	public static class Entity implements Serializable {

		/**
		 * Serial
		 */
		private static final long serialVersionUID = 5194129593797800215L;

		/**
		 * Name
		 */
		private String name;

		/**
		 * Constructor
		 */
		public Entity() {

		}

		public Entity(String name) {
			this.name = name;
		}

		/**
		 * 
		 * @return name of this service/entity
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name of this service/entity
		 * @param name
		 */
		public void setName(String name) {
			this.name = name;
		}
	}
}
