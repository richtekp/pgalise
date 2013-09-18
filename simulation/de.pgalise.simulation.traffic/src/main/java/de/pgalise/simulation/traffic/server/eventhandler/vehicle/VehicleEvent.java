/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler.vehicle;

import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.traffic.TrafficEvent;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import java.util.Map;
import org.graphstream.graph.Graph;

/**
 *
 * @author richter
 */
public interface VehicleEvent extends Event<VehicleEventType> {

	Vehicle<? extends VehicleData> getVehicle()  ;

	long getSimulationTime()  ;

	long getElapsedTime()  ;

	ServiceDictionary getServiceDictionary() ;

	/**
	 * @return shallow copy of currently driving vehicles
	 */
	Scheduler getDrivingVehicles() ;

	VehicleEventType getTargetEventType() ;

	Graph getGraph()  ;

	TrafficGraphExtensions getTrafficGraphExtensions()  ;
	
	Map<Long, TrafficEvent> getEventForVehicleMap()  ;
	
	TrafficServerLocal getServer() ;
	
}
