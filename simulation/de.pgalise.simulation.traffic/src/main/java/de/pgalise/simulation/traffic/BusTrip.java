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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import javax.persistence.Column;
import javax.persistence.ManyToOne;

/**
 * Holds all needed information for a bus trip.
 * @author Lena
 */
public class BusTrip {
	
	@ManyToOne
	private BusCalendar busCalendar;
	
	@Column(name = "ROUTE_ID")
	private String routeId;
	
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

	/**
	 * ID of the trip
	 */
	private Vehicle<BusData> bus;

	/**
	 * SensorHelper of the GPS sensor
	 */
	private SensorHelper gpsSensor;

	/**
	 * SensorHelper of the infrared sensor
	 */
	private SensorHelper infraredSensor;

	/**
	 * ID of the trip
	 */
	private String tripId;

	/**
	 * Short name
	 */
	private String routeShortName;

	/**
	 * Long name
	 */
	private String routeLongName;

	/**
	 * List with bus stops
	 */
	private List<String> busStops;

	/**
	 * Map with bus stops and stop times
	 */
	private Map<String, BusStopTime> busStopStopTimes;

	// /**
	// * Start time
	// */
	// private Time startTime;

	/**
	 * Constructor
	 * 
	 * @param tripId
	 *            ID of the trip
	 * @param routeId
	 *            ID of the route
	 * @param shortName
	 *            Short name
	 * @param longName
	 *            Long name
	 * @param busStops
	 *            List with bus stops
	 * @param stopTimes
	 *            Map with bus stops and stop times
	 */
	public BusTrip(String tripId, String routeId, String shortName, String longName, List<String> busStops,
			HashMap<String, BusStopTime> stopTimes, Vehicle<BusData> bus) {
		this.tripId = tripId;
		this.routeId = routeId;
		this.routeShortName = shortName;
		this.routeLongName = longName;
		this.busStops = busStops;
		this.busStopStopTimes = stopTimes;
		this.bus = bus;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getRouteShortName() {
		return routeShortName;
	}

	public void setRouteShortName(String routeShortName) {
		this.routeShortName = routeShortName;
	}

	public String getRouteLongName() {
		return routeLongName;
	}

	public void setRouteLongName(String routeLongName) {
		this.routeLongName = routeLongName;
	}

	public Map<String, BusStopTime> getBusStopStopTimes() {
		return busStopStopTimes;
	}

	public void setBusStopStopTimes(Map<String, BusStopTime> busStopStopTimes) {
		this.busStopStopTimes = busStopStopTimes;
	}

	public void setBus(
		Vehicle<BusData> bus) {
		this.bus = bus;
	}

	public Vehicle<BusData> getBus() {
		return bus;
	}

	public SensorHelper getGpsSensor() {
		return gpsSensor;
	}

	public void setGpsSensor(SensorHelper gpsSensor) {
		this.gpsSensor = gpsSensor;
	}

	public List<String> getBusStops() {
		return busStops;
	}

	public void setBusStops(List<String> busStops) {
		this.busStops = busStops;
	}

	public SensorHelper getInfraredSensor() {
		return infraredSensor;
	}

	public void setInfraredSensor(SensorHelper infraredSensor) {
		this.infraredSensor = infraredSensor;
	}

	public BusCalendar getServiceId() {
		return busCalendar;
	}

	public void setServiceId(BusCalendar serviceId) {
		this.busCalendar = serviceId;
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
