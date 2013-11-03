/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 *
 * @param <N> 
 * @param <E> 
 * @param <D> 
 * @param <V> 
 * @param <F> 
 * @author richter
 */
public interface OSMNavigationNode<
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	D extends VehicleData, 
	V extends Vehicle<D, N,E,V>
> extends TrafficNode<N,E,D,V> {
	
	String getOSMId();
}