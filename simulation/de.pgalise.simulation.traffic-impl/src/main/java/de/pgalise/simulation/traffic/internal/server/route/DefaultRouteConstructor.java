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

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import de.pgalise.simulation.service.GPSMapper;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.geometry.Geometry;
import de.pgalise.simulation.shared.traffic.BusStop;
import de.pgalise.simulation.shared.traffic.TrafficTrip;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.server.route.BusStopParser;
import de.pgalise.simulation.traffic.server.route.RandomVehicleTripGenerator;
import de.pgalise.simulation.traffic.server.route.RegionParser;
import de.pgalise.simulation.traffic.server.route.RouteConstructor;
import de.pgalise.util.cityinfrastructure.impl.GraphConstructor;
import de.pgalise.util.vector.Vector2d;

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
	private Graph graph;
	private final GraphConstructor graphConstructor;
	private final RandomSeedService randomSeedService;
	private final RegionParser regionParser;
	private final BusStopParser busStopParser;
	private final long time;
	private final CityInfrastructureData trafficInformation;

	private Dijkstra carNavigator;

	private TrafficGraphExtensions trafficGraphExtensions;
	private List<BusStop> busStops;

	private Map<Integer, List<Node>> startHomeNodesForServer = new HashMap<>();
	private Map<Integer, List<Node>> startWorkNodesForServer = new HashMap<>();

	final Set<Foo> map = new HashSet<>();

	// private static Logger log = LoggerFactory.getLogger(DefaultRouteConstructor.class);

	private static final class Foo {

		private boolean inUse;
		private Graph graph;
		private Dijkstra dijkstra;

		private Foo(final boolean inUse, Graph graph, Dijkstra dijkstra) {
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
	public DefaultRouteConstructor(GPSMapper mapper, CityInfrastructureData cityInfrastructure, long time,
			RandomSeedService randomSeedService, TrafficGraphExtensions trafficGraphExtensions) {
		this.trafficInformation = cityInfrastructure;
		this.graphConstructor = new GraphConstructor(mapper, trafficGraphExtensions);
		this.regionParser = new RegionParser(mapper, cityInfrastructure);
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
	 * @param trafficInformation
	 * @param gc
	 * @param time
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
		carNavigator = new Dijkstra(Element.NODE, "weight", null);
		this.init();
	}

	/**
	 * ...
	 */
	public Graph createGraph() {
		this.graph = this.graphConstructor.createGraph("City", this.trafficInformation.getCycleAndMotorways());
		return this.graph;
	}

	@Override
	public TrafficTrip createTimedTrip(int serverId, Geometry cityZone, VehicleTypeEnum vehicleType, Date date,
			int buffer) {
		if (cityZone == null) // for tests (if there is just 1 server and cityzone is undefined)
			return this.gen.createVehicleTrip(getAllHomeNodes(), getAllWorkNodes(), vehicleType, date, buffer);
		else {
			if (startHomeNodesForServer.get(serverId) == null)
				startHomeNodesForServer.put(serverId, getStartHomeNodes(cityZone));
			if (startWorkNodesForServer.get(serverId) == null)
				startWorkNodesForServer.put(serverId, getStartWorkNodes(cityZone));

			return this.gen.createVehicleTrip(startHomeNodesForServer.get(serverId),
					startWorkNodesForServer.get(serverId), vehicleType, date, buffer);
		}
	}

	@Override
	public TrafficTrip createTrip(int serverId, Geometry cityZone, VehicleTypeEnum vehicleType) {
		if (cityZone == null) {// for tests (if there is just 1 server and cityzone is undefined)
			return this.gen.createVehicleTrip(getAllHomeNodes(), getAllWorkNodes(), vehicleType, null, -1);
		} else {
			if (startHomeNodesForServer.get(serverId) == null)
				startHomeNodesForServer.put(serverId, getStartHomeNodes(cityZone));
			if (startWorkNodesForServer.get(serverId) == null)
				startWorkNodesForServer.put(serverId, getStartWorkNodes(cityZone));

			return this.gen.createVehicleTrip(startHomeNodesForServer.get(serverId),
					startWorkNodesForServer.get(serverId), vehicleType, null, -1);
		}

	}

	@Override
	public TrafficTrip createTrip(int serverId, Geometry cityZone, String nodeID, long startTimestamp,
			boolean isStartNode) {
		if (!isStartNode) {
			if (startHomeNodesForServer.get(serverId) == null)
				startHomeNodesForServer.put(serverId, getStartHomeNodes(cityZone));

			return this.gen.createVehicleTrip(startHomeNodesForServer.get(serverId), nodeID, startTimestamp);
		} else {
			if (cityZone == null || cityZone.covers((Vector2d) graph.getNode(nodeID).getAttribute("position")))
				return this.gen.createVehicleTrip(nodeID, getAllHomeNodes(), startTimestamp);
			else
				return null;
		}
		// return (isStartNode) ? this.gen.createVehicleTrip(nodeID, getAllHomeNodes(), startTimestamp) : this.gen
		// .createVehicleTrip(lastUsedStartHomeNodes, nodeID, startTimestamp);
	}

	@Override
	public TrafficTrip createTrip(int serverId, String startNodeID, String targetNodeID, long startTimestamp) {
		return new TrafficTrip(startNodeID, targetNodeID, startTimestamp);
	}

	@Override
	public List<Node> getAllHomeNodes() {
		return this.regionParser.getHomeNodes();
	}

	@Override
	public List<Node> getAllWorkNodes() {
		return this.regionParser.getWorkNodes();
	}

	@Override
	public Path getBusRoute(List<String> busStopIds) {
		HashMap<String, Node> stopMap = new HashMap<>();
		for (BusStop stop : busStops) {
			stopMap.put(stop.getGraphNode().getId(), stop.getGraphNode());
		}

		// create list which contains only existing nodes
		List<Node> stopNodes = new LinkedList<>();
		for (String stopId : busStopIds) {
			Node n = stopMap.get(stopId);
			if (n != null)
				stopNodes.add(n);
		}

		Path path = new Path();

		if (stopNodes.isEmpty()) {
			return path;
		}

		Node rootNode = stopNodes.get(0);
		Node currentNode = stopNodes.get(0);
		Node nextNode;

		Path intermediatePath;
		path.setRoot(rootNode);

		for (int i = 1; i < stopNodes.size(); i++) {
			nextNode = stopNodes.get(i);

			intermediatePath = getShortestPath(currentNode, nextNode);

			for (Edge e : intermediatePath.getEdgePath()) {
				path.add(e);
			}

			currentNode = nextNode;
		}

		return path;
	}

	@Override
	public Map<String, Node> getBusStopNodes(List<String> busStopIds) {
		HashMap<String, Node> stopMap = new HashMap<>();
		for (BusStop stop : busStops) {
			stopMap.put(stop.getGraphNode().getId(), stop.getGraphNode());
		}

		// create list which contains only existing nodes
		HashMap<String, Node> resultMap = new HashMap<>();
		for (String stopId : busStopIds) {
			Node n = stopMap.get(stopId);
			if (n != null)
				resultMap.put(stopId, n);
		}

		return resultMap;
	}

	@Override
	public List<BusStop> getBusStops() {
		return busStops;
	}

	@Override
	public Graph getGraph() {
		return this.graph;
	}

	@Override
	public Path getShortestPath(Node start, Node dest) {
		long t = System.currentTimeMillis();
		((Dijkstra) carNavigator).setSource(start);
		carNavigator.compute();

		Path path = ((Dijkstra) carNavigator).getPath(dest);
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

	private Graph copyGraph(final Graph graph) {
		Graph copy = null;
		ByteArrayOutputStream rOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut = null;
		try {
			oOut = new ObjectOutputStream(rOut);
			oOut.writeObject(graph);
		} catch (IOException e) {
			try {
				rOut.close();
				oOut.close();
			} catch (IOException e1) {
			}
		}
		ByteArrayInputStream bIn = new ByteArrayInputStream(rOut.toByteArray());
		ObjectInputStream oIn = null;
		try {
			oIn = new ObjectInputStream(bIn);
			copy = (Graph) oIn.readObject();
		} catch (IOException | ClassNotFoundException e) {
			try {
				bIn.close();
				oIn.close();
			} catch (IOException e1) {
			}
		}
		return copy;
	}

	@Override
	public List<Node> getStartHomeNodes(Geometry cityZone) {
		List<Node> nodes = new ArrayList<>();
		for (Node node : getAllHomeNodes()) {
			if (cityZone.covers(this.trafficGraphExtensions.getPosition(node)))
				nodes.add(node);
		}
		return nodes;
	}

	@Override
	public List<Node> getStartWorkNodes(Geometry cityZone) {
		List<Node> nodes = new ArrayList<>();
		for (Node node : getAllWorkNodes()) {
			if (cityZone.covers(this.trafficGraphExtensions.getPosition(node)))
				nodes.add(node);
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
