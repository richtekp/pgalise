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
 
package de.pgalise.simulation.service.configReader;

import java.io.Serializable;
import java.util.EnumSet;

/**
 * Enumeration of properties used by the ConfigReader to configure the simulation.    
 * @author mustafa
 *
 */
public enum Identifier implements Serializable {
	/**
	 * @return IP of this server (default 127.0.0.1)
	 */
	SERVER_IP("simulation.configuration.server.ip"),
	/**
	 * @return port used by the ejbd protocol (default 4021)
	 * @see Identifier#EJBD_PROTOCOL_ENABLED 
	 */
	SERVER_EJBD_PORT("simulation.configuration.server.ejbd.port"),
	/**
	 * @return port of the http protocol used by the webapps (default 8080)
	 */
	SERVER_HTTP_PORT("simulation.configuration.server.http.port"),
	/**
	 * Returns the ip of this host including the port used to access remote EJBs.
	 * The port depends on weather the ejb protocol is enabled or disabled.
	 * The value is automatically set and can't be overridden by the configuration.
	 * @see Identifier#EJBD_PROTOCOL_ENABLED
	 * @return server.ip+":"+[ejb.port|http.port] (default 127.0.0.1)
	 */
	SERVER_HOST("simulation.configuration.server.host"),	
	/**
	 * @return host of the datastream management system (ip+port) (default 127.0.0.1:6666)
	 */
	SENSOR_OUTPUT_SERVER("simulation.configuration.server.sensor_output"),
	/**
	 * Tells the simulation to mock the PersistenceService used by the SensorFramework.
	 * @return true or false (default true)
	 */
	MOCK_PERSISTENCE_SERVICE("simulation.configuration.server.persistence_service.mock"),
	/**
	 * Specifies if the simulation should log the time
	 * that has been past executing a particular process (e.g. starting all controller).
	 * 
	 * @return true or false (default false)
	 */
	SCALE_TEST("simulation.configuration.scaletest"),
	/**
	 * Specifies the path of the csv output file holding 
	 * the data of the scale test.
	 * If not explicit set the file will be saved under the 
	 * root directory.
	 * Has no effects if SCALE_TEST is not set to true.
	 * @see Identifier#SCALE_TEST
	 * @return path of the csv output file of the scale test (default ${catalina.base} or ${user.dir}) 
	 */
	SCALE_PATH("simulation.configuration.scaletest.path"),
	/**
	 * If enabled the simulation will use the ejbd protocol to resolve remote EJBs 
	 * otherwise the http protocol is used.
	 * @return true or false (default false -> http protocol will be used)
	 */
	EJBD_PROTOCOL_ENABLED("simulation.configuration.ejbdprotocol.enabled"),
	
	/**
	 * If enabled {@link DefaultControlCenterControllerLoader} will load an mock.
	 * @return true or false (default false)
	 */
	MOCK_CONTROL_CENTER_CONTROLLER("simulation.configuration.server.ccc.mock"),
	
	/**
	 * If enabled {@link DefaultOperationCenterControllerLoader} will load an mock.
	 * @return true or false (default false)
	 */
	MOCK_OPERATION_CENTER_CONTROLLER("simulation.configuration.server.occ.mock"),
	
	/**
	 * Specifies a decorator for the {@link de.pgalise.simulation.sensorFramework.output.Output} class,
	 * e.g. to send a start bit and end bit for each data tupel
	 * @return Class name of the decorator, 
	 * e.g. {@link de.pgalise.simulation.sensorFramework.output.decorator.OdysseusOutput} (default "")
	 */
	OUTPUT_DECORATOR("simulation.configuration.output.decorator");
	
	/**
	 * Set of all Identifier to iterate over them easily.
	 */
	public final static EnumSet<Identifier> ALL = EnumSet.of(
			SERVER_IP,
			SERVER_EJBD_PORT,
			SERVER_HTTP_PORT,
			SERVER_HOST,
			SENSOR_OUTPUT_SERVER,
			MOCK_PERSISTENCE_SERVICE,
			SCALE_TEST,
			SCALE_PATH,
			EJBD_PROTOCOL_ENABLED,
			MOCK_OPERATION_CENTER_CONTROLLER,
			MOCK_CONTROL_CENTER_CONTROLLER,
			OUTPUT_DECORATOR);
	
	private String name;
	
	Identifier(String propName) {
		this.name = propName;
	}
	
	/**
	 * 
	 * @return internal property name of the Identifier
	 */
	public String getName() {
		return name;
	}
}
