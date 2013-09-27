/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Experimental simulation facilities
 * @param <N> 
 * @param <E> 
 * @author richter
 */
public interface SelfManagingNavigationEdge<N extends NavigationNode, E extends NavigationEdge<N,E>> extends NavigationEdge<N,E> {
	
	/**
	 * both indicates whether there's possibilty (i.e. space) to take a vehicle on
	 * this edge and takes this vehicle on the edge. Sets the current edge of the vehicle to this instance and the current position to the source of the edge.<br/>
	 * Adjusts the velocity of the vehicle according to constraints of the implementation.
	 *
	 * @param vehicle
	 * @param timestamp 
	 * @return
	 */
	boolean takeVehicle(Vehicle<?> vehicle, long timestamp);

	/**
	 * retrieves vehicles which left the edge on the last simulation step. This is
	 * not intended to be indicate the vehicles which have left the edge towards
	 * the next edge in the current route.
	 *
	 * @return
	 */
	Set<Vehicle<?>> getLeavingVehicles();
	
	/**
	 * updates all {@link Vehicle}s on this edge to be in the situation which has 
	 * been up to date at <tt>timestamp</tt>
	 * @param timestamp
	 * @return a <tt>List</tt> of passed {@link NavigationEdge}s 
	 */
	Map<Vehicle<?>, List<E>> updateVehicles(long timestamp);
	
}
