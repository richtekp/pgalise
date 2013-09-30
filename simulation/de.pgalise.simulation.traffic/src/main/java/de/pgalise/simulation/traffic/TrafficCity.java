/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 *
 * @param <D> 
 * @param <N> 
 * @param <E> 
 * @param <V> 
 * @param <G> 
 * @author richter
 */
public interface  TrafficCity<
	D extends VehicleData,
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	V extends Vehicle<D,N,E,V>,
	G extends TrafficGraph<N,E,D,V>
> extends City {
	
	G getTrafficGraph();
	
	void setTrafficGraph(G graph);
}
