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
 
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.traffic.internal.DefaultBusTrip;
import de.pgalise.simulation.traffic.internal.DefaultBusStop;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.traffic.BusStopTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * 
 * @author marcus
 *
 */
@Entity
//@Table(name = "PGALISE.BUS_STOP_TIMES")
public class DefaultBusStopTime extends AbstractIdentifiable implements BusStopTime<DefaultBusStop, DefaultBusTrip>{
	private static final long serialVersionUID = 1L;
	
	@JoinColumn(name = "TRIP_ID")
	@OneToOne
	private DefaultBusTrip busTrip;
	
	@JoinColumn(name = "STOP_ID")
	@OneToOne
	private DefaultBusStop busStop;
	
	@Column(name = "ARRIVAL_TIME")
	@Temporal(TemporalType.TIME)
	private Date arrivalTime;
	
	@Column(name = "DEPARTURE_TIME")
	@Temporal(TemporalType.TIME)
	private Date departureTime;
	
	@Column(name = "STOP_SEQUENCE")
	private int stopSequence;
	
	@Column(name = "PICKUP_TYPE")
	private String pickupTime;
	
	@Column(name = "STOP_HEADSIGN")
	private String stopHeadsign;
	
	@Column(name = "DROP_OFF_TYPE")
	private String dropOffType;
	
	@Column(name = "SHAPE_DIST_TRAVELED")
	private String shapeDistTraveled;
	
	public DefaultBusStopTime() {}

	public DefaultBusStopTime(DefaultBusTrip trip,
			DefaultBusStop busStop, Date arrivalTime, Date departureTime,
			int stopSequence, String pickupTime, String stopHeadsign,
			String dropOffType, String shapeDistTraveled) {
		this.busTrip = trip;
		this.busStop = busStop;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.stopSequence = stopSequence;
		this.pickupTime = pickupTime;
		this.stopHeadsign = stopHeadsign;
		this.dropOffType = dropOffType;
		this.shapeDistTraveled = shapeDistTraveled;
	}

	public DefaultBusTrip getBusTrip() {
		return busTrip;
	}

	public void setBusTrip(DefaultBusTrip busTrip) {
		this.busTrip = busTrip
;	}

	public DefaultBusStop getStopId() {
		return busStop;
	}

	public void setStopId(DefaultBusStop busStop) {
		this.busStop = busStop;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public int getStopSequence() {
		return stopSequence;
	}

	public void setStopSequence(int stopSequence) {
		this.stopSequence = stopSequence;
	}

	public String getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(String pickupTime) {
		this.pickupTime = pickupTime;
	}

	public String getStopHeadsign() {
		return stopHeadsign;
	}

	public void setStopHeadsign(String stopHeadsign) {
		this.stopHeadsign = stopHeadsign;
	}

	public String getDropOffType() {
		return dropOffType;
	}

	public void setDropOffType(String dropOffType) {
		this.dropOffType = dropOffType;
	}

	public String getShapeDistTraveled() {
		return shapeDistTraveled;
	}

	public void setShapeDistTraveled(String shapeDistTraveled) {
		this.shapeDistTraveled = shapeDistTraveled;
	}	
	
	
}
