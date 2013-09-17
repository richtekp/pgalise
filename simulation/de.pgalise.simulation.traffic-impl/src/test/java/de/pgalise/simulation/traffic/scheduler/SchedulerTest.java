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
 
package de.pgalise.simulation.traffic.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.internal.server.scheduler.ListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.SortedListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.TreeSetScheduler;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.Administration;
import de.pgalise.simulation.traffic.server.scheduler.Item;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler.Modus;
import javax.vecmath.Vector2d;

/**
 * Tests the implementations of the {@link Scheduler}
 * 
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
public class SchedulerTest {

	/**
	 * Graph
	 */
	private static Graph graph;

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(SchedulerTest.class);

	/**
	 * Creates for vehicles for the test
	 * 
	 * @param ee
	 *            TrafficGraphExtensions
	 * @return List with four vehicles
	 */
	public static List<Vehicle<? extends VehicleData>> createVehicles(TrafficGraphExtensions ee) {
		Dijkstra algo = new Dijkstra(Element.NODE, "weight", null);
		algo.init(SchedulerTest.graph);
		algo.setSource(SchedulerTest.graph.getNode("a"));
		algo.compute();

		Path shortestPath = algo.getPath(SchedulerTest.graph.getNode("c"));

		List<Vehicle<? extends VehicleData>> vehicles = new ArrayList<>();

		Vehicle<? extends VehicleData> a = new BaseVehicle<>(ee);
		a.setName("a");
		a.setPath(shortestPath);
		vehicles.add(a);

		Vehicle<? extends VehicleData> b = new BaseVehicle<>(ee);
		b.setName("b");
		b.setPath(shortestPath);
		vehicles.add(b);

		Vehicle<? extends VehicleData> c = new BaseVehicle<>(ee);
		c.setName("c");
		c.setPath(shortestPath);
		vehicles.add(c);

		Vehicle<? extends VehicleData> d = new BaseVehicle<>(ee);
		d.setName("d");
		d.setPath(shortestPath);
		vehicles.add(d);

		return vehicles;
	}

	@BeforeClass
	public static void init() {
		SchedulerTest.graph = SchedulerTest.createGraph();
	}

	/**
	 * Tests the scheduler implementation
	 * 
	 * @param scheduler
	 *            Scheduler
	 * @param startTime
	 *            Start time of the vehicles
	 */
	public static void testScheduler(Scheduler scheduler, long startTime) {
		List<Item> vehicles = null;
		vehicles = scheduler.getExpiredItems(startTime);
		Assert.assertNotNull(vehicles);
		Assert.assertEquals(0, vehicles.size());

		List<Item> items = null;
		items = scheduler.getScheduledItems();
		Assert.assertNotNull(items);
		Assert.assertEquals(4, items.size());

		// 1000ms
		vehicles = scheduler.getExpiredItems(startTime + 1000);
		Assert.assertEquals(1, vehicles.size());
		Assert.assertEquals("(a)", SchedulerTest.getVehicles(vehicles));

		items = scheduler.getScheduledItems();
		Assert.assertEquals(3, items.size());

		// 2000ms
		vehicles = scheduler.getExpiredItems(startTime + 2000);
		Assert.assertEquals(2, vehicles.size());
		Assert.assertEquals("(a,b)", SchedulerTest.getVehicles(vehicles));

		items = scheduler.getScheduledItems();
		Assert.assertEquals(2, items.size());

		// 3000ms
		vehicles = scheduler.getExpiredItems(startTime + 3000);
		Assert.assertEquals(3, vehicles.size());
		Assert.assertEquals("(a,b,c)", SchedulerTest.getVehicles(vehicles));
		Assert.assertEquals("[a, b, c]", vehicles.get(2).getVehicle().getPath().toString());

		items = scheduler.getScheduledItems();
		Assert.assertEquals(1, items.size());

		// 4000ms
		vehicles = scheduler.getExpiredItems(startTime + 4000);
		Assert.assertEquals(4, vehicles.size());
		Assert.assertEquals("(a,b,c,d)", SchedulerTest.getVehicles(vehicles));

		items = scheduler.getScheduledItems();
		Assert.assertEquals(0, items.size());
	}

	/**
	 * Add nodes to the graph
	 * 
	 * @param graph
	 *            Graph
	 * @param id
	 *            ID
	 * @param x
	 *            X-coordinate
	 * @param y
	 *            Y-coordinate
	 * @return Node
	 */
	private static Node addNode(Graph graph, String id, double x, double y) {
		Node node = graph.addNode(id);
		node.setAttribute("position", new Vector2d(x, y));
		return node;
	}

	/**
	 * Create a test graph
	 * 
	 * @return Graph
	 */
	private static Graph createGraph() {
		Graph graph = new SingleGraph("city");
		Node a = SchedulerTest.addNode(graph, "a", 0, 0);
		Node b = SchedulerTest.addNode(graph, "b", 2, 0);
		Node c = SchedulerTest.addNode(graph, "c", 2, 2);

		Edge ab = null;
		ab = graph.addEdge("ab", a, b);
		ab.setAttribute("weight", 1);
		Edge bc = null;
		bc = graph.addEdge("bc", b, c);
		bc.setAttribute("weight", 1);

		return graph;
	}

	/**
	 * Returns the list of vehicle names in such a manner: (x1, x2,...)
	 * 
	 * @param vehicles
	 *            List with vehicles
	 * @return List of vehicles names as String
	 */
	private static String getVehicles(List<Item> vehicles) {
		StringBuilder str = new StringBuilder();
		str.append("(");
		for (int i = 0; i < vehicles.size(); i++) {
			if (i != (vehicles.size() - 1)) {
				str.append(vehicles.get(i).getVehicle().getName() + ",");
			} else {
				str.append(vehicles.get(i).getVehicle().getName());
			}
		}
		str.append(")");

		return str.toString();
	}

	/**
	 * TrafficGraphExtensions
	 */
	private TrafficGraphExtensions ee;

	@Test
	public void listSchedulerTest() throws IllegalAccessException {
		List<Vehicle<? extends VehicleData>> vehicles = SchedulerTest.createVehicles(this.ee);

		long startTime = System.currentTimeMillis();

		Administration admin = ListScheduler.createInstance();
		admin.changeModus(Modus.WRITE);
		Scheduler scheduler = admin.getScheduler();

		long scheduleDuration = System.currentTimeMillis();

		scheduler.scheduleItem(new Item(vehicles.get(0), startTime + 1000, 1000));
		scheduler.scheduleItem(new Item(vehicles.get(1), startTime + 2000, 1000));
		scheduler.scheduleItem(new Item(vehicles.get(2), startTime + 3000, 1000));
		scheduler.scheduleItem(new Item(vehicles.get(3), startTime + 4000, 1000));

		SchedulerTest.log.info("Duration (in millis) of the ListScheduler: "
				+ (System.currentTimeMillis() - scheduleDuration));

		// test
		SchedulerTest.testScheduler(scheduler, startTime);
	}

	@Test
	public void sortedListSchedulerTest() throws IllegalAccessException {
		List<Vehicle<? extends VehicleData>> vehicles = SchedulerTest.createVehicles(this.ee);

		long startTime = System.currentTimeMillis();

		Administration admin = SortedListScheduler.createInstance();
		admin.changeModus(Modus.WRITE);
		Scheduler scheduler = admin.getScheduler();

		long scheduleDuration = System.currentTimeMillis();

		scheduler.scheduleItem(new Item(vehicles.get(0), startTime + 1000, 1000));
		scheduler.scheduleItem(new Item(vehicles.get(1), startTime + 2000, 1000));
		scheduler.scheduleItem(new Item(vehicles.get(2), startTime + 3000, 1000));
		scheduler.scheduleItem(new Item(vehicles.get(3), startTime + 4000, 1000));

		SchedulerTest.log.info("Duration (in millis) of the SortedListScheduler: "
				+ (System.currentTimeMillis() - scheduleDuration));

		// test
		SchedulerTest.testScheduler(scheduler, startTime);
	}

	@Test
	public void treeSetSchedulerTest() throws IllegalAccessException {
		List<Vehicle<? extends VehicleData>> vehicles = SchedulerTest.createVehicles(this.ee);

		long startTime = System.currentTimeMillis();

		Administration admin = TreeSetScheduler.createInstance();
		admin.changeModus(Modus.WRITE);
		Scheduler scheduler = admin.getScheduler();

		long scheduleDuration = System.currentTimeMillis();

		scheduler.scheduleItem(new Item(vehicles.get(0), startTime + 1000, 1000));
		scheduler.scheduleItem(new Item(vehicles.get(1), startTime + 2000, 1000));
		scheduler.scheduleItem(new Item(vehicles.get(2), startTime + 3000, 1000));
		scheduler.scheduleItem(new Item(vehicles.get(3), startTime + 4000, 1000));

		SchedulerTest.log.info("Duration (in millis) of the TreeSetScheduler: "
				+ (System.currentTimeMillis() - scheduleDuration));

		// test
		SchedulerTest.testScheduler(scheduler, startTime);
	}

	@Before
	public void setUp() {
		this.ee = new DefaultTrafficGraphExtensions(new DefaultRandomSeedService());
	}
}
