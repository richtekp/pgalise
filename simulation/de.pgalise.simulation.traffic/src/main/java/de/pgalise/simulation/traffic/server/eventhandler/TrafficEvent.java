/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler;

import de.pgalise.simulation.shared.event.Event;
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
public interface TrafficEvent<E extends TrafficEvent> extends Event {
	
	TrafficServerLocal<E> getResponsibleServer() ;
	
	void setResponsibleServer(TrafficServerLocal<E> responsibleServer);
	
	long getSimulationTime()  ;

	long getElapsedTime()  ;
}
