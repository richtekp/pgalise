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

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.graphextension.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.server.scheduler.ListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.SortedListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.TreeSetScheduler;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.scheduler.Administration;
import de.pgalise.simulation.traffic.internal.server.scheduler.DefaultScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleModus;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultCar;
import org.jgrapht.alg.DijkstraShortestPath;

/**
 * Tests the implementations of the {@link Scheduler}
 * 
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
public class SchedulerTest {
	private static TrafficNode a = new TrafficNode(new JaxRSCoordinate( 0, 0));
	private static TrafficNode b = new TrafficNode(new JaxRSCoordinate( 2, 0));
	private static TrafficNode c = new TrafficNode(new JaxRSCoordinate( 2, 2));

	/**
	 * Graph
	 */
	private static TrafficGraph graph;

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
	public static List<Vehicle<?>> createVehicles(TrafficGraphExtensions ee) {
		DijkstraShortestPath<TrafficNode, TrafficEdge> algo = new DijkstraShortestPath<>(graph, a, c, Double.POSITIVE_INFINITY);

		List<TrafficEdge> shortestPath = algo.getPathEdgeList();

		List<Vehicle<?>> vehicles = new ArrayList<>();

		Vehicle<?> a = new DefaultCar(1L,"a", null,ee);
		a.setPath(shortestPath);
		vehicles.add(a);

		Vehicle<?> b = new DefaultCar(2L,"b",null,ee);
		b.setPath(shortestPath);
		vehicles.add(b);

		Vehicle<?> c = new DefaultCar(3L,"c",null,ee);
		c.setPath(shortestPath);
		vehicles.add(c);

		Vehicle<?> d = new DefaultCar(4L,"d",null,ee);
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
		List<ScheduleItem> vehicles = null;
		vehicles = scheduler.getExpiredItems(startTime);
		Assert.assertNotNull(vehicles);
		Assert.assertEquals(0, vehicles.size());

		List<ScheduleItem> items = null;
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
	 * Create a test graph
	 * 
	 * @return Graph
	 */
	private static TrafficGraph createGraph() {

		TrafficGraph graph = new DefaultTrafficGraph();

		TrafficEdge ab = graph.addEdge(a,
			b);
		graph.setEdgeWeight(ab,
			1);
		TrafficEdge bc = graph.addEdge(b,
			c);
		graph.setEdgeWeight(bc,
			1);

		return graph;
	}

	/**
	 * Returns the list of vehicle names in such a manner: (x1, x2,...)
	 * 
	 * @param vehicles
	 *            List with vehicles
	 * @return List of vehicles names as String
	 */
	private static String getVehicles(List<ScheduleItem> vehicles) {
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
		List<Vehicle<?>> vehicles = SchedulerTest.createVehicles(this.ee);

		long startTime = System.currentTimeMillis();

		Administration admin = ListScheduler.createInstance();
		admin.changeModus(ScheduleModus.WRITE);
		Scheduler scheduler = admin.getScheduler();

		long scheduleDuration = System.currentTimeMillis();

		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(0), startTime + 1000, 1000));
		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(1), startTime + 2000, 1000));
		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(2), startTime + 3000, 1000));
		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(3), startTime + 4000, 1000));

		SchedulerTest.log.info("Duration (in millis) of the ListScheduler: "
				+ (System.currentTimeMillis() - scheduleDuration));

		// test
		SchedulerTest.testScheduler(scheduler, startTime);
	}

	@Test
	public void sortedListSchedulerTest() throws IllegalAccessException {
		List<Vehicle<?>> vehicles = SchedulerTest.createVehicles(this.ee);

		long startTime = System.currentTimeMillis();

		Administration admin = SortedListScheduler.createInstance();
		admin.changeModus(ScheduleModus.WRITE);
		Scheduler scheduler = admin.getScheduler();

		long scheduleDuration = System.currentTimeMillis();

		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(0), startTime + 1000, 1000));
		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(1), startTime + 2000, 1000));
		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(2), startTime + 3000, 1000));
		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(3), startTime + 4000, 1000));

		SchedulerTest.log.info("Duration (in millis) of the SortedListScheduler: "
				+ (System.currentTimeMillis() - scheduleDuration));

		// test
		SchedulerTest.testScheduler(scheduler, startTime);
	}

	@Test
	public void treeSetSchedulerTest() throws IllegalAccessException {
		List<Vehicle<?>> vehicles = SchedulerTest.createVehicles(this.ee);

		long startTime = System.currentTimeMillis();

		Administration admin = TreeSetScheduler.createInstance();
		admin.changeModus(ScheduleModus.WRITE);
		Scheduler scheduler = admin.getScheduler();

		long scheduleDuration = System.currentTimeMillis();

		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(0), startTime + 1000, 1000));
		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(1), startTime + 2000, 1000));
		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(2), startTime + 3000, 1000));
		scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(3), startTime + 4000, 1000));

		SchedulerTest.log.info("Duration (in millis) of the TreeSetScheduler: "
				+ (System.currentTimeMillis() - scheduleDuration));

		// test
		SchedulerTest.testScheduler(scheduler, startTime);
	}

	@Before
	public void setUp() {
		this.ee = new DefaultTrafficGraphExtensions(new DefaultRandomSeedService(), graph);
	}
}
