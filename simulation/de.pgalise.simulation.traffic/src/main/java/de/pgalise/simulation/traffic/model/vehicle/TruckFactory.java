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
 
package de.pgalise.simulation.traffic.model.vehicle;

import java.awt.Color;

import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;

/**
 * Interface to provide methods to create different types of {@link Truck}.
 * 
 * @author Andreas
 * @version 1.0
 */
public interface TruckFactory<N extends TrafficNode<N,E,TruckData,V>, E extends TrafficEdge<N,E, TruckData,V>, V extends Vehicle<TruckData, N,E,V>> {

	/**
	 * Method to create a {@link Truck} with the given typeId.
	 * 
	 * @param id
	 *            ID of the {@link Truck}
	 * @param typeId
	 *            ID of the {@link Truck} type
	 * @param color
	 *            Color of the {@link Truck}
	 * @param trailercount
	 *            Number of trailers
	 * @return created {@link Truck}
	 */
	public V createTruck(  Color color, int trailercount, SensorHelper gpsSensor);

	/**
	 * Method to create a random {@link Truck}.
	 * 
	 * @param id
	 *            ID of the {@link Truck}
	 * @return created {@link Truck}
	 */
	public V createRandomTruck( SensorHelper gpsSensor);
}
