/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler.vehicle;

import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.city.TrafficGraph;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import java.util.Map;

/**
 * An event concerning one vehicle (general traffic event are described using {@link TrafficEvent}
 * @param <D> 
 * @author richter
 */
public interface VehicleEvent<D extends VehicleData> extends TrafficEvent {
	
	Vehicle<D> getVehicle()  ;

	ServiceDictionary getServiceDictionary() ;

	/**
	 * @return shallow copy of currently driving vehicles
	 */
	Scheduler getDrivingVehicles() ;

	TrafficGraph<?> getGraph()  ;

	TrafficGraphExtensions getTrafficGraphExtensions()  ;
	
	Map<Long, AbstractTrafficEvent> getEventForVehicleMap()  ;
	
}
