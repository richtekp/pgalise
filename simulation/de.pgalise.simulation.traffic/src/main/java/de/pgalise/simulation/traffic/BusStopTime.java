/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.BusStop;
import de.pgalise.simulation.traffic.BusTrip;
import java.util.Date;

/**
 *
 * @param <S> 
 * @param <T> 
 * @author richter
 */
public interface BusStopTime<S extends BusStop<?>, T extends BusTrip<?,?>> {
	

	public T getBusTrip();

	public void setBusTrip(T busTrip) ;

	public S getStopId() ;

	public void setStopId(S busStop) ;

	public Date getArrivalTime() ;

	public void setArrivalTime(Date arrivalTime) ;

	public Date getDepartureTime() ;

	public void setDepartureTime(Date departureTime) ;

	public int getStopSequence() ;

	public void setStopSequence(int stopSequence) ;

	public String getPickupTime() ;

	public void setPickupTime(String pickupTime) ;

	public String getStopHeadsign() ;

	public void setStopHeadsign(String stopHeadsign) ;

	public String getDropOffType() ;

	public void setDropOffType(String dropOffType) ;

	public String getShapeDistTraveled() ;

	public void setShapeDistTraveled(String shapeDistTraveled) ;	
	
}
