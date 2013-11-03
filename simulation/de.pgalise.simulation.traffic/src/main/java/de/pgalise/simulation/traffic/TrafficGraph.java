/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import org.jgrapht.DirectedGraph;

/**
 *
 * @author richter
 */
public interface TrafficGraph<N extends TrafficNode<N,E,D,V>, E extends TrafficEdge<N,E,D,V>, D extends VehicleData, V extends Vehicle<D,N,E,V>> extends Identifiable, DirectedGraph<N, E> {
	
	
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
	public Identifiable getElementClosestTo(Coordinate position, double distanceTolerance) ;
	
	public N getNodeClosestTo(Coordinate position);
	
	public N getNodeClosestTo(Coordinate position, int distanceTolerance) ;

	/**
	 * 
	 * @return 
	 */
	public Geometry calulateBoundaries() ;
}
