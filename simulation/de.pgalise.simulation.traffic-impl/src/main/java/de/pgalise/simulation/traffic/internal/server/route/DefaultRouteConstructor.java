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
package de.pgalise.simulation.traffic.internal.server.route;

import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.shared.traffic.VehicleType;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficTrip;
import de.pgalise.simulation.traffic.server.route.BusStopParser;
import de.pgalise.simulation.traffic.server.route.RandomVehicleTripGenerator;
import de.pgalise.simulation.traffic.server.route.RouteConstructor;
import de.pgalise.util.cityinfrastructure.impl.GraphConstructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.alg.DijkstraShortestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of a {@link RouteConstructor}.
 *
 * @author Mustafa
 * @author Lena
 * @author Timo
 * @author mischa
 * @author Andreas Rehfeldt
 * @version 1.1
 */
@Stateful
public class DefaultRouteConstructor implements RouteConstructor {

  private static final Logger log = LoggerFactory.getLogger(
    DefaultRouteConstructor.class);
  @EJB
  private RandomVehicleTripGenerator randomVehicleTripGenerator;
  private TrafficGraph graph;
  @EJB
  private GraphConstructor graphConstructor;
  @EJB
  private RandomSeedService randomSeedService;
  @EJB
  private RegionParser regionParser;
  @EJB
  private BusStopParser busStopParser;
  @EJB
  private TrafficGraphExtensions trafficGraphExtensions;
  private List<BusStop> busStops;

  private List<TrafficNode> startHomeNodesForServer = new LinkedList<>();
  private List<TrafficNode> startWorkNodesForServer = new LinkedList<>();

  final Set<Pair<Boolean, TrafficGraph>> map = new HashSet<>();

