/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler;

import de.pgalise.simulation.service.event.EventHandlerManager;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 *
 * @param <H> 
 * @param <F> 
 * @param <N> 
 * @param <E> 
 * @param <D> 
 * @param <V> 
 * @author richter
 */
public interface TrafficEventHandlerManager<
	H extends TrafficEventHandler<F,N,E,D,V>, 
	F extends TrafficEvent<N,E,D,V,F>, 
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	D extends VehicleData, 
	V extends Vehicle<D,N,E,V>
> extends EventHandlerManager<H, F> {
	
}
