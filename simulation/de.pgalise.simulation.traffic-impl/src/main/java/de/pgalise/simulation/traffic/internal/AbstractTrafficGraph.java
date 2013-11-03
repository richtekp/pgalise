/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.util.Set;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 *
 * @param <N> 
 * @param <E> 
 * @param <D> 
 * @param <V> 
 * @author richter
 */
public class AbstractTrafficGraph<N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	D extends VehicleData, 
	V extends Vehicle<D,N,E,V>> 
extends DefaultDirectedGraph<N, E> implements TrafficGraph<N,E,D,V> {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	public AbstractTrafficGraph(EdgeFactory<N,E> edgeFactory) {
		super(edgeFactory);
	}
	
	/**
	 * 
	 * @param position
	 * @param distanceTolerance can be set to {@link Double#MAX_VALUE} in order to ignore
	 * @return <ol><li> a {@link NavigationNode} if the distance to a node in 
	 * this graph is <= <tt>distanceTolerance</tt></li>
	 * <li>a {@link NavigationEdge} if the distance to an edge in this 
	 * graph is less than <tt>distanceTolerance</tt>	and 1. doesn't apply</li>
	 * <li><code>null</code> if 1. and 2. don't apply
	 */
	@Override
	public Identifiable getElementClosestTo(Coordinate position, double distanceTolerance) {
		//@TODO: remove brute force search
		N minDistanceNode = null;
		double minDistance = Double.MAX_VALUE;
		for(N node : this.vertexSet()) {
			double distance = node.getGeoLocation().distance(position);
			if(distance <= distanceTolerance && distance < minDistance) {
				minDistanceNode = node;
			}
		}
		if(minDistanceNode != null) {
			return minDistanceNode;
		}
		minDistance = Double.MAX_VALUE;
		E minDistanceEdge = null;
		for(E edge : this.edgeSet()) {
			Coordinate node0Position = edge.getSource().getGeoLocation();
			Coordinate node1Position = edge.getTarget().getGeoLocation();
			Coordinate node0Coordinates = new Coordinate(node0Position.x, node0Position.y);
			Coordinate node1Coordinates = new Coordinate(node1Position.x, node1Position.y);
			Coordinate positionCoordines = new Coordinate(position.x, position.y);
			double distance = CGAlgorithms.distancePointLine(positionCoordines, node0Coordinates, node1Coordinates);
			if(distance <= distanceTolerance && distance < minDistance) {
				minDistanceEdge = edge;
			}
		}
		return minDistanceEdge; //if it is null it fulfills the last condition;
	}
	
	@Override
	public N getNodeClosestTo(Coordinate position) {
		return getNodeClosestTo(position,
			Integer.MAX_VALUE);
	}
	
	@Override
	public N getNodeClosestTo(Coordinate position, int distanceTolerance) {
		
		N minDistanceNode = null;
		double minDistance = Double.MAX_VALUE;
		for(N node : this.vertexSet()) {
			double distance = node.getGeoLocation().distance(position);
			if(distance <= distanceTolerance && distance < minDistance) {
				minDistanceNode = node;
			}
		}
		return minDistanceNode;
	}

	@Override	
	@Id
	public Long getId() {
		return this.id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	@OneToMany
	public Set<E> getEdgeSet() {
		return super.edgeSet(); 
	}
	
	public void setEdgeSet(Set<E> edgeSet) {
		super.edgeSet().addAll(edgeSet);
	}

	@OneToMany
	public Set<N> getVertexSet() {
		return super.vertexSet(); 
	}
	
	public void setVertexSet(Set<N> vertexSet) {
		super.vertexSet().addAll(vertexSet);
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	public Geometry calulateBoundaries() {
		throw new UnsupportedOperationException();
	}
	
	private static class NavigationEdgeFactory<Y extends DefaultTrafficNode<Z>, X extends DefaultTrafficEdge<Z>, Z extends VehicleData> implements EdgeFactory<Y, X> {
		private Class<? extends X> edgeClass;

		NavigationEdgeFactory(Class<X> edgeClass) {
			this.edgeClass = edgeClass;
		}

		@Override
		public X createEdge(Y sourceVertex, Y targetVertex) {
			X retValue;
			try {
				retValue = edgeClass.newInstance();
			} catch (InstantiationException | IllegalAccessException ex) {
				throw new RuntimeException(ex);
			}
			retValue.setSource(sourceVertex);
			retValue.setTarget(targetVertex);
			return retValue;
		}
	}
}
