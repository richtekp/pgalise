/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;


import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.util.Set;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * a graph of {@link NavigationNode}s connected with {@link NavigationEdge}s. {@link DirectPosition2D}s are saved in a prefix tree in order to ensure search of closest nodes to arbitrary positions using {@link  #getElementClosestTo(org.geotools.geometry.DirectPosition2D, double) }.
 * A <tt>TrafficGraph</tt> does not handle vehicle management which is done by a {@link TrafficManager}.
 * @param <D> 
 * @author richter
 */
/*
 * implemenation notes:
 * - it's a lot of work to implement listening to boundary changes because every add and remove operation to the graph has to be listened to and almost every method has to be extended (edge and vertex sets are backed up by the graph). Therefore boundaries are always calculated and not updated at every add or remove operation.
 * - this is not an Entity for both practical and data redundancy reasons (Graph doesn't have a default constructor) (information is completely managed by nodes and edges), but it's an Identifiable nevertheless
 */
public class DefaultTrafficGraph<D extends VehicleData> extends DefaultDirectedGraph<DefaultTrafficNode<D>, DefaultTrafficEdge<D>> implements TrafficGraph<DefaultTrafficNode<D>, DefaultTrafficEdge<D>,D, BaseVehicle<D>> {
	private static final long serialVersionUID = 1L;
	
	private Long id;

	public DefaultTrafficGraph() {
		super(new NavigationEdgeFactory<>(DefaultTrafficEdge.class));
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
		DefaultTrafficNode<D> minDistanceNode = null;
		double minDistance = Double.MAX_VALUE;
		for(DefaultTrafficNode<D> node : this.vertexSet()) {
			double distance = node.getGeoLocation().distance(position);
			if(distance <= distanceTolerance && distance < minDistance) {
				minDistanceNode = node;
			}
		}
		if(minDistanceNode != null) {
			return minDistanceNode;
		}
		minDistance = Double.MAX_VALUE;
		DefaultTrafficEdge<D> minDistanceEdge = null;
		for(DefaultTrafficEdge<D> edge : this.edgeSet()) {
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
	public DefaultTrafficNode<D> getNodeClosestTo(Coordinate position) {
		return getNodeClosestTo(position,
			Integer.MAX_VALUE);
	}
	
	@Override
	public DefaultTrafficNode<D> getNodeClosestTo(Coordinate position, int distanceTolerance) {
		
		DefaultTrafficNode<D> minDistanceNode = null;
		double minDistance = Double.MAX_VALUE;
		for(DefaultTrafficNode<D> node : this.vertexSet()) {
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
	public Set<DefaultTrafficEdge<D>> getEdgeSet() {
		return super.edgeSet(); 
	}
	
	public void setEdgeSet(Set<DefaultTrafficEdge<D>> edgeSet) {
		super.edgeSet().addAll(edgeSet);
	}

	@OneToMany
	public Set<DefaultTrafficNode<D>> getVertexSet() {
		return super.vertexSet(); 
	}
	
	public void setVertexSet(Set<DefaultTrafficNode<D>> vertexSet) {
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
		private Class<X> edgeClass;

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
