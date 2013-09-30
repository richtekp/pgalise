/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler.vehicle;

import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import java.util.Map;

/**
 * An event concerning one vehicle (general traffic event are described using {@link TrafficEvent}
 * @param <D> 
 * @param <N> 
 * @param <E> 
 * @param <V> 
 * @param <F> 
 * @author richter
 */
public interface VehicleEvent<
	D extends VehicleData, 
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	V extends Vehicle<D,N,E,V>, 
	F extends TrafficEvent<N,E,D,V,F>
> {
	
	V getVehicle()  ;

	ServiceDictionary getServiceDictionary() ;

	/**
	 * @return shallow copy of currently driving vehicles
	 */
	Scheduler<D, N, E, V, ScheduleItem<D, N, E, V>> getDrivingVehicles() ;

	TrafficGraph<N,E,D,V> getGraph()  ;

	TrafficGraphExtensions<N, E, D, V> getTrafficGraphExtensions()  ;
	
	Map<Long, F> getEventForVehicleMap()  ;
	
}
