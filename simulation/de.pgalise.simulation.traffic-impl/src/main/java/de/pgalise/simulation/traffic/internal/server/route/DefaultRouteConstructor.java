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

import com.vividsolutions.jts.geom.Coordinate;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.pgalise.simulation.traffic.internal.DefaultBusStop;
import de.pgalise.simulation.traffic.internal.DefaultTrafficTrip;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.shared.city.BusStop;
import de.pgalise.simulation.shared.city.DefaultNavigationNode;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.internal.DefaultTrafficEdge;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.DefaultTrafficNode;
import de.pgalise.simulation.traffic.internal.graphextension.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.simulation.traffic.server.route.BusStopParser;
import de.pgalise.simulation.traffic.server.route.RandomVehicleTripGenerator;
import de.pgalise.simulation.traffic.server.route.RouteConstructor;
import de.pgalise.util.cityinfrastructure.impl.GraphConstructor;
import org.jgrapht.alg.DijkstraShortestPath;

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
public class DefaultRouteConstructor implements RouteConstructor {
	private RandomVehicleTripGenerator gen;
	private DefaultTrafficGraph<D> graph;
	private final GraphConstructor graphConstructor;
	private final RandomSeedService randomSeedService;
	private final RegionParser regionParser;
	private final BusStopParser busStopParser;
	private final long time;
	private final CityInfrastructureData trafficInformation;

	private TrafficGraphExtensions trafficGraphExtensions;
	private List<BusStop<?>> busStops;

	private Map<TrafficServer<?>, List<NavigationNode>> startHomeNodesForServer = new HashMap<>();
	private Map<TrafficServer<?>, List<NavigationNode>> startWorkNodesForServer = new HashMap<>();

	final Set<Foo> map = new HashSet<>();

	// private static Logger log = LoggerFactory.getLogger(DefaultRouteConstructor.class);

	private static class Foo {

		private boolean inUse;
		private DefaultTrafficGraph<?> graph;

		private Foo(final boolean inUse, DefaultTrafficGraph<?> graph) {
			this.inUse = inUse;
			this.graph = graph;
			this.dijkstra = dijkstra;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param mapper
	 *            GPS mapper
	 * @param cityInfrastructure
	 *            City Infrastructure Data
	 * @param time
	 * @param randomSeedService
	 * @param trafficGraphExtensions
	 */
	public DefaultRouteConstructor(Coordinate mapper, CityInfrastructureData cityInfrastructure, long time,
			RandomSeedService randomSeedService, TrafficGraphExtensions trafficGraphExtensions) {
		this.trafficInformation = cityInfrastructure;
		this.graphConstructor = new GraphConstructor(trafficGraphExtensions);
		this.regionParser = new RegionParser(cityInfrastructure);
		this.busStopParser = new BusStopParser(cityInfrastructure);
		this.time = time;
		this.randomSeedService = randomSeedService;
		this.trafficGraphExtensions = trafficGraphExtensions;

		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
			map.add(new Foo(false, null, null));
		}

		carNavigator = new Dijkstra(Element.NODE, "weight", null);
		this.init();
	}

	/**
	 * Constructor
	 * 
	 * @param rp
	 * @param bsp 
	 * @param trafficInformation
	 * @param gc
	 * @param time
	 * @param randomSeedService  
	 */
	public DefaultRouteConstructor(RegionParser rp, BusStopParser bsp, CityInfrastructureData trafficInformation,
			GraphConstructor gc, long time, RandomSeedService randomSeedService) {
		this.trafficInformation = trafficInformation;
		this.graphConstructor = gc;
		this.regionParser = rp;
		this.busStopParser = bsp;
		// this.gen = gen;
		this.time = time;
		this.randomSeedService = randomSeedService;
		this.init();
	}

	/**
	 * ...
	 * 
	 * @return 
	 */
	public DefaultTrafficGraph<?> createGraph() {
		this.graph = this.graphConstructor.createGraph("City", this.trafficInformation.getCycleAndMotorways());
		return this.graph;
	}

	@Override
	public TrafficTrip createTimedTrip(TrafficServer<?> serverId, Geometry cityZone, VehicleTypeEnum vehicleType, Date date,
			int buffer) {
		if (cityZone == null) {
			return this.gen.createVehicleTrip(getAllHomeNodes(), getAllWorkNodes(), vehicleType, date, buffer);
		}
		else {
			if (startHomeNodesForServer.get(serverId) == null) {
				startHomeNodesForServer.put(serverId, getStartHomeNodes(cityZone));
			}
			if (startWorkNodesForServer.get(serverId) == null) {
				startWorkNodesForServer.put(serverId, getStartWorkNodes(cityZone));
			}

			return this.gen.createVehicleTrip(startHomeNodesForServer.get(serverId),
					startWorkNodesForServer.get(serverId), vehicleType, date, buffer);
		}
	}

