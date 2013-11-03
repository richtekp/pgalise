/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import org.jgrapht.EdgeFactory;

/**
 *
 * @param <D> 
 * @author richter
 */
public class DefaultTrafficEdgeFactory<D extends VehicleData> implements EdgeFactory<DefaultTrafficNode<D>, DefaultTrafficEdge<D>> {

	@Override
	public DefaultTrafficEdge<D> createEdge(
		DefaultTrafficNode<D> sourceVertex,
		DefaultTrafficNode<D> targetVertex) {
		return new DefaultTrafficEdge<>(sourceVertex,
			targetVertex);
	}
	
}
