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
 *//* 
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

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.graphextension.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultCar;
import de.pgalise.simulation.traffic.internal.server.scheduler.ListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.SortedListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.TreeSetScheduler;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.scheduler.Administration;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleModus;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import de.pgalise.testutils.TestUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.jgrapht.alg.DijkstraShortestPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the implementations of the {@link Scheduler} against duration
 *
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
@Ignore
@LocalClient
@ManagedBean
public class SchedulerScaleTest {

  @EJB
  private IdGenerator idGenerator;
  private TrafficNode a;
  private TrafficNode b;
  private TrafficNode c;
  /**
   * Graph
   */
  private static TrafficGraph graph;

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(
    SchedulerScaleTest.class);

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
   * @param ee TrafficGraphExtensions
   * @param numberOfCars Amount of cars to create
   * @return List with four vehicles
   */
  public List<Vehicle<?>> createVehicles(TrafficGraphExtensions ee,
    int numberOfCars) {
    DijkstraShortestPath<TrafficNode, TrafficEdge> algo = new DijkstraShortestPath<>(
      graph,
      a,
      c,
      Double.POSITIVE_INFINITY);

    List<TrafficEdge> shortestPath = algo.getPathEdgeList();

    List<Vehicle<?>> vehicles = new ArrayList<>();

    for (int i = 0; i < numberOfCars; i++) {
      Vehicle<?> a = new DefaultCar(89324L,
        null,
        ee);
      a.setPath(shortestPath);
      vehicles.add(a);
    }

    return vehicles;
  }

  public SchedulerScaleTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
    a = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(0,
        0));
    b = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(2,
        0));
    c = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(2,
        2));
    this.trafficGraphExtensions = new DefaultTrafficGraphExtensions(
      new DefaultRandomSeedService(),
      graph);
    SchedulerScaleTest.graph = createGraph();
  }

  /**
   * Tests the scheduler implementation
   *
   * @param schedulers List with schedulers
   * @param startTime Start time of the vehicles
   * @param numberOfCars Amount of cars
   */
  public static void testScheduler(long startTime,
    List<Scheduler> schedulers,
    int numberOfCars) {
    List<ScheduleItem> vehicles = null;
    List<ScheduleItem> items = null;

    for (int i = 0; i < numberOfCars; i++) {
      for (Scheduler scheduler : schedulers) {
        // Get vehicles
        vehicles = scheduler.
          getExpiredItems(startTime + (i * SCHEDULE_INTERVAL));
        Assert.assertEquals((i + 1),
          vehicles.size());

        // Get items
        items = scheduler.getScheduledItems();
        Assert.assertEquals((numberOfCars - (i + 1)),
          items.size());
      }

    }
  }

  /**
   * Create a test graph
   *
   * @return Graph
   */
  private TrafficGraph createGraph() {
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
   * TrafficGraphExtensions
   */
  @EJB
  private TrafficGraphExtensions trafficGraphExtensions;

  @Test
  public void scaleTest() throws IllegalAccessException {
    /*
     * Preparation
     */
    List<Vehicle<?>> vehicles = createVehicles(this.trafficGraphExtensions,
      NUMBER_OF_VEHCILES);
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
      scheduler.scheduleItem(new ScheduleItem(vehicles.get(i),
        startTime + (i * SCHEDULE_INTERVAL),
        1000));
    }
    log.info("Duration (in millis) of the SortedListScheduler: " + (System.
      currentTimeMillis() - scheduleDuration));

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
      scheduler.scheduleItem(new ScheduleItem(vehicles.get(i),
        startTime + (i * SCHEDULE_INTERVAL),
        1000));
    }
    log.info("Duration (in millis) of the TreeSetScheduler: " + (System.
      currentTimeMillis() - scheduleDuration));

    /*
     * TEST
     */
    SchedulerScaleTest.testScheduler(startTime,
      schedulers,
      NUMBER_OF_VEHCILES);
  }
}
