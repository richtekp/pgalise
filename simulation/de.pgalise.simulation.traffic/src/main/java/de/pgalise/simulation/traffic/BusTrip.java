/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @param <X> 
 * @param <D> 
 * @param <N> 
 * @param <E> 
 * @author richter
 */
public interface BusTrip<
	X extends BusStopTime<?,?,V,N,E>, 
	V extends Vehicle<BusData, N,E,V>, 
	N extends TrafficNode<N,E,BusData,V>, 
	E extends TrafficEdge<N,E,BusData,V>
> extends TrafficTrip {

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
