/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;

/**
 *
 * @param <D> 
 * @author richter
 */
public class AbstractTrafficEventHandler<D extends VehicleData,E extends TrafficEvent> implements TrafficEventHandler<E> {
	/**
	 * Traffic server
	 */
	private TrafficServerLocal<E> responsibleServer;
	
	private Output output;

	public Output getOutput() {
		return output;
	}

	public void setOutput(Output output) {
		this.output = output;
	}

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
