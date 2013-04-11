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
 
package de.pgalise.simulation.internal.visualizationcontroller;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.configReader.Identifier;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;
import de.pgalise.simulation.visualizationcontroller.OperationCenterControllerLoader;
/**
 * Default implementation of {@link OperationCenterControllerLoader}.
 * If 'simulation.configuration.server.oc.mock=true' is set, it will return
 * the EJB for "de.pgalise.simulation.internal.visualizationcontroller.OperationCenterControllerMock",
 * otherwise "de.pgalise.simulation.visualizationcontroller.OperationCenterController".
 * @author Timo
 */
@Lock(LockType.READ)
@Local
@Singleton(name = "de.pgalise.simulation.visualizationcontroller.OperationCenterController")
public class DefaultOperationCenterControllerLoader implements OperationCenterControllerLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOperationCenterControllerLoader.class);
	
	@EJB(name="ejb/de.pgalise.simulation.internal.visualizationcontroller.OperationCenterControllerMock")
	private OperationCenterController occMock;
	
	@EJB(name="ejb/de.pgalise.simulation.visualizationcontroller.OperationCenterController")
	private OperationCenterController occ;
	
	@EJB
	private ConfigReader configReader;
	
	/**
	 * Default
	 */
	public DefaultOperationCenterControllerLoader() {}
	
	@Override
	public OperationCenterController loadOperationCenterController() {
		String loadMock = this.configReader.getProperty(Identifier.MOCK_OPERATION_CENTER_CONTROLLER);
		if(loadMock != null && loadMock.equalsIgnoreCase("true")) {
			LOGGER.debug("Load mocked operation center controller.");
			return occMock;
		} 

		return occ;
	}
}
