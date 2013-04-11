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

import org.graphstream.graph.Graph;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * The implementation of the TrafficServer uses a model to simulate a traffic jam. 
 * To exchange this model easily an API has been formed, the TrafficJamModel.
 * 
 * @author Marina, Mustafa
 */
public interface TrafficJamModel {

	/**
	 * Sets a new {@link SurroundingCarsFinder}
	 * 
	 * @param finder
	 *            new {@link SurroundingCarsFinder}
	 */
	public void setSurroundingCarsFinder(SurroundingCarsFinder finder);

	/**
	 * Updates the position of a car with certain traffic jam rules.
	 * 
	 * @param vehicles
	 *            List of vehicles
	 * @param v
	 *            The referred vehicle
	 * @param time
	 *            The current time
	 * @param graph
	 *            The graph where the cars drive on
	 * @param probability
	 *            The probability for slowing down (e.g. 0.1 for 10%)
	 */
	public void update(Vehicle<? extends VehicleData> v, long time, Graph graph, double probability);
}
