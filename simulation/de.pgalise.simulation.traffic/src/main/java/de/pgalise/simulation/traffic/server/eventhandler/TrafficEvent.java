/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler;

import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;

/**
 *
 * @author richter
 * @param <N>
 * @param <E>
 * @param <D>
 * @param <V>
 * @param <F>  
 */
public interface TrafficEvent<
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>,
	D extends VehicleData, 
	V extends Vehicle<D,N,E,V>, 
	F extends TrafficEvent<N,E,D,V,F>
> extends Event {
	
	TrafficServerLocal<F,N,E,D,V> getResponsibleServer() ;
	
	void setResponsibleServer(TrafficServerLocal<F,N,E,D,V> responsibleServer);
	
	long getSimulationTime()  ;

	long getElapsedTime()  ;
}