  /**
   * Constructor
   *
   */
  public DefaultRouteConstructor() {
    for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
      map.add(new MutablePair(false,
        null));
    }
  }

  @PostConstruct
  public void initialize() {
    this.busStops = parseBusStops();
  }

  @Override
  public TrafficTrip createTimedTrip(TrafficControllerLocal<?> serverId,
    Geometry cityZone,
    VehicleType vehicleType,
    Date date,
    int buffer) {
    if (cityZone == null) {
      return this.randomVehicleTripGenerator.
        createVehicleTrip(getAllHomeNodes(),
          getAllWorkNodes(),
          vehicleType,
          date,
          buffer);
    } else {
      if (startHomeNodesForServer == null) {
        startHomeNodesForServer = getStartHomeNodes(cityZone);
      }
      if (startWorkNodesForServer == null) {
        startWorkNodesForServer
          = getStartWorkNodes(cityZone);
      }

      return this.randomVehicleTripGenerator.createVehicleTrip(
        startHomeNodesForServer,
        startWorkNodesForServer,
        vehicleType,
        date,
        buffer);
    }
  }

  @Override
  public TrafficTrip createTrip(TrafficControllerLocal<?> server,
    Geometry cityZone,
    VehicleType vehicleType) {
    if (cityZone == null) {// for tests (if there is just 1 server and cityzone is undefined)
      return this.randomVehicleTripGenerator.
        createVehicleTrip(getAllHomeNodes(),
          getAllWorkNodes(),
          vehicleType,
          new Date(),
          10);
    } else {
      if (startHomeNodesForServer == null) {
        startHomeNodesForServer
          = getStartHomeNodes(cityZone);
      }
      if (startWorkNodesForServer == null) {
        startWorkNodesForServer
          = getStartWorkNodes(cityZone);
      }

      return this.randomVehicleTripGenerator.createVehicleTrip(
        startHomeNodesForServer,
        startWorkNodesForServer,
        vehicleType,
        new Date(),
        10);
    }

  }

  @Override
  public TrafficTrip createTrip(TrafficControllerLocal<?> serverId,
    Geometry cityZone,
    TrafficNode nodeID,
    long startTimestamp,
    boolean isStartNode) {
    if (!isStartNode) {
      if (startHomeNodesForServer == null) {
        startHomeNodesForServer
          = getStartHomeNodes(cityZone);
      }

      return this.randomVehicleTripGenerator.createVehicleTrip(
        startHomeNodesForServer,
        nodeID,
        startTimestamp);
    } else {
      if (cityZone == null || cityZone.covers(GeoToolsBootstrapping.getGeometryFactory().createPoint(
          nodeID))) {
        return this.randomVehicleTripGenerator.createVehicleTrip(nodeID,
          getAllHomeNodes(),
          startTimestamp);
      } else {
        return null;
      }
    }
    // return (isStartNode) ? this.gen.createVehicleTrip(nodeID, getAllHomeNodes(), startTimestamp) : this.gen
    // .createVehicleTrip(lastUsedStartHomeNodes, nodeID, startTimestamp);
  }

  @Override
  public TrafficTrip createTrip(TrafficControllerLocal<?> serverId,
    TrafficNode startNodeID,
    TrafficNode targetNodeID,
    long startTimestamp) {
    return new TrafficTrip(startNodeID,
      targetNodeID,
      startTimestamp);
  }

  @Override
  public List<TrafficNode> getAllHomeNodes() {
    return this.regionParser.getHomeNodes();
  }

  @Override
  public List<TrafficNode> getAllWorkNodes() {
    return this.regionParser.getWorkNodes();
  }

  @Override
  public List<TrafficEdge> getBusRoute(List<BusStop> busStopIds) {
    HashMap<NavigationNode, BusStop> stopMap = new HashMap<>();
    for (BusStop stop : busStops) {
      stopMap.put(stop,
        stop);
    }

    // create list which contains only existing nodes
    List<TrafficNode> stopNodes = new LinkedList<>();
    for (BusStop stopId : busStopIds) {
      stopNodes.add(stopId);
    }

    List<TrafficEdge> path = new LinkedList<>();
    if (stopNodes.isEmpty()) {
      return path;
    }

    NavigationNode rootNode = stopNodes.get(0);
    TrafficNode currentNode = stopNodes.get(0);
    TrafficNode nextNode;

    List<TrafficEdge> intermediatePath;

    for (int i = 1; i < stopNodes.size(); i++) {
      nextNode = stopNodes.get(i);

      intermediatePath = getShortestPath(currentNode,
        nextNode);

      for (TrafficEdge e : intermediatePath) {
        path.add(e);
      }

      currentNode = nextNode;
    }

    return path;
  }

  @Override
  public Map<BusStop, TrafficNode> getBusStopNodes(List<BusStop> busStopIds) {
    HashMap<BusStop, TrafficNode> stopMap = new HashMap<>();
    for (BusStop stop : busStops) {
      stopMap.put(stop,
        stop);
    }

    // create list which contains only existing nodes
    HashMap<BusStop, TrafficNode> resultMap = new HashMap<>();
    for (BusStop stopId : busStopIds) {
      TrafficNode n = stopMap.get(stopId);
      if (n != null) {
        resultMap.put(stopId,
          n);
      }
    }

    return resultMap;
  }

  @Override
  public List<BusStop> getBusStops() {
    return busStops;
  }

  @Override
  public TrafficGraph getGraph() {
    return this.graph;
  }

  @Override
  public List<TrafficEdge> getShortestPath(TrafficNode start,
    TrafficNode dest) {
    long t = System.currentTimeMillis();
    List<TrafficEdge> path = DijkstraShortestPath.findPathBetween(graph,
      start,
      dest);
    return path;
  }

  // public Path getShortestPath(Node start, Node dest) {
  //
  // Foo entry = null;
  // for(Foo f : this.map) {
  // if(f.inUse == false) {
  // entry = f;
  // break;
  // }
  // }
  //
  // entry.inUse = true;
  //
  // if(entry.graph == null) {
  // entry.graph = this.copyGraph(this.graph);
  // entry.dijkstra = new Dijkstra(Element.NODE, "weight", null);
  // entry.dijkstra.init(entry.graph);
  // }
  // entry.dijkstra.setSource(start);
  // entry.dijkstra.compute();
  // final Path result = entry.dijkstra.getPath(dest);
  // entry.inUse = false;
  // return result;
  //
  // }
  @Override
  public List<TrafficNode> getStartHomeNodes(Geometry cityZone) {
    List<TrafficNode> nodes = new ArrayList<>();
    for (TrafficNode node : getAllHomeNodes()) {
      if (cityZone.covers(GeoToolsBootstrapping.getGeometryFactory().
        createPoint(
          this.trafficGraphExtensions.getPosition(node)))) {
        nodes.add(node);
      }
    }
    return nodes;
  }

  @Override
  public List<TrafficNode> getStartWorkNodes(Geometry cityZone) {
    List<TrafficNode> nodes = new ArrayList<>();
    for (TrafficNode node : getAllWorkNodes()) {
      if (cityZone.covers(GeoToolsBootstrapping.getGeometryFactory().
        createPoint(
          this.trafficGraphExtensions.getPosition(node)))) {
        nodes.add(node);
      }
    }
    return nodes;
  }

  @Override
  public TrafficGraphExtensions getTrafficGraphExtesions() {
    return trafficGraphExtensions;
  }

  public List<BusStop> parseBusStops() {
    return this.busStopParser.parseBusStops(this.graph);
  }

  public void parseHomeAndWorkNodes(long time,
    CityInfrastructureData cityInfrastructureData) {
    this.regionParser.parseLanduse(this.graph,
      cityInfrastructureData);
    this.randomVehicleTripGenerator = new DefaultRandomVehicleTripGenerator(time,
      this.getAllHomeNodes(),
      this.getAllWorkNodes(),
      this.randomSeedService);
  }

}
