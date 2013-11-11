/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Experimental simulation facilities
 * @param <N> 
 * @param <E> 
 * @param <D> 
 * @param <V> 
 * @author richter
 */
public abstract class SelfManagingTrafficEdge extends TrafficEdge {

	protected SelfManagingTrafficEdge() {
	}
	
	/**
	 * both indicates whether there's possibilty (i.e. space) to take a vehicle on
	 * this edge and takes this vehicle on the edge. Sets the current edge of the vehicle to this instance and the current position to the source of the edge.<br/>
	 * Adjusts the velocity of the vehicle according to constraints of the implementation.
	 *
	 * @param vehicle
	 * @param timestamp 
	 * @return
	 */
	public abstract boolean takeVehicle(Vehicle vehicle, long timestamp);

	/**
	 * retrieves vehicles which left the edge on the last simulation step. This is
	 * not intended to be indicate the vehicles which have left the edge towards
	 * the next edge in the current route.
	 *
	 * @return
	 */
	public abstract Set<Vehicle> getLeavingVehicles();
	
	/**
	 * updates all {@link Vehicle}s on this edge to be in the situation which has 
	 * been up to date at <tt>timestamp</tt>
	 * @param timestamp
	 * @return a <tt>List</tt> of passed {@link NavigationEdge}s 
	 */
	public abstract Map<Vehicle, List<TrafficEdge>> updateVehicles(long timestamp);
	
}
