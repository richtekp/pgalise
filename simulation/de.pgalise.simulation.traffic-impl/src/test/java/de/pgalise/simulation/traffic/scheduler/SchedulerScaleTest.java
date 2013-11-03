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
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.graphextension.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.internal.server.scheduler.ListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.SortedListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.TreeSetScheduler;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.Administration;
import de.pgalise.simulation.traffic.internal.server.scheduler.DefaultScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleModus;
import javax.vecmath.Vector2d;

/**
 * Tests the implementations of the {@link Scheduler} against duration
 * 
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
@Ignore
public class SchedulerScaleTest {

	/**
	 * Graph
	 */
	private static Graph graph;

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(SchedulerScaleTest.class);

	/**
	 * Amount of scheduled vehicles
	 */
	public static final int NUMBER_OF_VEHCILES = 40000;

	/**
	 * Schedule interval
	 */
	public static final int SCHEDULE_INTERVAL = 1000;

	/**
	 * Creates for vehicles for the test
	 * 
	 * @param ee
	 *            TrafficGraphExtensions
	 * @param numberOfCars
	 *            Amount of cars to create
	 * @return List with four vehicles
	 */
	public static List<Vehicle<? extends VehicleData,N,E>> createVehicles(TrafficGraphExtensions ee, int numberOfCars) {
		Dijkstra algo = new Dijkstra(Element.NODE, "weight", null);
		algo.init(SchedulerScaleTest.graph);
		algo.setSource(SchedulerScaleTest.graph.getNode("a"));
		algo.compute();

		Path shortestPath = algo.getPath(SchedulerScaleTest.graph.getNode("c"));

		List<Vehicle<? extends VehicleData,N,E>> vehicles = new ArrayList<>();

		for (int i = 0; i < numberOfCars; i++) {
			Vehicle<? extends VehicleData,N,E> a = new BaseVehicle<>(ee);
			a.setName("" + i);
			a.setPath(shortestPath);
			vehicles.add(a);
		}

		return vehicles;
	}

	@BeforeClass
	public static void init() {
		SchedulerScaleTest.graph = SchedulerScaleTest.createGraph();
	}

	/**
	 * Tests the scheduler implementation
	 * 
	 * @param schedulers
	 *            List with schedulers
	 * @param startTime
	 *            Start time of the vehicles
	 * @param numberOfCars
	 *            Amount of cars
	 */
	public static void testScheduler(long startTime, List<Scheduler> schedulers, int numberOfCars) {
		List<DefaultScheduleItem> vehicles = null;
		List<DefaultScheduleItem> items = null;

		for (int i = 0; i < numberOfCars; i++) {
			for (Scheduler scheduler : schedulers) {
				// Get vehicles
				vehicles = scheduler.getExpiredItems(startTime + (i * SCHEDULE_INTERVAL));
				Assert.assertEquals((i + 1), vehicles.size());

				// Get items
				items = scheduler.getScheduledItems();
				Assert.assertEquals((numberOfCars - (i + 1)), items.size());
			}

		}
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
		Node a = SchedulerScaleTest.addNode(graph, "a", 0, 0);
		Node b = SchedulerScaleTest.addNode(graph, "b", 2, 0);
		Node c = SchedulerScaleTest.addNode(graph, "c", 2, 2);

		Edge ab = null;
		ab = graph.addEdge("ab", a, b);
		ab.setAttribute("weight", 1);
		Edge bc = null;
		bc = graph.addEdge("bc", b, c);
		bc.setAttribute("weight", 1);

		return graph;
	}

	/**
	 * TrafficGraphExtensions
	 */
	private TrafficGraphExtensions ee;

	@Test
	public void scaleTest() throws IllegalAccessException {
		/*
		 * Preparation
		 */
		List<Vehicle<? extends VehicleData,N,E>> vehicles = SchedulerScaleTest.createVehicles(this.ee, NUMBER_OF_VEHCILES);
		long startTime = System.currentTimeMillis();
		List<Scheduler> schedulers = new ArrayList<>();

		/*
		 * ListScheduler (default scheduler of Mustafa)
		 */
		Administration admin = ListScheduler.createInstance();
		admin.changeModus(ScheduleModus.WRITE);
		Scheduler scheduler = admin.getScheduler();
		// schedulers.add(scheduler);

		// // Schedule
		long scheduleDuration = System.currentTimeMillis();
		// for (int i = 0; i < vehicles.size(); i++) {
		// scheduler.scheduleItem(new Item(vehicles.get(i), startTime + (i * SCHEDULE_INTERVAL), 1000));
		// }
		// SchedulerScaleTest.log.info("Duration (in millis) of the ListScheduler: "
		// + (System.currentTimeMillis() - scheduleDuration));

		/*
		 * SortedListScheduler
		 */
		admin = SortedListScheduler.createInstance();
		admin.changeModus(ScheduleModus.WRITE);
		scheduler = admin.getScheduler();
		schedulers.add(scheduler);

		// Schedule
		scheduleDuration = System.currentTimeMillis();
		for (int i = 0; i < vehicles.size(); i++) {
			scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(i), startTime + (i * SCHEDULE_INTERVAL), 1000));
		}
		log.info("Duration (in millis) of the SortedListScheduler: " + (System.currentTimeMillis() - scheduleDuration));

		/*
		 * SortedListScheduler
		 */
		admin = TreeSetScheduler.createInstance();
		admin.changeModus(ScheduleModus.WRITE);
		scheduler = admin.getScheduler();
		schedulers.add(scheduler);

		// Schedule
		scheduleDuration = System.currentTimeMillis();
		for (int i = 0; i < vehicles.size(); i++) {
			scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(i), startTime + (i * SCHEDULE_INTERVAL), 1000));
		}
		log.info("Duration (in millis) of the TreeSetScheduler: " + (System.currentTimeMillis() - scheduleDuration));

		/*
		 * TEST
		 */
		SchedulerScaleTest.testScheduler(startTime, schedulers, NUMBER_OF_VEHCILES);
	}

	@Before
	public void setUp() {
		this.ee = new DefaultTrafficGraphExtensions(new DefaultRandomSeedService());
	}
}
