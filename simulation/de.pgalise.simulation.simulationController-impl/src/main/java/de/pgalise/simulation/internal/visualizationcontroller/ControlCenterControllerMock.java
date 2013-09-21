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

import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;

import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;
/**
 * Mock for {@link ControlCenterController}. It does not send anything to the control center.
 * @author Timo
 */
@Lock(LockType.READ)
@Local
@Stateless(name = "de.pgalise.simulation.internal.visualizationcontroller.ControlCenterControllerMock")
public class ControlCenterControllerMock extends AbstractController implements ControlCenterController {

	/**
	 * Default
	 */
	public ControlCenterControllerMock() {}
	
	@Override
	public String getName() {
		return ControlCenterControllerMock.class.getName();
	}

	@Override
	public void displayException(Exception exception)
			throws IllegalStateException {	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {}

	@Override
	protected void onReset() {}

	@Override
	protected void onStart(StartParameter param) {}

	@Override
	protected void onStop() {}

	@Override
	protected void onResume() {}

	@Override
	protected void onUpdate(EventList simulationEventList) {}
}
