/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.Way;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.util.Set;

/**
 *
 * @author richter
 * @param <N>
 * @param <E>
 * @param <D>
 * @param <V>
 */
public interface TrafficEdge<N extends TrafficNode<N,E,D,V>, E extends TrafficEdge<N,E,D,V>, D extends VehicleData, V extends Vehicle<D,N,E,V>> extends NavigationEdge<N,E> {
	

	Set<V> getVehicles();
	
	void setVehicles(Set<V> vehicles);
	
	/**
	 * 
	 * @return oncoming traffic edge or <code>null</code> if this is a oneway edge
	 */
	E getOncomingTrafficEdge();
	
	/**
	 * 
	 * @return <code>true</code> if the oncoming traffic edge can be reached from this edge and has no physical barrier between
	 */
	boolean isOncomingTrafficEdgeReachable();
	
	/**
	 * <tt>-1</tt> indicates that the value is not set
	 * @return 
	 */
	double getMaxSpeed();
	
	void setMaxSpeed(double maxSpeed);
	
	Way<?,?> getWay();
	
	boolean isPriorityRoad();
	
	void setPriorityRoad(boolean priorityRoad);
	
	boolean isCarsAllowed();
	
	void setCarsAllowed(boolean carsAllowed);
	
	boolean isBicyclesAllowed();
	
	void setBicyclesAllowed(boolean bicyclesAllowed);
	
	/**
	 * <tt>-1</tt> indicates that the value has not been set
	 * @return 
	 */
	int getGrossVehicleWeight();
	
	void setGrossVehicleWeight(int grossVehicleWeight);
}
