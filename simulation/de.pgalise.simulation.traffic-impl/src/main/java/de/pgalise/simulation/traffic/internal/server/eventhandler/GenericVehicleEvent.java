/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.event.AbstractVehicleEvent;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;

/**
 * This class encapsultes the different types of {@link VehicleEvent}s which are not represented as subclasses (like {@link TrafficEvent}, but only by their type attribute. This should change in the futures.
 * @param <D> 
 * @author richter
 */
public class GenericVehicleEvent<D extends VehicleData,N extends TrafficNode, E extends TrafficEdge<N,E>> extends AbstractVehicleEvent<D,N,E>{
	private static final long serialVersionUID = 1L;
	private EventType type;

	public GenericVehicleEvent(TrafficServerLocal server,
		long simulationTime,
		long elapsedTime,
		Vehicle<D,N,E> vehicles,
		EventType type) {
		super(server,
			simulationTime,
			elapsedTime,
			vehicles);
		this.type = type;
	}

	@Override
	public EventType getType() {
		return type;
	}
	
}
