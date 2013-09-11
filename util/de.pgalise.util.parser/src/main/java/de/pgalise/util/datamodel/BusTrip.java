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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author marcus
 *
 */
@Entity
//@Table(name = "PGALISE.BUS_TRIPS")
public final class BusTrip {
	
	@Column(name = "SERVICE_ID")
	private String serviceId;
	
	@Column(name = "ROUTE_ID")
	private String routeId;
	
	@Id
	@Column(name = "TRIP_ID")
	private String tripId;
	
	@Column(name = "TRIP_HEADSIGN")
	private String tripHeadsign;
	
	@Column(name = "TRIP_SHORT_NAME")
	private String tripShortName;
	
	@Column(name = "DIRECTION_ID")
	private String directionId;
	
	@Column(name = "BLOCK_ID")
	private String blockId;
	
	@Column(name = "SHAPE_ID")
	private String shapeId;
	
	@Column(name = "WHEELCHAIR_ACCESSIBLE")
	private String wheelchairAccessible;
	
	public BusTrip() {}

	public BusTrip(String serviceId, String routeId, String tripId,
			String tripHeadsign, String tripShortName, String directionId,
			String blockId, String shapeId, String wheelchairAccessible) {
		this.serviceId = serviceId;
		this.routeId = routeId;
		this.tripId = tripId;
		this.tripHeadsign = tripHeadsign;
		this.tripShortName = tripShortName;
		this.directionId = directionId;
		this.blockId = blockId;
		this.shapeId = shapeId;
		this.wheelchairAccessible = wheelchairAccessible;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public String getTripHeadsign() {
		return tripHeadsign;
	}

	public void setTripHeadsign(String tripHeadsign) {
		this.tripHeadsign = tripHeadsign;
	}

	public String getTripShortName() {
		return tripShortName;
	}

	public void setTripShortName(String tripShortName) {
		this.tripShortName = tripShortName;
	}

	public String getDirectionId() {
		return directionId;
	}

	public void setDirectionId(String directionId) {
		this.directionId = directionId;
	}

	public String getBlockId() {
		return blockId;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}

	public String getShapeId() {
		return shapeId;
	}

	public void setShapeId(String shapeId) {
		this.shapeId = shapeId;
	}

	public String getWheelchairAccessible() {
		return wheelchairAccessible;
	}

	public void setWheelchairAccessible(String wheelchairAccessible) {
		this.wheelchairAccessible = wheelchairAccessible;
	}
	
	
}
