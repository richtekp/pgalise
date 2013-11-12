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
 
package de.pgalise.simulation.traffic.server.eventhandler;

import de.pgalise.simulation.service.event.EventHandler;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;

/**
 * A TrafficHandler handles a specific type of a TrafficEvent 
 * raised by the user of the simulation. 
 * 
 * @param <F> 
 * @param <N> 
 * @param <E> 
 * @param <D> 
 * @param <V> 
 * @author Mustafa
 * @version 1.0 (Mar 21, 2013)
 */
public interface TrafficEventHandler<
	F extends TrafficEvent
> extends EventHandler<F> {

	/**
	 * Inits the event handler
	 * 
	 * @param server
	 *            Corresponding {@link TrafficServer}
	 */
	void init(TrafficServerLocal<F>  server);
	
	TrafficServerLocal<F>  getResponsibleServer();
}
