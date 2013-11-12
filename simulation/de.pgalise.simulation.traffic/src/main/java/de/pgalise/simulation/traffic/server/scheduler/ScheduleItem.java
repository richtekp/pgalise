/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.scheduler;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 *
 * @author richter
 */
public interface ScheduleItem {

	/**
	 * The time the vehicle should be departured.
	 * 
	 * @return
	 */
	public long getDepartureTime() ;

	public void setDepartureTime(long time) ;

	public Vehicle<?> getVehicle();

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
