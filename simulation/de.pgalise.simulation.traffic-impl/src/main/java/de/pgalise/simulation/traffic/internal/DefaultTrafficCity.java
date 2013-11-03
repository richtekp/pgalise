/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.shared.city.DefaultCity;
import de.pgalise.simulation.traffic.TrafficCity;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import javax.persistence.OneToOne;

/**
 *
 * @param <D> 
 * @author richter
 */
public class DefaultTrafficCity<D extends VehicleData> extends DefaultCity implements TrafficCity<
	D, DefaultTrafficNode<D>, 
	DefaultTrafficEdge<D>, 
	BaseVehicle<D>, 
	DefaultTrafficGraph<D>> {
	private static final long serialVersionUID = 1L;

	@OneToOne
	private DefaultTrafficGraph<D> graph;

	@Override
	public void setTrafficGraph(DefaultTrafficGraph<D> graph) {
		this.graph = graph;
	}

	@Override
	public DefaultTrafficGraph<D> getTrafficGraph() {
		return graph;
	}
}
