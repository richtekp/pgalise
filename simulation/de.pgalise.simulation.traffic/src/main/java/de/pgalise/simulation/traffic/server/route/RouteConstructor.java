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
 
package de.pgalise.simulation.traffic.server.route;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.traffic.graphextension.GraphExtensions;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.shared.city.BusStop;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.server.TrafficServer;

/**
 * Provides functionality to create a traffic graph based on an open street map. Generates also random routes between
 * the nodes.
 * 
 * @param <E> 
 * @author Lena
 * @author mischa
 */
public interface RouteConstructor<E extends NavigationEdge<?,?>> {
	public TrafficTrip createTrip(TrafficServer<?> serverId, Geometry cityZone, VehicleTypeEnum vehicleType);

	public TrafficTrip createTrip(TrafficServer<?> serverId, Geometry cityZone, NavigationNode nodeID, long startTimestamp, boolean isStartNode);

	public TrafficTrip createTrip(TrafficServer<?> serverId, NavigationNode startNodeID, NavigationNode targetNodeID, long startTimestamp);

	public TrafficTrip createTimedTrip(TrafficServer<?> serverId, Geometry cityZone, VehicleTypeEnum vehicleType, Date date, int buffer);

	public TrafficGraph<E> getGraph();

	public List<NavigationNode> getStartHomeNodes(Geometry cityZone);

	public List<NavigationNode> getStartWorkNodes(Geometry cityZone);

	public List<NavigationNode> getAllHomeNodes();

	public List<NavigationNode> getAllWorkNodes();

	public List<BusStop<?>> getBusStops();

	public List<E> getShortestPath(NavigationNode start, NavigationNode dest);

	public GraphExtensions getTrafficGraphExtesions();

	public List<E> getBusRoute(List<BusStop<?>> busStopIds);

	public Map<String, NavigationNode> getBusStopNodes(List<BusStop<?>> busStopIds);
}
