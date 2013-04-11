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
 
package de.pgalise.simulation.service.manager;

import java.util.List;

import de.pgalise.simulation.shared.controller.ServerConfiguration;

/**
 * The ServerConfigurationReader looks for services the user is interested in
 * on the servers that are listed in the ServerConfiguration.
 * Whenever a service could be found a callback function is executed in order to inform the user 
 * and pass him the resolved reference to the service.
 *  
 * @see de.pgalise.simulation.shared.controller.ServerConfiguration
 * @see de.pgalise.simulation.service.manager.ServiceHandler
 * @author mustafa
 *
 */
public interface ServerConfigurationReader {
	/**
	 * 
	 * @param serverConfig list of servers and distributed services
	 * @param handler list of callback functions used to inform the user about the services he is looking for
	 */
	@SuppressWarnings("rawtypes")
	public void read(ServerConfiguration serverConfig, List<ServiceHandler> handler);

	/**
	 * 
	 * @param serverConfig list of servers and distributed services
	 * @param handler callback function used to inform the user about the service he is looking for
	 */
	@SuppressWarnings("rawtypes")
	public void read(ServerConfiguration serverConfig, ServiceHandler handler);
}
