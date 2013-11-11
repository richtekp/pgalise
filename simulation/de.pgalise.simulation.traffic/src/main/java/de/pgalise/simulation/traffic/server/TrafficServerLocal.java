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
 
package de.pgalise.simulation.traffic.server;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.pgalise.simulation.service.ServiceDictionary;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.BusStop;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.model.RoadBarrier;
import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandlerManager;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Local view of the TrafficServer. The provided methods are just available for the application on the local host.
 * 
 * @author mustafa
 * @author Lena
 */
public interface TrafficServerLocal<F extends TrafficEvent> extends TrafficServer<F> {
	public static Set<SensorType> RESPONSIBLE_FOR_SENSOR_TYPES = new HashSet<SensorType>(
		Arrays.asList(
			SensorTypeEnum.TRAFFICLIGHT_SENSOR,
			SensorTypeEnum.INFRARED, 
			SensorTypeEnum.INDUCTIONLOOP, 
			SensorTypeEnum.TOPORADAR, 
			SensorTypeEnum.GPS_BIKE,
			SensorTypeEnum.GPS_BUS, 
			SensorTypeEnum.GPS_CAR, 
			SensorTypeEnum.GPS_TRUCK, 
			SensorTypeEnum.GPS_MOTORCYCLE
		)
	);

	/** 
	 * @return scheduler {@link Scheduler}
	 */
	public Scheduler getScheduler();

	/**
	 * Returns the CarFactory.
	 * 
	 * @return {@link CarFactory}
	 */
	public CarFactory getCarFactory();

	/**
	 * Return the TruckFactory.
	 * 
	 * @return {@link TruckFactory}
	 */
	public TruckFactory getTruckFactory();

	/**
	 * Returns the BikeFactory.
	 * 
	 * @return {@link BicycleFactory}
	 */
	public BicycleFactory getBikeFactory();

	/**
	 * Returns the MotorcycleFactory.
	 * 
	 * @return {@link MotorcycleFactory}
	 */
	public MotorcycleFactory getMotorcycleFactory();

	/**
	 * Returns the BusFactory.
	 * 
	 * @return {@link BusFactory}
	 */
	public BusFactory getBusFactory();

	/**
	 * Returns the ServiceDictionary.
	 * 
	 * @return {@link ServiceDictionary}
	 */
	public ServiceDictionary getServiceDictionary();

	/**
	 * @return update intervall of the simulation in ms
	 */
	public long getUpdateIntervall();

	/**
	 * Creates a trip of a vehicle with randomizes start and end nodes (depentent to the cityZone)
	 * @param cityZone boundaries the produced trip is limited by
	 * @param vehicleType type of the vehicle the trip is produced for
	 * @return {@link TrafficTrip}
	 */
	public TrafficTrip createTrip(Geometry cityZone, VehicleTypeEnum vehicleType);

	/**
	 * Creates a trip of a vehicle with specified start and end nodes at a specific time (e.g. specified in CC).
	 * @param startNodeID
	 * @param targetNodeID
	 * @param startTimestamp
	 * @return {@link TrafficTrip}
	 */
	public TrafficTrip createTrip(TrafficNode startNodeID, TrafficNode targetNodeID, long startTimestamp);

	/**
	 * Creates a trip of a vehicle with either a specified start node or a specified end nodes at a specific time. 
	 * The other node will be chosen randomly.
	 * @param cityZone boundaries the produced trip is limited by
	 * @param nodeID either the start node or target node of the trip
	 * @param startTimestamp preferred start time for the trip
	 * @param isStartNode specifies if nodeID is start node or target node
	 * @return {@link TrafficTrip}
	 */
	public TrafficTrip createTrip(Geometry cityZone, TrafficNode nodeID, long startTimestamp, boolean isStartNode);

	/**
	 * Creates a trip with randomly chosen start and end notes. If the date is null, the 
	 * time will be chosen randomly too.
	 * @param cityZone boundaries the produced trip is limited by
	 * @param vehicleType type of the vehicle the trip is produced for
	 * @param date preferred start time for the trip
	 * @param buffer
	 * @return {@link TrafficTrip}
	 */
	public TrafficTrip createTimedTrip(Geometry cityZone, VehicleTypeEnum vehicleType, Date date, int buffer);

	/**
	 * Returns a representation of the city traffic infrastructure as a graph.
	 * @return {@link Graph}
	 */
	public TrafficGraph getGraph();

	/**
	 * Calculates the shortest path between to nodes.
	 * @param start start node
	 * @param dest target node
	 * @return the shortest path between start node and target node
	 */
	public List<TrafficEdge> getShortestPath(TrafficNode start, TrafficNode dest);

	/**
	 * 
	 * @return {@link TrafficGraphExtensions}
	 */
	public TrafficGraphExtensions getTrafficGraphExtesions();

	/**
	 * Returns the path of a bus. This path will be calculated by the given bus stop ids. 
	 * Only existing bus stops will be considered for the path.
	 * @param busStopIds
	 * @return
	 */
	public List<TrafficEdge> getBusRoute(List<BusStop> busStopIds);

	/**
	 * Returns the nodes of the graph for a given list of bus stop ids.
	 * @param busStopIds
	 * @return
	 */
	public Map<BusStop, TrafficNode> getBusStopNodes(List<BusStop> busStopIds);
	
	/**
	 * 
	 * @return {@link SimulationEventHandlerManager}
	 */
	public TrafficEventHandlerManager<TrafficEventHandler<F>,F> getTrafficEventHandlerManager();
	
	/**
	 * 
	 * @return number of the other TrafficServer in the simulation
	 */
	public int getServerListSize();
	
	/**
	 * 
	 * @return current simulation time
	 */
	public long getCurrentTime();
	
	/**
	 * Returns the associated event for a vehicle. If a vehicle was e.g. created 
	 * by an attraction event this function will return this event.
	 * @return
	 */
	public Map<Long, F> getEventForVehicle();

	/**
	 * Items to schedule after the corresponding vehicle reached the attraction.
	 * @deprecated Should be used in the implementation only
	 * @return
	 */
	public List<ScheduleItem> getItemsToScheduleAfterAttractionReached();

	/**
	 * Items to schedule after the VehicleAmountManager checks the actual amount of vehicles. 
	 * If more vehicles should start driving they will be scheduled in this function.
	 * @deprecated Should be used in the implementation only
	 * @return
	 */
	public List<ScheduleItem> getItemsToScheduleAfterFuzzy();

	/**
	 * Items to remove after the VehicleAmountManager checks the actual amount of vehicles. 
	 * If less vehicles should start driving they will be removed from the scheduler in this function.
	 * @deprecated Should be used in the implementation only
	 * @return
	 */
	public List<Vehicle<?>> getItemsToRemoveAfterFuzzy();
	
	/**
	 * @deprecated
	 * @return
	 */
	public VehicleAmountManager getVehicleFuzzyManager();

	/**
	 * Adds a new {@link RoadBarrier}
	 * 
	 * @param barrier
	 *            {@link RoadBarrier}
	 */
	public void addNewRoadBarrier(RoadBarrier barrier);
	
	/**
	 * 
	 * @param timestamp current simulation time
	 * @return currently blocked roads
	 */
	public Set<TrafficEdge> getBlockedRoads(long timestamp);
}
