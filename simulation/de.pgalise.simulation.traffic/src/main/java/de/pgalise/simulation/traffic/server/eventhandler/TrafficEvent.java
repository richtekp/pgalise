/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler;

import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;

/**
 *
 * @author richter
 */
public interface TrafficEvent extends Event {
	
	TrafficServerLocal getResponsibleServer() ;
	
	void setResponsibleServer(TrafficServerLocal responsibleServer);
}
