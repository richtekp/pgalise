/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface TrafficNode extends NavigationNode {
	
	
	/**
	 * legacy (should be removable)
	 * @return 
	 */
	boolean isOnStreet();
	
	boolean isOnJunction();
	
	TrafficRule getTrafficRule();
	
	void setTrafficRule(TrafficRule trafficRule);
	
	Set<Vehicle<?>> getVehicles();
	
	void setVehicles(Set<Vehicle<?>> vehicles);
	
	Set<StaticTrafficSensor> getSensors();
	
	void setSensors(Set<StaticTrafficSensor> sensors);
}