	@Override
	public TrafficTrip createTrip(int serverId, Geometry cityZone, VehicleTypeEnum vehicleType) {
		if (cityZone == null) {// for tests (if there is just 1 server and cityzone is undefined)
			return this.gen.createVehicleTrip(getAllHomeNodes(), getAllWorkNodes(), vehicleType, null, -1);
		} else {
			if (startHomeNodesForServer.get(serverId) == null) {
				startHomeNodesForServer.put(serverId, getStartHomeNodes(cityZone));
			}
			if (startWorkNodesForServer.get(serverId) == null) {
				startWorkNodesForServer.put(serverId, getStartWorkNodes(cityZone));
			}

			return this.gen.createVehicleTrip(startHomeNodesForServer.get(serverId),
					startWorkNodesForServer.get(serverId), vehicleType, null, -1);
		}

	}
	
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	@Override
	public TrafficTrip createTrip(TrafficServer<?> serverId, Geometry cityZone, NavigationNode nodeID, long startTimestamp,
			boolean isStartNode) {
		if (!isStartNode) {
			if (startHomeNodesForServer.get(serverId) == null) {
				startHomeNodesForServer.put(serverId, getStartHomeNodes(cityZone));
			}

			return this.gen.createVehicleTrip(startHomeNodesForServer.get(serverId), nodeID, startTimestamp);
		} else {
			if (cityZone == null || cityZone.covers(GEOMETRY_FACTORY.createPoint(nodeID.getGeoLocation()))) {
				return this.gen.createVehicleTrip(nodeID, getAllHomeNodes(), startTimestamp);
			}
			else {
				return null;
			}
		}
		// return (isStartNode) ? this.gen.createVehicleTrip(nodeID, getAllHomeNodes(), startTimestamp) : this.gen
		// .createVehicleTrip(lastUsedStartHomeNodes, nodeID, startTimestamp);
	}

	@Override
	public TrafficTrip createTrip(TrafficServer<?> serverId, NavigationNode startNodeID, NavigationNode targetNodeID, long startTimestamp) {
		return new DefaultTrafficTrip(startNodeID, targetNodeID, startTimestamp);
	}

	@Override
	public List<DefaultTrafficNode<D>> getAllHomeNodes() {
		return this.regionParser.getHomeNodes();
	}

	@Override
	public List<DefaultTrafficNode<D>> getAllWorkNodes() {
		return this.regionParser.getWorkNodes();
	}

	@Override
	public List<DefaultTrafficEdge<D>>  getBusRoute(List<BusStop<?>> busStopIds) {
		HashMap<NavigationNode, BusStop<?>> stopMap = new HashMap<>();
		for (BusStop<?> stop : busStops) {
			stopMap.put(stop, stop);
		}

		// create list which contains only existing nodes
		List<DefaultTrafficNode<?>> stopNodes = new LinkedList<>();
		for (BusStop<?> stopId : busStopIds) {
			stopNodes.add(stopId);
		}

		List<DefaultTrafficEdge<D>>  path = new LinkedList<> ();
		if (stopNodes.isEmpty()) {
			return path;
		}

		NavigationNode rootNode = stopNodes.get(0);
		DefaultTrafficNode<D> currentNode = stopNodes.get(0);
		DefaultTrafficNode<D> nextNode;

		List<DefaultTrafficEdge<D>>  intermediatePath;

		for (int i = 1; i < stopNodes.size(); i++) {
			nextNode = stopNodes.get(i);

			intermediatePath = getShortestPath(currentNode, nextNode);

			for (DefaultTrafficEdge<D> e : intermediatePath) {
				path.add(e);
			}

			currentNode = nextNode;
		}

		return path;
	}

	@Override
	public Map<BusStop<?>, DefaultTrafficNode<D>> getBusStopNodes(List<BusStop<?>> busStopIds) {
		HashMap<BusStop<?>, DefaultTrafficNode<D>> stopMap = new HashMap<>();
		for (BusStop<?> stop : busStops) {
			stopMap.put(stop, stop);
		}

		// create list which contains only existing nodes
		HashMap<BusStop<?>, NavigationNode> resultMap = new HashMap<>();
		for (BusStop<?> stopId : busStopIds) {
			NavigationNode n = stopMap.get(stopId);
			if (n != null) {
				resultMap.put(stopId, n);
			}
		}

		return resultMap;
	}

	@Override
	public List<BusStop<?>> getBusStops() {
		return busStops;
	}

	@Override
	public DefaultTrafficGraph<D> getGraph() {
		return this.graph;
	}

	@Override
	public List<DefaultTrafficEdge<D>> getShortestPath(DefaultTrafficNode<D> start, DefaultTrafficNode<D> dest) {
		long t = System.currentTimeMillis();
		List<DefaultTrafficEdge<D>> path = DijkstraShortestPath.findPathBetween(graph,
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
	public List<NavigationNode> getStartHomeNodes(Geometry cityZone) {
		List<NavigationNode> nodes = new ArrayList<>();
		for (DefaultTrafficNode<D> node : getAllHomeNodes()) {
			if (cityZone.covers(GEOMETRY_FACTORY.createPoint(this.trafficGraphExtensions.getPosition(node)))) {
				nodes.add(node);
			}
		}
		return nodes;
	}

	@Override
	public List<NavigationNode> getStartWorkNodes(Geometry cityZone) {
		List<NavigationNode> nodes = new ArrayList<>();
		for (NavigationNode node : getAllWorkNodes()) {
			if (cityZone.covers(GEOMETRY_FACTORY.createPoint(this.trafficGraphExtensions.getPosition(node)))) {
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

	public void parseHomeAndWorkNodes() {
		this.regionParser.parseLanduse(this.graph);
		this.gen = new DefaultRandomVehicleTripGenerator(this.time, this.getAllHomeNodes(), this.getAllWorkNodes(),
				this.randomSeedService);
	}

	private void init() {
		createGraph();
		carNavigator.init(graph);
		parseHomeAndWorkNodes();
		this.busStops = parseBusStops();
	}
}
