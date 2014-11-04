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
package de.pgalise.simulation.internal;

import de.pgalise.simulation.sensorFramework.Sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.FrontController;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import java.util.Set;
import javax.ejb.Stateful;

/**
 * The Front Controller exists on every server and inits the
 * {@link SensorRegistry} and the {@link ServiceDictionary}.
 *
 * @author Mustafa
 */
@Stateful
public class DefaultFrontController extends AbstractController<Event, StartParameter, InitParameter> implements
	FrontController {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(
		DefaultFrontController.class);
	private static final String NAME = "FrontController";
	private static final long serialVersionUID = 1L;

	private Set<Sensor> sensorRegistry;

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
	}

	@Override
	protected void onReset() {
		sensorRegistry.clear();
	}

	@Override
	protected void onStart(StartParameter param) {
	}

	@Override
	protected void onStop() {
		for (Sensor<?, ?> sensor : sensorRegistry) {
			sensor.setActivated(false);
		}
	}

	@Override
	protected void onUpdate(EventList simulationEventList) {
		for (Sensor<?, ?> sensor : sensorRegistry) {
			sensor.update(simulationEventList);
		}
	}

	@Override
	protected void onResume() {
		for (Sensor<?, ?> sensor : sensorRegistry) {
			sensor.setActivated(true);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
