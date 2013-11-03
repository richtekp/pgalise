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
 
package de.pgalise.simulation.traffic;

import java.util.List;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.util.generic.function.Function;
import de.pgalise.util.graph.GraphVisualizer;

/**
 * An extension of the GraphVisualizer adding features especially for the traffic 
 * aspect of the graph.
 *  
 * @param <N> 
 * @param <E> 
 * @param <D> 
 * @param <V> 
 * @author Marina, Mustafa
 */
public interface TrafficVisualizer<N extends TrafficNode<N,E,D,V>, E extends TrafficEdge<N,E,D,V>, D extends VehicleData, V extends Vehicle<D,N,E,V>> extends GraphVisualizer<TrafficGraph<N,E,D,V>,N,E> {

	public void setVehicles(List<V> vehicles);

	public List<V> getVehicles();

	/**
	 * The window of this TrafficVisualizer has to run in the main thread. To provide flowing animations you have to
	 * update the vehicles on a separate thread. This function relieves you of this task. Each command added here will
	 * be executed on a separate thread.
	 * 
	 * @param command
	 *            task to run on a separate thread, e.g. to update the vehicle's positions
	 */
	public void addCommand(Function command);

	/**
	 * Invokes the commands given by {@link #addCommand(Function)}.
	 * 
	 * @see #addCommand(Function)
	 */
	public void start();

	/**
	 * Tests if all commands given by {@link #addCommand(Function)} have been finished.
	 * 
	 * @return true if all commands have been finished otherwise false
	 */
	public boolean finishedCommands();
}
