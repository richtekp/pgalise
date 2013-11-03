/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.BusStop;
import de.pgalise.simulation.traffic.BusTrip;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import java.util.Date;

/**
 * 
 * @author marcus
 *
 */
public interface BusStopTime {
	
	@JoinColumn(name = "TRIP_ID")
	@OneToOne
	private BusTrip busTrip;
	
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
