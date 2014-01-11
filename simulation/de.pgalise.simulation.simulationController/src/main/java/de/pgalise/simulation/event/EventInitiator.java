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
 
package de.pgalise.simulation.event;

import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import java.util.List;

/**
 * An event initiator gives the tact of the simulation. It calls the
 * update method of the {@link SimulationComponent} controllers and
 * provides instances of {@link SimulationEvent}, which shall be
 * processed by the respective controllers. The events are encapsulated in a
 * {@link SimulationEventList} with the current timestamp. It's also possible, that
 * there is no current {@link SimulationEvent} in the given {@link SimulationEventList}, then
 * the {@link SimulationEventList} indicates only the progress of the simulation time.
 * 
 * @author Timo
 */
public interface EventInitiator extends Controller<Event, StartParameter, InitParameter> {

	/**
	 * Returns the event thread. Use this only for testing.
	 * 
	 * @return event thread
	 */
	public Thread getEventThread();
	
	/**
	 * Adds an event list.
	 * @param simulationEventList
	 */
	public void addSimulationEventList(EventList<?> simulationEventList);
	
	public EventList<?> getEventList();
	
	/**
	 * Returns the current timestamp of the simulation.
	 * @return
	 */
	public long getCurrentTimestamp();
	
	public void setFrontController(List<Controller<?,?,?>> frontController);

}
