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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.shared.city.BusCalendar;
import de.pgalise.simulation.traffic.BusRoute;
import de.pgalise.simulation.shared.city.BusStop;
import de.pgalise.simulation.traffic.BusTrip;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

/**
 * Holds all needed information for a bus trip.
 * @author Lena
 */
@Entity
public class DefaultBusTrip extends DefaultTrafficTrip implements BusTrip<DefaultBusStopTime, BaseVehicle<BusData>> {
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private BusCalendar busCalendar;
	
	@Column(name = "ROUTE_ID")
	@ManyToOne
	private BusRoute<?> route;
	
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
	private BaseVehicle<BusData> bus;

	/**
	 * SensorHelper of the GPS sensor
	 */
	private SensorHelper<?> gpsSensor;

	/**
	 * SensorHelper of the infrared sensor
	 */
	private SensorHelper<?> infraredSensor;

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
	@ManyToMany
	private List<BusStop<?>> busStops;

	/**
	 * Map with bus stops and stop times
	 */
	@MapKey
	@OneToMany
	private Map<BusStop<?>, DefaultBusStopTime> busStopStopTimes;

	protected DefaultBusTrip() {
	}

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
	 * @param bus  
	 */
	public DefaultBusTrip(BusRoute<?> route, String shortName, String longName, List<BusStop<?>> busStops,
			HashMap<BusStop<?>, DefaultBusStopTime> stopTimes, BaseVehicle<BusData> bus) {
		this.route = route;
		this.routeShortName = shortName;
		this.routeLongName = longName;
		this.busStops = busStops;
		this.busStopStopTimes = stopTimes;
		this.bus = bus;
	}

	@Override
	public String getRouteShortName() {
		return routeShortName;
	}

	@Override
	public void setRouteShortName(String routeShortName) {
		this.routeShortName = routeShortName;
	}

	@Override
	public String getRouteLongName() {
		return routeLongName;
	}

	@Override
	public void setRouteLongName(String routeLongName) {
		this.routeLongName = routeLongName;
	}

	@Override
	public Map<BusStop<?>, DefaultBusStopTime> getBusStopStopTimes() {
		return busStopStopTimes;
	}

	@Override
	public void setBusStopStopTimes(Map<BusStop<?>, DefaultBusStopTime> busStopStopTimes) {
		this.busStopStopTimes = busStopStopTimes;
	}

	@Override
	public void setBus(
		BaseVehicle<BusData> bus) {
		this.bus = bus;
	}

	@Override
	public BaseVehicle<BusData> getBus() {
		return bus;
	}

	@Override
	public SensorHelper getGpsSensor() {
		return gpsSensor;
	}

	@Override
	public void setGpsSensor(SensorHelper gpsSensor) {
		this.gpsSensor = gpsSensor;
	}

	@Override
	public List<BusStop<?>> getBusStops() {
		return busStops;
	}

	@Override
	public void setBusStops(List<BusStop<?>> busStops) {
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

	@Override
	public BusRoute<?> getRoute() {
		return route;
	}

	@Override
	public void setRoute(BusRoute<?> route) {
		this.route = route;
	}
}
