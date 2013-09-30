/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import java.util.Set;

/**
 *
 * @param <N> 
 * @param <E> 
 * @param <D> 
 * @param <V> 
 * @author richter
 */
public interface TrafficNode<N extends TrafficNode<N,E,D,V>, E extends TrafficEdge<N,E,D,V>, D extends VehicleData, V extends Vehicle<D, N,E,V>> extends NavigationNode {
	
	
	/**
	 * legacy (should be removable)
	 * @return 
	 */
	boolean isOnStreet();
	
	boolean isOnJunction();
	
	TrafficRule<D, N, E, V> getTrafficRule();
	
	void setTrafficRule(TrafficRule<D,N,E,V> trafficRule);
	
	Set<V> getVehicles();
	
	void setVehicles(Set<V> vehicles);
	
	Set<StaticTrafficSensor<N,E>> getSensors();
	
	void setSensors(Set<StaticTrafficSensor<N,E>> sensors);
}
