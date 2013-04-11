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

/**
 * The ServiceHandler has two jobs: <br/>
 * 1) To tell the ServerConfigurationReader which service 
 * the user is looking for. <br/>
 * 2) To provide a callback function which will be invoked with 
 * the reference to the service whenever it could be found.
 * 
 * @see de.pgalise.simulation.shared.controller.ServerConfiguration
 * @see de.pgalise.simulation.service.manager.ServerConfigurationReader
 * 
 * @author mustafa
 * @param <T> type of the service (may be a class or interface)
 */
public interface ServiceHandler<T> {
	/**
	 * 
	 * @return name of the service the user is looking for
	 */
	public String getSearchedName();
	
	/**
	 * Callback method invoked whenever the searched service 
	 * could be found.
	 * @param server address of the server hosting the service
	 * @param service reference to the service
	 */
	public void handle(String server, T service);
}
