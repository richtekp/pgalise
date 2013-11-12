/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.shared.persistence.Identifiable;
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
public abstract class TrafficGraph extends DefaultDirectedGraph<TrafficNode, TrafficEdge>{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	public TrafficGraph(EdgeFactory<TrafficNode,TrafficEdge> edgeFactory) {
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
	public Identifiable getElementClosestTo(Coordinate position, double distanceTolerance) {
		//@TODO: remove brute force search
		TrafficNode minDistanceNode = null;
		double minDistance = Double.MAX_VALUE;
		for(TrafficNode node : this.vertexSet()) {
			double distance = node.getGeoLocation().distance(position);
			if(distance <= distanceTolerance && distance < minDistance) {
				minDistanceNode = node;
			}
		}
		if(minDistanceNode != null) {
			return minDistanceNode;
		}
		minDistance = Double.MAX_VALUE;
		TrafficEdge minDistanceEdge = null;
		for(TrafficEdge edge : this.edgeSet()) {
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
	
	public TrafficNode getNodeClosestTo(Coordinate position) {
		return getNodeClosestTo(position,
			Integer.MAX_VALUE);
	}
	
	public TrafficNode getNodeClosestTo(Coordinate position, int distanceTolerance) {
		
		TrafficNode minDistanceNode = null;
		double minDistance = Double.MAX_VALUE;
		for(TrafficNode node : this.vertexSet()) {
			double distance = node.getGeoLocation().distance(position);
			if(distance <= distanceTolerance && distance < minDistance) {
				minDistanceNode = node;
			}
		}
		return minDistanceNode;
	}

	@Id
	public Long getId() {
		return this.id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	@OneToMany
	public Set<TrafficEdge> getEdgeSet() {
		return super.edgeSet(); 
	}
	
	public void setEdgeSet(Set<TrafficEdge> edgeSet) {
		super.edgeSet().addAll(edgeSet);
	}

	@OneToMany
	public Set<TrafficNode> getVertexSet() {
		return super.vertexSet(); 
	}
	
	public void setVertexSet(Set<TrafficNode> vertexSet) {
		super.vertexSet().addAll(vertexSet);
	}

	/**
	 * 
	 * @return 
	 */
	public Geometry calulateBoundaries() {
		throw new UnsupportedOperationException();
	}
	
	private static class NavigationEdgeFactory<Y extends TrafficNode, X extends TrafficEdge, Z extends VehicleData> implements EdgeFactory<Y, X> {
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
