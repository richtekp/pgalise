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

import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.shared.traffic.VehicleType;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficTrip;
import de.pgalise.simulation.traffic.graphextension.GraphExtensions;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides functionality to create a traffic graph based on an open street map.
 * Generates also random routes between the nodes.
 *
 * @param <D>
 * @param <E>
 * @param <V>
 * @param <B>
 * @author Lena
 * @author mischa
 */
public interface RouteConstructor {

  public TrafficTrip createTrip(TrafficControllerLocal<?> serverId,
    Geometry cityZone,
    VehicleType vehicleType);

  public TrafficTrip createTrip(TrafficControllerLocal<?> serverId,
    Geometry cityZone,
    TrafficNode nodeID,
    long startTimestamp,
    boolean isStartNode);

  public TrafficTrip createTrip(TrafficControllerLocal<?> serverId,
    TrafficNode startNodeID,
    TrafficNode targetNodeID,
    long startTimestamp);

  public TrafficTrip createTimedTrip(TrafficControllerLocal<?> serverId,
    Geometry cityZone,
    VehicleType vehicleType,
    Date date,
    int buffer);

  public TrafficGraph getGraph();

  public List<TrafficNode> getStartHomeNodes(Geometry cityZone);

  public List<TrafficNode> getStartWorkNodes(Geometry cityZone);

  public List<TrafficNode> getAllHomeNodes();

  public List<TrafficNode> getAllWorkNodes();

  public List<BusStop> getBusStops();

  public List<TrafficEdge> getShortestPath(TrafficNode start,
    TrafficNode dest);

  public GraphExtensions getTrafficGraphExtesions();

  public List<TrafficEdge> getBusRoute(List<BusStop> busStopIds);

  public Map<BusStop, TrafficNode> getBusStopNodes(List<BusStop> busStopIds);
}
