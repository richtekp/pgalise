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
 
package de.pgalise.simulation.traffic.server.jam;

import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import java.util.List;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.util.Set;

/**
 * Strategy pattern to find vehicles surrounding a specific one.
 * 
 * @author Marina, Mustafa
 */
public interface SurroundingCarsFinder<
	D extends VehicleData,
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	V extends Vehicle<D,N,E,V>> {

	/**
	 * Returns a list of vehicles which surround the given car.
	 * 
	 * @param car
	 * @return list of vehicles
	 */
	public V findCars(V car, long time);

	/**
	 * Returns the closest car to the given car.
	 * 
	 * @param surroundingCars
	 * @param car
	 * @return nearest car
	 */
	public V findNearestCar(V car, long time);
}
