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

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import de.pgalise.simulation.shared.geometry.Geometry;
import de.pgalise.simulation.shared.graphextension.GraphExtensions;
import de.pgalise.simulation.shared.traffic.BusStop;
import de.pgalise.simulation.shared.traffic.TrafficTrip;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;

/**
 * Provides functionality to create a traffic graph based on an open street map. Generates also random routes between
 * the nodes.
 * 
 * @author Lena
 * @author mischa
 */
public interface RouteConstructor {
	public TrafficTrip createTrip(int serverId, Geometry cityZone, VehicleTypeEnum vehicleType);

	public TrafficTrip createTrip(int serverId, Geometry cityZone, String nodeID, long startTimestamp, boolean isStartNode);

	public TrafficTrip createTrip(int serverId, String startNodeID, String targetNodeID, long startTimestamp);

	public TrafficTrip createTimedTrip(int serverId, Geometry cityZone, VehicleTypeEnum vehicleType, Date date, int buffer);

	public Graph getGraph();

	public List<Node> getStartHomeNodes(Geometry cityZone);

	public List<Node> getStartWorkNodes(Geometry cityZone);

	public List<Node> getAllHomeNodes();

	public List<Node> getAllWorkNodes();

	public List<BusStop> getBusStops();

	public Path getShortestPath(Node start, Node dest);

	public GraphExtensions getTrafficGraphExtesions();

	public Path getBusRoute(List<String> busStopIds);

	public Map<String, Node> getBusStopNodes(List<String> busStopIds);
}
