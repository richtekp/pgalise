///* 
// * Copyright 2013 PG Alise (http://www.pg-alise.de/)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License. 
// */
// 
//package de.pgalise.simulation.traffic;
//
//import de.pgalise.simulation.shared.city.NavigationEdge;
//import de.pgalise.simulation.shared.city.NavigationNode;
//import com.vividsolutions.jts.geom.Coordinate;
//import de.pgalise.simulation.service.RandomSeedService;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.UUID;
//
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
//import de.pgalise.simulation.shared.event.EventList;
//import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
//import de.pgalise.simulation.traffic.internal.graphextension.DefaultTrafficGraphExtensions;
//import de.pgalise.simulation.traffic.internal.DefaultTrafficVisualizer;
//import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
//import de.pgalise.simulation.traffic.internal.model.vehicle.ExtendedXMLVehicleFactory;
//import de.pgalise.simulation.traffic.internal.server.jam.DefaultNaSchModel;
//import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
//import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
//import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
//import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
//import de.pgalise.simulation.traffic.server.jam.TrafficJamModel;
//import de.pgalise.util.generic.MutableBoolean;
//import de.pgalise.util.generic.function.Function;
//import javax.vecmath.Vector2d;
//import org.jgrapht.alg.DijkstraShortestPath;
//
///**
// * Test for the Traffic Visualizer. Opens a window and draws a graph with driving vehicles.
// * 
// * @author Marina
// * @author Mustafa
// */
//@Ignore
//public class TrafficVisualizerTest {
//	private TrafficEdge ab,bc;
//	private TrafficGraph graph = new DefaultTrafficGraph();
//	private NavigationNode a = new NavigationNode(new Coordinate( 1, 1));
//	private NavigationNode b = new NavigationNode(new Coordinate( 2, 2));
//	private NavigationNode c = new NavigationNode(new Coordinate( 4, 7));
//	private NavigationNode d = new NavigationNode(new Coordinate( 9, 4));
//	private NavigationNode e = new NavigationNode(new Coordinate( 11, 7));
//	private NavigationNode f = new NavigationNode(new Coordinate( 2, 10));
//
//	/**
//	 * CarFactory
//	 */
//	private CarFactory factory;
//
//	/**
//	 * TrafficGraphExtensions
//	 */
//	private TrafficGraphExtensions ee;
//
//
//	@Before
//	public void init() {
//		RandomSeedService rss = new DefaultRandomSeedService();
//		TrafficGraph graph = new DefaultTrafficGraph();
//		ee = new DefaultTrafficGraphExtensions(rss, graph);
//		rss.init(System.currentTimeMillis());
//
//		factory = new ExtendedXMLVehicleFactory(rss, BaseVehicle.class.getResourceAsStream("/defaultvehicles.xml"),
//				this.ee);
//	}
//
//	@Test
//	public void vizualizerTest() throws InterruptedException {
//		final MutableBoolean exit = new MutableBoolean();
//		exit.setValue(false);
//		final TrafficGraph graph = createGraph();
//		final List<Vehicle<?>> vehicles = createVehicles(graph);
//
//		RandomSeedService seedService = new DefaultRandomSeedService();
//		seedService.init(System.currentTimeMillis());
//
//		final TrafficJamModel nasch = new DefaultNaSchModel(seedService);
//
//		// Visualizer for the traffic
//		final TrafficVisualizer viz = new DefaultTrafficVisualizer(900, 700, graph, ee);
//		viz.setVehicles(vehicles);
//
//		viz.scale(50, 50);
//
//		viz.addCommand(new Function() {
//
//			@Override
//			public void delegate() {
//				long allTime = 0;
//				for (int i = 0; i < 200; i++) {
//					if (vehicles.isEmpty()) {
//						break;
//					}
//					long currentTime = 1000;
//					allTime += currentTime;
//					for (Iterator<Vehicle<?>> it = vehicles.iterator(); it.hasNext();) {
//						Vehicle<?> v = it.next();
//						nasch.update(v, currentTime, graph, 0.5);
//						for (final TrafficNode node : graph.getVertexSet()) {
//							try {
//								ee.getTrafficRule(node).update(
//										new EventList(null, allTime, UUID.randomUUID()));
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//						// v.update(currentTime);
//						if (v.getVehicleState() == VehicleStateEnum.REACHED_TARGET) {
//							it.remove();
//						}
//					}
//
//					try {
//						Thread.sleep(300);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//		});
//		// Starts the visualizer
//		viz.start();
//
//		// Draws the graph and vehicles as long as the command is executed
//		while (!viz.finishedCommands()) {
//			viz.draw();
//		}
//	}
//
//	/**
//	 * Creates vehicles and their routes and positions them on Nodes of the given graph.
//	 * 
//	 * @param graph
//	 *            Given graph
//	 * @return List of vehicles which got positioned on the graph
//	 * @throws InterruptedException
//	 */
//	private List<Vehicle<?>> createVehicles(TrafficGraph graph) throws InterruptedException {
//
//		// Sets the max speed of the edges
//		ab.setMaxSpeed(50.0);
//		bc.setMaxSpeed(120.0);
//
//		// Creates the default car factory
//
//		// Creating the cars
//		Vehicle<?> carA = factory.createRandomCar( null);
//		carA.setTrafficGraphExtensions(ee);
//		carA.setPath(getRoute(graph.getNode("a"), graph.getNode("e")));
//		carA.setVelocity(27);
//		carA.setName("carA");
//
//		Vehicle<?> carB = factory.createRandomCar( null);
//		carB.setTrafficGraphExtensions(ee);
//		carB.setPath(getRoute(graph.getNode("a"), graph.getNode("e")));
//
//		Vehicle<?> carC = factory.createRandomCar( null);
//		carC.setTrafficGraphExtensions(ee);
//		carC.setPath(getRoute(graph.getNode("a"), graph.getNode("e")));
//
//		Vehicle<?> carD = factory.createRandomCar( null);
//		carD.setTrafficGraphExtensions(ee);
//		carD.setPath(getRoute(graph.getNode("a"), graph.getNode("e")));
//
//		Vehicle<?> carE = factory.createRandomCar( null);
//		carE.setTrafficGraphExtensions(ee);
//		carE.setPath(getRoute(graph.getNode("f"), graph.getNode("e")));
//
//		Vehicle<?> carF = factory.createRandomCar( null);
//		carF.setTrafficGraphExtensions(ee);
//		carF.setPath(getRoute(graph.getNode("f"), graph.getNode("e")));
//
//		Vehicle<?> carG = factory.createRandomCar( null);
//		carG.setTrafficGraphExtensions(ee);
//		carG.setPath(getRoute(graph.getNode("f"), graph.getNode("e")));
//
//		Vehicle<?> carH = factory.createRandomCar( null);
//		carH.setTrafficGraphExtensions(ee);
//		carH.setPath(getRoute(graph.getNode("f"), graph.getNode("e")));
//
//		Vehicle<?> carI = factory.createRandomCar( null);
//		carI.setTrafficGraphExtensions(ee);
//		carI.setPath(getRoute(graph.getNode("f"), graph.getNode("e")));
//
//		// CAR B
//		Vector2d apos = new Vector2d(carA.getPosition().x, carA.getPosition().y);
//		Vector2d adir = carA.getDirection();
//		adir.scale(0.15);
//		apos.add(adir);
//
//		carB.setPosition(new Coordinate(apos.x, apos.y));
//		carB.setName("carB");
//		carB.setVelocity(60);
//
//		// CAR C
//		apos = new Vector2d(carA.getPosition().x, carA.getPosition().y);
//		adir = carA.getDirection();
//		adir.scale(0.3);
//		apos.add(adir);
//
//		carC.setPosition(new Coordinate(apos.x, apos.y));
//		carC.setName("carC");
//		carC.setVelocity(50);
//
//		// CAR D
//		apos = new Vector2d(carA.getPosition().x, carA.getPosition().y);
//		adir = carA.getDirection();
//		adir.scale(0.7);
//		apos.add(adir);
//
//		carD.setPosition(new Coordinate(apos.x, apos.y));
//		carD.setName("carD");
//		carD.setVelocity(10);
//
//		// carE.setPosition(apos);
//		carE.setName("carE");
//		carE.setVelocity(60);
//
//		apos = new Vector2d(carE.getPosition().x, carE.getPosition().y);
//		adir = carE.getDirection();
//		adir.scale(0.12);
//		apos.add(adir);
//
//		carF.setPosition(new Coordinate(apos.x, apos.y));
//		carF.setName("carF");
//		carF.setVelocity(50);
//
//		apos = new Vector2d(carE.getPosition().x, carE.getPosition().y);
//		adir = carE.getDirection();
//		adir.scale(0.32);
//		apos.add(adir);
//
//		carG.setPosition(new Coordinate(apos.x, apos.y));
//		carG.setName("carG");
//		carG.setVelocity(10);
//
//		apos = new Vector2d(carE.getPosition().x, carE.getPosition().y);
//		adir = carE.getDirection();
//		adir.scale(0.52);
//		apos.add(adir);
//
//		carH.setPosition(new Coordinate(apos.x, apos.y));
//		carH.setName("carH");
//		carH.setVelocity(10);
//
//		apos = new Vector2d(carE.getPosition().x, carE.getPosition().y);
//		adir = carE.getDirection();
//		adir.scale(0.82);
//		apos.add(adir);
//
//		carI.setPosition(new Coordinate(apos.x, apos.y));
//		carI.setName("carI");
//		carI.setVelocity(10);
//
//		List<Vehicle<?>> vehicles = new LinkedList<>();
//		vehicles.add(carA);
//		vehicles.add(carB);
//		vehicles.add(carC);
//		vehicles.add(carD);
//		vehicles.add(carE);
//		vehicles.add(carF);
//		vehicles.add(carG);
//		vehicles.add(carH);
//		vehicles.add(carI);
//
//		return vehicles;
//	}
//
//	/**
//	 * Creates the graph with nodes and edges for the test.
//	 * 
//	 * @return created graph
//	 */
//	private TrafficGraph createGraph() {
//
//		NavigationEdge ab = null;
//		ab = graph.addEdge("ab", a, b);
//		ab.setAttribute("weight", 1);
//		NavigationEdge bc = null;
//		bc = graph.addEdge("bc", b, c);
//		bc.setAttribute("weight", 1);
//		NavigationEdge cd = null;
//		cd = graph.addEdge("cd", c, d);
//		ee.setPriorityRoad(cd, true);
//		cd.setAttribute("weight", 1);
//		NavigationEdge de = null;
//		de = graph.addEdge("de", d, e);
//		de.setAttribute("weight", 1);
//		NavigationEdge fc = null;
//		fc = graph.addEdge("fc", f, c);
//		ee.setPriorityRoad(fc, true);
//		fc.setAttribute("weight", 1);
//		return graph;
//	}
//
//	/**
//	 * Adds a node to the given graph.
//	 * 
//	 * @param graph
//	 *            Given graph
//	 * @param id
//	 *            ID of the node
//	 * @param x
//	 *            X-Position of the node
//	 * @param y
//	 *            Y-Position of the node
//	 * @return The added node
//	 */
//	private NavigationNode addNode(TrafficGraph graph, String id, double x, double y) {
//		NavigationNode node = graph.addNode(id);
//		node.setAttribute("position", new Vector2d(x, y));
//		return node;
//	}
//
//	/**
//	 * Get path between start and destination node
//	 * 
//	 * @param start
//	 *            Start node
//	 * @param dest
//	 *            Destination node
//	 * @return Path
//	 */
//	protected List<NavigationEdge> getRoute(NavigationNode start, NavigationNode dest) {
//		DijkstraShortestPath algo = new DijkstraShortestPath(null,
//			start,
//			dest),
//			start,
//			dest)
//		((Dijkstra) algo).setSource(start);
//		algo.compute();
//
//		return ((Dijkstra) algo).getPath(dest);
//	}
//
//	public static void main(String args[]) throws InterruptedException {
//		TrafficVisualizerTest test = new TrafficVisualizerTest();
//		test.init();
//		test.vizualizerTest();
//	}
//}
