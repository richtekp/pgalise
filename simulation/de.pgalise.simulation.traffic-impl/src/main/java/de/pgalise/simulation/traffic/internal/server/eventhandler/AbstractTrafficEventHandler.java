/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
import de.pgalise.simulation.traffic.internal.DefaultTrafficEdge;
import de.pgalise.simulation.traffic.internal.DefaultTrafficNode;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.internal.server.DefaultTrafficServer;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;

/**
 *
 * @param <E> 
 * @author richter
 */
public class AbstractTrafficEventHandler<D extends VehicleData> implements TrafficEventHandler<AbstractTrafficEvent<D>, DefaultTrafficNode<D>, DefaultTrafficEdge<D>, D, BaseVehicle<D>> {
	/**
	 * Traffic server
	 */
	private TrafficServerLocal<E> responsibleServer;

	@Override
	public void init(TrafficServerLocal<E>  server) {
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
	public TrafficServerLocal<E>  getResponsibleServer() {
		return responsibleServer;
	}

	public void setResponsibleServer(TrafficServerLocal<E>  responsibleServer) {
		this.responsibleServer = responsibleServer;
	}
	
}
