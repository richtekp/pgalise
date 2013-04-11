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
 
package de.pgalise.simulation.shared.traffic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.pgalise.simulation.shared.sensor.SensorHelper;

/**
 * Holds all needed information for a bus trip.
 * @author Lena
 */
public class BusTrip {

	/**
	 * ID of the trip
	 */
	private UUID id;

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
	 * ID of the route
	 */
	private String routeId;

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
	private Map<String, BusStopTimes> busStopStopTimes;

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
			HashMap<String, BusStopTimes> stopTimes) {
		this.tripId = tripId;
		this.routeId = routeId;
		this.routeShortName = shortName;
		this.routeLongName = longName;
		this.busStops = busStops;
		this.busStopStopTimes = stopTimes;
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

	public Map<String, BusStopTimes> getBusStopStopTimes() {
		return busStopStopTimes;
	}

	public void setBusStopStopTimes(Map<String, BusStopTimes> busStopStopTimes) {
		this.busStopStopTimes = busStopStopTimes;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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
}
