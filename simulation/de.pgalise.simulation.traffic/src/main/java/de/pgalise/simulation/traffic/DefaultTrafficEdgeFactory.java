package de.pgalise.simulation.traffic;

import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import org.jgrapht.EdgeFactory;



/**
 *
 * @param <D> 
 * @author richter
 */
public class DefaultTrafficEdgeFactory<D extends VehicleData> implements EdgeFactory<TrafficNode, TrafficEdge> {
	
	@Override
	public TrafficEdge createEdge(
		TrafficNode sourceVertex,
		TrafficNode targetVertex) {
		return new TrafficEdge(sourceVertex, targetVertex);
	}
	
}
