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
 
package de.pgalise.util.datamodel;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * 
 * @author marcus
 *
 */
@Entity
//@Table(name = "PGALISE.BUS_STOP_TIMES")
public class BusStopTime {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "STOP_TIMES_ID")
	private BigInteger stopTimesId;
	
	@JoinColumn(name = "TRIP_ID")
	private BusTrip tripId;
	
	@JoinColumn(name = "STOP_ID")
	private BusStop stopId;
	
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
	
	public BusStopTime() {}

	public BusStopTime(BusTrip tripId,
			BusStop stopId, Date arrivalTime, Date departureTime,
			int stopSequence, String pickupTime, String stopHeadsign,
			String dropOffType, String shapeDistTraveled) {
		this.tripId = tripId;
		this.stopId = stopId;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.stopSequence = stopSequence;
		this.pickupTime = pickupTime;
		this.stopHeadsign = stopHeadsign;
		this.dropOffType = dropOffType;
		this.shapeDistTraveled = shapeDistTraveled;
	}

	public BigInteger getStopTimesId() {
		return stopTimesId;
	}

	public void setStopTimesId(BigInteger stopTimesId) {
		this.stopTimesId = stopTimesId;
	}

	public BusTrip getTripId() {
		return tripId;
	}

	public void setTripId(BusTrip tripId) {
		this.tripId = tripId;
	}

	public BusStop getStopId() {
		return stopId;
	}

	public void setStopId(BusStop stopId) {
		this.stopId = stopId;
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
