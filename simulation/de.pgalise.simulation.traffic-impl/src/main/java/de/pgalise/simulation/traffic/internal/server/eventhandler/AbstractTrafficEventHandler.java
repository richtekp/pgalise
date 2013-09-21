/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;

/**
 *
 * @param <E> 
 * @author richter
 */
public class AbstractTrafficEventHandler<E extends TrafficEvent> implements TrafficEventHandler<E> {
	/**
	 * Traffic server
	 */
	private TrafficServerLocal responsibleServer;

	@Override
	public void init(TrafficServerLocal server) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public EventType getTargetEventType() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void handleEvent(
		E event) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public TrafficServerLocal getResponsibleServer() {
		return responsibleServer;
	}

	public void setResponsibleServer(TrafficServerLocal responsibleServer) {
		this.responsibleServer = responsibleServer;
	}
	
}
