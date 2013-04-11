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
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;
import de.pgalise.simulation.visualizationcontroller.ControlCenterControllerLoader;

/**
 * Default implementation of {@link ControlCenterControllerLoader}.
 * If 'simulation.configuration.server.cc.mock=true' is set, it will return
 * the EJB for "de.pgalise.simulation.internal.visualizationcontroller.ControlCenterControllerMock",
 * otherwise "de.pgalise.simulation.visualizationcontroller.ControlCenterController".
 * @author Timo
 */
@Lock(LockType.READ)
@Local
@Singleton(name = "de.pgalise.simulation.visualizationcontroller.ControlCenterControllerLoader")
public class DefaultControlCenterControllerLoader implements ControlCenterControllerLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultControlCenterControllerLoader.class);
	
	@EJB(name="ejb/de.pgalise.simulation.internal.visualizationcontroller.ControlCenterControllerMock")
	private ControlCenterController cccMock;
	
	@EJB(name="ejb/de.pgalise.simulation.visualizationcontroller.ControlCenterController")
	private ControlCenterController ccc;
	
	@EJB
	private ConfigReader configReader;
	
	/**
	 * Default
	 */
	public DefaultControlCenterControllerLoader() {}

	@Override
	public ControlCenterController loadControlCenterController() {
		String loadMock = this.configReader.getProperty(Identifier.MOCK_CONTROL_CENTER_CONTROLLER);
		if(loadMock != null && loadMock.equalsIgnoreCase("true")) {
			LOGGER.debug("Load mocked control center controller.");
			return cccMock;
		} 
		return ccc;
	}

}
