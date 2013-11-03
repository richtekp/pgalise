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
 
package de.pgalise.simulation.traffic.server.eventhandler.vehicle;

import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;

/**
 * Handler for the events being fired during the update of a specific vehicle.

* @param <D> 
* @param <N> 
* @param <E> 
* @param <F> 
* @param <V> 
* @see VehicleEventType
 * @see VehicleEvent
 * @author mustafa
 *
 */
public interface VehicleEventHandler<
	D extends VehicleData, 
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	F extends VehicleEvent<D,N,E,V,F>, 
	V extends Vehicle<D,N,E,V>
> extends TrafficEventHandler<F,N,E,D,V> {
	
}
