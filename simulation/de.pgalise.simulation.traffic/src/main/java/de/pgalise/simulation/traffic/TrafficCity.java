/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.traffic.TrafficCity;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import javax.persistence.OneToOne;

/**
 *
 * @param <D> 
 * @author richter
 */
public class TrafficCity<D extends VehicleData> extends City  {
	private static final long serialVersionUID = 1L;

	@OneToOne
	private TrafficGraph graph;

	public void setTrafficGraph(TrafficGraph graph) {
		this.graph = graph;
	}

	public TrafficGraph getTrafficGraph() {
		return graph;
	}
}
