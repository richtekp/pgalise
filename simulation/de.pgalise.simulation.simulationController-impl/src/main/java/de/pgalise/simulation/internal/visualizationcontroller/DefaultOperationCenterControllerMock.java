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

import java.util.Collection;

import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;

import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;
/**
 * Mock for {@link OperationCenterController}. It does not send anything to the operation center.
 * @author Timo
 */
@Local
@Stateless(name = "de.pgalise.simulation.internal.visualizationcontroller.OperationCenterControllerMock")
public class DefaultOperationCenterControllerMock extends AbstractController<Event, StartParameter, InitParameter> implements OperationCenterControllerMock {
	private static final long serialVersionUID = 1L;

	/**
	 * Default
	 */
	public DefaultOperationCenterControllerMock() {}
	
	@Override
	public void displayException(Exception exception)
			throws IllegalStateException {}

	@Override
	public void createSensor(SensorHelper<?> sensor) throws SensorException {}

	@Override
	public void createSensors(Collection<SensorHelper<?>> sensors)
			throws SensorException {}

	@Override
	public void deleteSensor(SensorHelper<?> sensor) throws SensorException {}

	@Override
	public void deleteSensors(Collection<SensorHelper<?>> sensors)
			throws SensorException {}

	@Override
	public boolean statusOfSensor(SensorHelper<?> sensor) throws SensorException {
		return false;
	}

	@Override
	public String getName() {
		return DefaultOperationCenterControllerMock.class.getName();
	}

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
	protected void onUpdate(EventList<Event> simulationEventList) {}
}
