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
 
package de.pgalise.simulation.service.internal.event;

import javax.ejb.Local;
import javax.ejb.Stateless;

import de.pgalise.simulation.service.event.SimulationEventHandler;
import de.pgalise.simulation.service.event.SimulationEventHandlerManager;
import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;

/**
 * Default implementation of the SimulationEventHandler
 * @author mustafa
 *
 */
@Local(SimulationEventHandlerManager.class)
@Stateless(name = "de.pgalise.simulation.service.event.SimulationEventHandlerManager")
public class DefaultSimulationEventHandlerManager extends
		AbstractEventHandlerManager<SimulationEventHandler, SimulationEvent, SimulationEventTypeEnum> implements
		SimulationEventHandlerManager {

	/**
	 * Default constructor
	 */
	public DefaultSimulationEventHandlerManager() {
		super();
	}

	@Override
	public boolean responsibleFor(SimulationEventHandler handler, SimulationEvent event) {
		return handler.getTargetEventType().equals(event.getType());
	}

}
