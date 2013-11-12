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
//package de.pgalise.simulation.traffic.model.vehicle;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.awt.Color;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.Collections;
//import java.util.List;
//
//import org.easymock.EasyMock;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import de.pgalise.simulation.service.RandomSeedService;
//import de.pgalise.simulation.sensorFramework.SensorHelper;
//import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
//import de.pgalise.simulation.traffic.TrafficGraph;
//import de.pgalise.simulation.traffic.TrafficGraphExtensions;
//import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
//import de.pgalise.simulation.traffic.internal.graphextension.DefaultTrafficGraphExtensions;
//import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
//import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultMotorizedVehicle;
//import de.pgalise.simulation.traffic.internal.model.vehicle.XMLVehicleFactory;
//import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
//import javax.vecmath.Vector2d;
//import org.jgrapht.alg.DijkstraShortestPath;
//
///**
// * Tests the attitude of a {@link Vehicle}
// * 
// * @author Marina
// * @author Mustafa
// * @version 1.0 (Nov 22, 2012)
// */
//public class VehicleTest {
//	/**
//	 * Logger
//	 */
//	private static final Logger log = LoggerFactory.getLogger(VehicleTest.class);
//
//	/**
//	 * Used car factory
//	 */
//	private CarFactory factory;
//
//	/**
//	 * TrafficGraphExtensions
//	 */
//	private TrafficGraphExtensions ee;
//
//	/**
//	 * File path of the test file
//	 */
//	public static final String FILEPATH = System.getProperty("user.dir") + "/vehicle.bin";
//	private TrafficGraph graph = new DefaultTrafficGraph();
//
//	@Before
//	public void setUp() {
//		RandomSeedService rss = EasyMock.createNiceMock(RandomSeedService.class);
//		EasyMock.replay(rss);
//		ee = new DefaultTrafficGraphExtensions(rss, graph);
//
//		factory = new XMLVehicleFactory(rss, BaseVehicle.class.getResourceAsStream("/defaultvehicles.xml"), ee);
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		// delete test file
//		File file = new File(FILEPATH);
//		if (file.exists()) {
//			file.delete();
//		}
//	}
//
//	@Test
//	public void drivingTest() throws InterruptedException {
//		DijkstraShortestPath 
//		
//		algo.init(graph);
//		algo.setSource(graph.getNode("a"));
//		algo.compute();
//
//		Path shortestPath = algo.getPath(graph.getNode("c"));
//		log.debug("Shortest path: " + shortestPath.toString());
//
//		Vehicle<?> car = new BaseVehicle<>(this.ee);
//
//		car.setCurrentNode(graph.getNode("a"));
//		car.setPosition(ee.getPosition(graph.getNode("a")));
//		car.setVelocity(1);
//		car.setName("test car");
//
//		car.setPath(shortestPath);
//
//		assertEquals(VehicleStateEnum.NOT_STARTED, car.getVehicleState());
//
//		// shoudn't do anything
//		// UpdatedData data = car.getUpdate(0);
//		// log.debug(data.toString());
//
//		assertEquals(graph.getEdge("ab"), car.getCurrentEdge());
//		assertEquals(graph.getEdge("bc"), car.getNextEdge());
//		car.update(0);
//		log.debug(String.format("x=%s, y=%s", car.getPosition().x, car.getPosition().y));
//		assertEquals(VehicleStateEnum.DRIVING, car.getVehicleState());
//		assertTrue(car.getPosition().x == 0 && car.getPosition().y == 0);
//
//		car.update(1000);
//		log.debug(String.format("x=%s, y=%s", car.getPosition().x, car.getPosition().y));
//		assertEquals(VehicleStateEnum.DRIVING, car.getVehicleState());
//		assertTrue(car.getPosition().x == 1 && car.getPosition().y == 0);
//		// dürfte sich noch nicht geändert haben
//		assertEquals(graph.getEdge("ab"), car.getCurrentEdge());
//		assertEquals(graph.getEdge("bc"), car.getNextEdge());
//
//		// bei b angekommen
//		car.update(1000);
//		log.debug(String.format("x=%s, y=%s", car.getPosition().x, car.getPosition().y));
//		assertEquals(VehicleStateEnum.DRIVING, car.getVehicleState());
//		assertEquals(2, car.getPosition().x, 0);
//		assertEquals(0, car.getPosition().y, 0);
//		assertEquals(graph.getEdge("bc"), car.getCurrentEdge());
//		assertEquals(null, car.getNextEdge());
//
//		car.update(1000);
//		log.debug(String.format("x=%s, y=%s", car.getPosition().x, car.getPosition().y));
//		assertEquals(VehicleStateEnum.DRIVING, car.getVehicleState());
//		assertTrue(car.getPosition().x == 2 && car.getPosition().y == 1);
//
//		car.update(1000);
//		log.debug(String.format("x=%s, y=%s", car.getPosition().x, car.getPosition().y));
//		assertEquals(VehicleStateEnum.REACHED_TARGET, car.getVehicleState());
//		assertTrue(car.getPosition().x == 2 && car.getPosition().y == 2);
//		assertEquals(null, car.getCurrentEdge());
//		assertEquals(null, car.getNextEdge());
//
//		car.update(1000);
//		log.debug(String.format("x=%s, y=%s", car.getPosition().x, car.getPosition().y));
//		assertEquals(VehicleStateEnum.REACHED_TARGET, car.getVehicleState());
//		assertTrue(car.getPosition().x == 2 && car.getPosition().y == 2);
//	}
//
//	@Test
//	public void multipleItermediateNodeTest() {
//		Graph graph = createGraph2();
//
//		algo.init(graph);
//		algo.setSource(graph.getNode("a"));
//		algo.compute();
//
//		Path shortestPath = algo.getPath(graph.getNode("e"));
//
//		Vehicle<?> v = new BaseVehicle<BicycleData>(null, "carA", this.ee);
//		v.setVelocity(9.5);
//		v.setPath(shortestPath);
//
//		assertEquals(0, v.getPosition().x, 0);
//		assertEquals(0, v.getPosition().y, 0);
//
//		v.update(1000);
//		assertEquals(0, v.getPosition().x, 0.0001);
//		assertEquals(3.5, v.getPosition().y, 0.0001);
//
//		v.update(1000);
//		assertEquals(0, v.getPosition().x, 0.0001);
//		assertEquals(4, v.getPosition().y, 0.0001);
//		assertEquals(VehicleStateEnum.REACHED_TARGET, v.getVehicleState());
//	}
//
//	@Test
//	public void registerOnEdgeTest() {
//		final Graph graph = createGraph();
//
//		algo.init(graph);
//		algo.setSource(graph.getNode("a"));
//		algo.compute();
//
//		Path shortestPath = algo.getPath(graph.getNode("c"));
//		log.debug("Shortest path: " + shortestPath.toString());
//
//		/**
//		 * Node a = this.addNode(graph, "a", 0, 0); Node b = this.addNode(graph, "b", 2, 0); Node c =
//		 * this.addNode(graph, "c", 2, 2);
//		 */
//
//		// Creating the cars
//		Vehicle<?> carA = factory.createRandomCar( null);
//		carA.setTrafficGraphExtensions(ee);
//		carA.setName("carA");
//		carA.setPath(shortestPath);
//		carA.setVelocity(1);
//
//		Path revPath = shortestPath.getACopy();
//		Collections.reverse(revPath.getNodePath());
//		Collections.reverse(revPath.getEdgePath());
//
//		Vehicle<?> carB = factory.createRandomCar( null);
//		carB.setTrafficGraphExtensions(ee);
//		carB.setName("carB");
//		carB.setPath(revPath);
//		carB.setVelocity(1);
//
//		carA.update(1000);
//		carB.update(1000);
//		List<Vehicle<?>> abList = graph.getEdge("ab").getAttribute("cars_a_b");
//		List<Vehicle<?>> baList = graph.getEdge("ab").getAttribute("cars_b_a");
//		List<Vehicle<?>> bcList = graph.getEdge("bc").getAttribute("cars_b_c");
//		List<Vehicle<?>> cbList = graph.getEdge("bc").getAttribute("cars_c_b");
//		Assert.assertEquals(1, abList.size(), 0);
//		Assert.assertNull(baList);
//		Assert.assertNull(bcList);
//		Assert.assertEquals(1, cbList.size(), 0);
//
//		carA.update(1000);
//		carB.update(1000);
//		abList = graph.getEdge("ab").getAttribute("cars_a_b");
//		baList = graph.getEdge("ab").getAttribute("cars_b_a");
//		bcList = graph.getEdge("bc").getAttribute("cars_b_c");
//		cbList = graph.getEdge("bc").getAttribute("cars_c_b");
//		assertEquals(0, abList.size());
//		assertEquals(0, ee.getVehiclesFor(graph.getEdge("ab"), graph.getNode("a"), graph.getNode("b")).size());
//		assertEquals(1, baList.size());
//		assertEquals(1, ee.getVehiclesFor(graph.getEdge("ab"), graph.getNode("b"), graph.getNode("a")).size());
//		assertEquals(1, bcList.size());
//		assertEquals(1, ee.getVehiclesFor(graph.getEdge("bc"), graph.getNode("b"), graph.getNode("c")).size());
//		assertEquals(0, cbList.size());
//		assertEquals(0, ee.getVehiclesFor(graph.getEdge("bc"), graph.getNode("c"), graph.getNode("b")).size());
//
//		carA.update(1000);
//		carB.update(1000);
//		abList = graph.getEdge("ab").getAttribute("cars_a_b");
//		baList = graph.getEdge("ab").getAttribute("cars_b_a");
//		bcList = graph.getEdge("bc").getAttribute("cars_b_c");
//		cbList = graph.getEdge("bc").getAttribute("cars_c_b");
//		assertEquals(0, abList.size());
//		assertEquals(0, ee.getVehiclesFor(graph.getEdge("ab"), graph.getNode("a"), graph.getNode("b")).size());
//		assertEquals(1, baList.size());
//		assertEquals(1, ee.getVehiclesFor(graph.getEdge("ab"), graph.getNode("b"), graph.getNode("a")).size());
//		assertEquals(1, bcList.size());
//		assertEquals(1, ee.getVehiclesFor(graph.getEdge("bc"), graph.getNode("b"), graph.getNode("c")).size());
//		assertEquals(0, cbList.size());
//		assertEquals(0, ee.getVehiclesFor(graph.getEdge("bc"), graph.getNode("c"), graph.getNode("b")).size());
//
//		carA.update(1000);
//		carB.update(1000);
//		abList = graph.getEdge("ab").getAttribute("cars_a_b");
//		baList = graph.getEdge("ab").getAttribute("cars_b_a");
//		bcList = graph.getEdge("bc").getAttribute("cars_b_c");
//		cbList = graph.getEdge("bc").getAttribute("cars_c_b");
//		assertEquals(0, abList.size());
//		assertEquals(0, baList.size());
//		assertEquals(0, bcList.size());
//		assertEquals(0, cbList.size());
//	}
//
//	@Test
//	public void serializableTest() throws FileNotFoundException, IOException, ClassNotFoundException {
//		log.info("################");
//		log.info("serializableTest");
//		log.info("################");
//
//		Graph graph = createGraph();
//		algo.init(graph);
//
//		Vehicle<CarData> car = createCar();
//
//		Path path = this.getShortestPath(graph.getNode("a"), graph.getNode("c"));
//
//		car.setPath(path);
//		saveVehicle(car, FILEPATH);
//
//		Vehicle<?> car2 = loadVehicle(FILEPATH, graph, car.getPath().getNodePath().get(0).getId(),
//				car.getPath().getNodePath().get(car.getPath().getNodeCount() - 1).getId());
//
//		assertEquals(car.getPath().getNodePath().get(0).getId(), car2.getPath().getNodePath().get(0).getId());
//
//		assertEquals(car.getPath().getNodePath().get(car.getPath().getNodeCount() - 1).getId(), car2.getPath()
//				.getNodePath().get(car.getPath().getNodeCount() - 1).getId());
//
//		assertEquals(car.getData().getType(), car2.getData().getType());
//	}
//
//	/**
//	 * Creae random car
//	 * 
//	 * @return Vehicle<CarData>
//	 */
//	private Vehicle<CarData> createCar() {
//		CarData data = new CarData(Color.BLACK, 0, 0, 0, 0, 0, 0, 0, 0.0d, 0, 0, "description", new SensorHelper(), VehicleTypeEnum.CAR);
//		Vehicle<CarData> car = new DefaultMotorizedVehicle<>( data, ee);
//		return car;
//	}
//
//	/**
//	 * Save vehicle to file
//	 * 
//	 * @param car
//	 *            Vehicle
//	 * @param path
//	 *            File path
//	 */
//	private void saveVehicle(Vehicle<?> car, String path) throws FileNotFoundException, IOException {
//		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
//		out.writeObject(car);
//		out.close();
//	}
//
//	/**
//	 * Load vehicle from file
//	 * 
//	 * @param path
//	 *            file path
//	 * @param graph
//	 *            Graph
//	 * @param startNodeId
//	 *            Start node
//	 * @param targetNodeId
//	 *            Traget node
//	 * @return Vehicle
//	 */
//	@SuppressWarnings("unchecked")
//	private Vehicle<?> loadVehicle(String path, Graph graph, String startNodeId, String targetNodeId)
//			throws FileNotFoundException, IOException, ClassNotFoundException {
//
//		Vehicle<?> vehicle;
//		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
//			vehicle = (Vehicle<?>) in.readObject();
//			vehicle.setTrafficGraphExtensions(ee);
//			vehicle.setPath(this.getShortestPath(graph.getNode(startNodeId), graph.getNode(targetNodeId)));
//		}
//
//		return vehicle;
//	}
//
//	/**
//	 * Calculate shortest path
//	 * 
//	 * @param start
//	 *            Node to start
//	 * @param dest
//	 *            Target node
//	 * @return shortest path
//	 */
//	private Path getShortestPath(Node start, Node dest) {
//		algo.setSource(start);
//		algo.compute();
//		return algo.getPath(dest);
//	}
//
//	/**
//	 * Create Graph
//	 * 
//	 * @return Graph
//	 */
//	private Graph createGraph2() {
//		Graph graph = new SingleGraph("city");
//		Node a = this.addNode(graph, "a", 0, 0);
//		Node b = this.addNode(graph, "b", 0, 1);
//		Node c = this.addNode(graph, "c", -1 * Math.sqrt(15), 2);
//		Node d = this.addNode(graph, "d", 0, 3);
//		Node e = this.addNode(graph, "e", 0, 4);
//
//		Edge edge = null;
//		edge = graph.addEdge("ab", a, b);
//		edge.addAttribute("weight", 1);
//
//		edge = graph.addEdge("bc", b, c);
//		edge.addAttribute("weight", 1);
//
//		edge = graph.addEdge("cd", c, d);
//		edge.addAttribute("weight", 1);
//
//		edge = graph.addEdge("de", d, e);
//		edge.addAttribute("weight", 1);
//
//		return graph;
//	}
//
//	/**
//	 * Create test graph
//	 * 
//	 * @return Graph
//	 */
//	private Graph createGraph() {
//		Graph graph = new SingleGraph("city");
//		Node a = this.addNode(graph, "a", 0, 0);
//		Node b = this.addNode(graph, "b", 2, 0);
//		Node c = this.addNode(graph, "c", 2, 2);
//
//		Edge ab = null;
//		ab = graph.addEdge("ab", a, b);
//		ab.setAttribute("weight", 1);
//		Edge bc = null;
//		bc = graph.addEdge("bc", b, c);
//		bc.setAttribute("weight", 1);
//
//		return graph;
//	}
//
//	/**
//	 * Add node to graph
//	 * 
//	 * @param graph
//	 *            Graph
//	 * @param id
//	 *            ID
//	 * @param x
//	 *            X-coordinate Y-coordinate
//	 * @param y
//	 * @return Node
//	 */
//	private Node addNode(Graph graph, String id, double x, double y) {
//		Node node = graph.addNode(id);
//		node.setAttribute("position", new Vector2d(x, y));
//		return node;
//	}
//}
