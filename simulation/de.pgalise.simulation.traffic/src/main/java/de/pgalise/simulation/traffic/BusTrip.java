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

import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.shared.city.BusCalendar;
import de.pgalise.simulation.shared.city.BusStop;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.util.List;
import java.util.Map;

/**
	 * 
	 */
public interface BusTrip extends TrafficTrip {

	public BusRoute<?> getRoute() ;

	public void setRoute(BusRoute<?> routeId) ;

	public String getRouteShortName() ;

	public void setRouteShortName(String routeShortName) ;

	public String getRouteLongName() ;

	public void setRouteLongName(String routeLongName) ;

	public Map<BusStop<?>, X> getBusStopStopTimes() ;

	public void setBusStopStopTimes(Map<BusStop<?>, X> busStopStopTimes) ;

	public void setBus(
		V bus) ;

	public V getBus() ;

	public SensorHelper<?> getGpsSensor() ;

	public void setGpsSensor(SensorHelper<?> gpsSensor) ;

	public List<BusStop<?>> getBusStops() ;

	public void setBusStops(List<BusStop<?>> busStops) ;

	public SensorHelper<?> getInfraredSensor() ;

	public void setInfraredSensor(SensorHelper<?> infraredSensor) ;

	public BusCalendar getServiceId() ;

	public void setServiceId(BusCalendar serviceId) ;

	public String getTripHeadsign() ;

	public void setTripHeadsign(String tripHeadsign) ;

	public String getTripShortName() ;

	public void setTripShortName(String tripShortName) ;

	public String getDirectionId() ;

	public void setDirectionId(String directionId) ;

	public String getBlockId() ;

	public void setBlockId(String blockId) ;

	public String getShapeId() ;

	public void setShapeId(String shapeId) ;

	public String getWheelchairAccessible() ;

	public void setWheelchairAccessible(String wheelchairAccessible) ;
}
