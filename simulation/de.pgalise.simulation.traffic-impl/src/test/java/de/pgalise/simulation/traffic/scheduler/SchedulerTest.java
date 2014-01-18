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

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.server.scheduler.DefaultScheduleItem;
import de.pgalise.simulation.traffic.internal.server.scheduler.ListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.SortedListScheduler;
import de.pgalise.simulation.traffic.internal.server.scheduler.TreeSetScheduler;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the implementations of the {@link Scheduler}
 *
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
@LocalClient
@ManagedBean
public class SchedulerTest {

  @EJB
  private IdGenerator idGenerator;
  private TrafficNode a;
  private TrafficNode b;
  private TrafficNode c;
  @EJB
  private CarFactory carFactory;

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
   * @param ee TrafficGraphExtensions
   * @return List with four vehicles
   */
  public List<Vehicle<?>> createVehicles(TrafficGraphExtensions ee) {
    DijkstraShortestPath<TrafficNode, TrafficEdge> algo = new DijkstraShortestPath<>(
      graph,
      a,
      c,
      Double.POSITIVE_INFINITY);

    List<TrafficEdge> shortestPath = algo.getPathEdgeList();

    List<Vehicle<?>> vehicles = new ArrayList<>();

    Vehicle<?> a = carFactory.createCar();
    a.setPath(shortestPath);
    vehicles.add(a);

    Vehicle<?> b = carFactory.createCar();
    b.setPath(shortestPath);
    vehicles.add(b);

    Vehicle<?> c = carFactory.createCar();
    c.setPath(shortestPath);
    vehicles.add(c);

    Vehicle<?> d = carFactory.createCar();
    d.setPath(shortestPath);
    vehicles.add(d);

    return vehicles;
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
      this);
    TrafficGraph graph = new DefaultTrafficGraph();
    a = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(0,
        0));
    b = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(2,
        0));
    c = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(2,
        2));
    graph.addVertex(a);
    graph.addVertex(b);
    graph.addVertex(c);
    TrafficEdge ab = graph.addEdge(a,
      b);
    graph.setEdgeWeight(ab,
      1.0);
    TrafficEdge bc = graph.addEdge(b,
      c);
    graph.setEdgeWeight(bc,
      1.0);
    SchedulerTest.graph = graph;
  }

  /**
   * Tests the scheduler implementation
   *
   * @param scheduler Scheduler
   * @param startTime Start time of the vehicles
   */
  /*
   called from withing a @Test method
   */
  public void testScheduler(Scheduler scheduler,
    long startTime) {
    List<ScheduleItem> vehicles = null;
    vehicles = scheduler.getExpiredItems(startTime);
    Assert.assertNotNull(vehicles);
    Assert.assertEquals(0,
      vehicles.size());

    List<ScheduleItem> items = null;
    items = scheduler.getScheduledItems();
    Assert.assertNotNull(items);
    Assert.assertEquals(4,
      items.size());

    // 1000ms
    vehicles = scheduler.getExpiredItems(startTime + 1000);
    Assert.assertEquals(1,
      vehicles.size());
    Assert.assertEquals("(a)",
      SchedulerTest.getVehicles(vehicles));

    items = scheduler.getScheduledItems();
    Assert.assertEquals(3,
      items.size());

    // 2000ms
    vehicles = scheduler.getExpiredItems(startTime + 2000);
    Assert.assertEquals(2,
      vehicles.size());
    Assert.assertEquals("(a,b)",
      SchedulerTest.getVehicles(vehicles));

    items = scheduler.getScheduledItems();
    Assert.assertEquals(2,
      items.size());

    // 3000ms
    vehicles = scheduler.getExpiredItems(startTime + 3000);
    Assert.assertEquals(3,
      vehicles.size());
    Assert.assertEquals("(a,b,c)",
      SchedulerTest.getVehicles(vehicles));
    Assert.assertEquals("[a, b, c]",
      vehicles.get(2).getVehicle().getPath().toString());

    items = scheduler.getScheduledItems();
    Assert.assertEquals(1,
      items.size());

    // 4000ms
    vehicles = scheduler.getExpiredItems(startTime + 4000);
    Assert.assertEquals(4,
      vehicles.size());
    Assert.assertEquals("(a,b,c,d)",
      SchedulerTest.getVehicles(vehicles));

    items = scheduler.getScheduledItems();
    Assert.assertEquals(0,
      items.size());
  }

  /**
   * Returns the list of vehicle names in such a manner: (x1, x2,...)
   *
   * @param vehicles List with vehicles
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
  @EJB
  private TrafficGraphExtensions ee;

  @Test
  public void listSchedulerTest() throws IllegalAccessException {
    List<Vehicle<?>> vehicles = createVehicles(this.ee);

    long startTime = System.currentTimeMillis();

    Administration admin = ListScheduler.createInstance();
    admin.changeModus(ScheduleModus.WRITE);
    Scheduler scheduler = admin.getScheduler();

    long scheduleDuration = System.currentTimeMillis();

    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(0),
      startTime + 1000,
      1000));
    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(1),
      startTime + 2000,
      1000));
    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(2),
      startTime + 3000,
      1000));
    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(3),
      startTime + 4000,
      1000));

    SchedulerTest.log.info("Duration (in millis) of the ListScheduler: "
      + (System.currentTimeMillis() - scheduleDuration));

    // test
    testScheduler(scheduler,
      startTime);
  }

  @Test
  public void sortedListSchedulerTest() throws IllegalAccessException {
    List<Vehicle<?>> vehicles = createVehicles(this.ee);

    long startTime = System.currentTimeMillis();

    Administration admin = SortedListScheduler.createInstance();
    admin.changeModus(ScheduleModus.WRITE);
    Scheduler scheduler = admin.getScheduler();

    long scheduleDuration = System.currentTimeMillis();

    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(0),
      startTime + 1000,
      1000));
    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(1),
      startTime + 2000,
      1000));
    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(2),
      startTime + 3000,
      1000));
    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(3),
      startTime + 4000,
      1000));

    SchedulerTest.log.info("Duration (in millis) of the SortedListScheduler: "
      + (System.currentTimeMillis() - scheduleDuration));

    // test
    testScheduler(scheduler,
      startTime);
  }

  @Test
  public void treeSetSchedulerTest() throws IllegalAccessException {
    List<Vehicle<?>> vehicles = createVehicles(this.ee);

    long startTime = System.currentTimeMillis();

    Administration admin = TreeSetScheduler.createInstance();
    admin.changeModus(ScheduleModus.WRITE);
    Scheduler scheduler = admin.getScheduler();

    long scheduleDuration = System.currentTimeMillis();

    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(0),
      startTime + 1000,
      1000));
    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(1),
      startTime + 2000,
      1000));
    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(2),
      startTime + 3000,
      1000));
    scheduler.scheduleItem(new DefaultScheduleItem(vehicles.get(3),
      startTime + 4000,
      1000));

    SchedulerTest.log.info("Duration (in millis) of the TreeSetScheduler: "
      + (System.currentTimeMillis() - scheduleDuration));

    // test
    testScheduler(scheduler,
      startTime);
  }
}
