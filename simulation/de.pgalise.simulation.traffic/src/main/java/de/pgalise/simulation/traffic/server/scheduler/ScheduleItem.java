/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.scheduler;

import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 *
 * @author richter
 */
public interface ScheduleItem<
	D extends VehicleData,
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	V extends Vehicle<D,N,E,V>> {
<<<<<<< HEAD

=======
	
>>>>>>> ejb_skip

	/**
	 * The time the vehicle should be departured.
	 * 
	 * @return
	 */
	public long getDepartureTime() ;

	public void setDepartureTime(long time) ;

	public V getVehicle();

	/**
	 * @return the time the vehicle of this items has been updated
	 */
	public long getLastUpdate();

	public void setLastUpdate(long lastUpdate) ;

	/**
	 * First time the vehicle is driving.
	 * 
	 * @return
	 */
	public long getScheduleTime() ;

	public void setScheduleTime(long time) ;
}
