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
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.jgrapht.alg.DijkstraShortestPath;
import static org.junit.Assert.*;
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

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(SchedulerTest.class);

  @EJB
  private IdGenerator idGenerator;
  @EJB
  private CarFactory carFactory;
  /**
   * TrafficGraphExtensions
   */
  @EJB
  private TrafficGraphExtensions ee;
  private TrafficNode nodeA, nodeB, nodeC;
  private final TrafficGraph graph = new DefaultTrafficGraph();
  private List<TrafficEdge> shortestPath;
  private TrafficEdge ab, bc;

  public SchedulerTest() {
  }

  /**
   * Creates for vehicles for the test
   *
   * @param ee TrafficGraphExtensions
   * @return List with four vehicles
   */
  public List<Vehicle<?>> createVehicles(TrafficGraphExtensions ee) {
    List<Vehicle<?>> vehicles = new ArrayList<>();

    Vehicle<?> a1 = carFactory.createCar(graph.edgeSet());
    a1.setPath(shortestPath);
    vehicles.add(a1);

    Vehicle<?> b1 = carFactory.createCar(graph.edgeSet());
    b1.setPath(shortestPath);
    vehicles.add(b1);

    Vehicle<?> c1 = carFactory.createCar(graph.edgeSet());
    c1.setPath(shortestPath);
    vehicles.add(c1);

    Vehicle<?> d1 = carFactory.createCar(graph.edgeSet());
    d1.setPath(shortestPath);
    vehicles.add(d1);

    return vehicles;
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
    nodeA = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(0,
        0));
    nodeB = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(2,
        0));
    nodeC = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(2,
        2));
    graph.addVertex(nodeA);
    graph.addVertex(nodeB);
    graph.addVertex(nodeC);
    ab = graph.addEdge(nodeA,
      nodeB);
    graph.setEdgeWeight(ab,
      1.0);
    bc = graph.addEdge(nodeB,
      nodeC);
    graph.setEdgeWeight(bc,
      1.0);
    DijkstraShortestPath<TrafficNode, TrafficEdge> algo = new DijkstraShortestPath<>(
      graph,
      nodeA,
      nodeC,
      Double.POSITIVE_INFINITY);

    shortestPath = algo.getPathEdgeList();
  }

  /**
   * Tests the scheduler implementation
   *
   * @param scheduler Scheduler
   * @param startTime Start time of the vehicles
   * @param a
   * @param b
   * @param c
   * @param d
   */
  /*
   called from withing a @Test method
   */
  public void testScheduler(Scheduler scheduler,
    long startTime,
    Vehicle<?> a,
    Vehicle<?> b,
    Vehicle<?> c,
    Vehicle<?> d) {
    List<ScheduleItem> vehicles = scheduler.getExpiredItems(startTime);
    assertNotNull(vehicles);
    assertEquals(0,
      vehicles.size());

    List<ScheduleItem> items = scheduler.getScheduledItems();
    assertNotNull(items);
    assertEquals(4,
      items.size());

    // 1000ms
    vehicles = scheduler.getExpiredItems(startTime + 1000);
    assertEquals(1,
      vehicles.size());
    assertEquals(new ArrayList<>(Arrays.asList(a)),
      SchedulerTest.getVehicles(vehicles));

    items = scheduler.getScheduledItems();
    assertEquals(3,
      items.size());

    // 2000ms
    vehicles = scheduler.getExpiredItems(startTime + 2000);
    assertEquals(2,
      vehicles.size());
    assertEquals(new ArrayList<>(Arrays.asList(a,
      b)),
      SchedulerTest.getVehicles(vehicles));

    items = scheduler.getScheduledItems();
    assertEquals(2,
      items.size());

    // 3000ms
    vehicles = scheduler.getExpiredItems(startTime + 3000);
    assertEquals(3,
      vehicles.size());
    assertEquals(new ArrayList<>(Arrays.asList(a,
      b,
      c)),
      SchedulerTest.getVehicles(vehicles));
    assertEquals(new ArrayList<>(Arrays.asList(ab,
      bc)),
      vehicles.get(2).getVehicle().getPath()
    );

    items = scheduler.getScheduledItems();
    assertEquals(1,
      items.size());

    // 4000ms
    vehicles = scheduler.getExpiredItems(startTime + 4000);
    assertEquals(4,
      vehicles.size());
    assertEquals(new ArrayList<>(Arrays.asList(a,
      b,
      c,
      d)),
      SchedulerTest.getVehicles(vehicles));

    items = scheduler.getScheduledItems();
    assertEquals(0,
      items.size());
  }

  /**
   * Returns the list of vehicle names in such a manner: (x1, x2,...)
   *
   * @param vehicles List with vehicles
   * @return List of vehicles names as String
   */
  private static List<Vehicle<?>> getVehicles(List<ScheduleItem> vehicles) {
    List<Vehicle<?>> retValue = new LinkedList<>();
    for (ScheduleItem scheduleItem : vehicles) {
      retValue.add(scheduleItem.getVehicle());
    }
    return retValue;
  }

  @Test
  public void listSchedulerTest() throws IllegalAccessException {
    List<Vehicle<?>> vehicles = createVehicles(this.ee);

    long startTime = System.currentTimeMillis();

    Administration admin = ListScheduler.createInstance();
    admin.changeModus(ScheduleModus.WRITE);
    Scheduler scheduler = admin.getScheduler();

    long scheduleDuration = System.currentTimeMillis();

    scheduler.scheduleItem(new ScheduleItem(vehicles.get(0),
      startTime + 1000,
      1000));
    scheduler.scheduleItem(new ScheduleItem(vehicles.get(1),
      startTime + 2000,
      1000));
    scheduler.scheduleItem(new ScheduleItem(vehicles.get(2),
      startTime + 3000,
      1000));
    scheduler.scheduleItem(new ScheduleItem(vehicles.get(3),
      startTime + 4000,
      1000));

    SchedulerTest.log.info("Duration (in millis) of the ListScheduler: "
      + (System.currentTimeMillis() - scheduleDuration));

    // test
    testScheduler(scheduler,
      startTime,
      vehicles.get(0),
      vehicles.get(1),
      vehicles.get(2),
      vehicles.get(3));
  }

  @Test
  public void sortedListSchedulerTest() throws IllegalAccessException {
    List<Vehicle<?>> vehicles = createVehicles(this.ee);

    long startTime = System.currentTimeMillis();

    Administration admin = SortedListScheduler.createInstance();
    admin.changeModus(ScheduleModus.WRITE);
    Scheduler scheduler = admin.getScheduler();

    long scheduleDuration = System.currentTimeMillis();

    scheduler.scheduleItem(new ScheduleItem(vehicles.get(0),
      startTime + 1000,
      1000));
    scheduler.scheduleItem(new ScheduleItem(vehicles.get(1),
      startTime + 2000,
      1000));
    scheduler.scheduleItem(new ScheduleItem(vehicles.get(2),
      startTime + 3000,
      1000));
    scheduler.scheduleItem(new ScheduleItem(vehicles.get(3),
      startTime + 4000,
      1000));

    SchedulerTest.log.info("Duration (in millis) of the SortedListScheduler: "
      + (System.currentTimeMillis() - scheduleDuration));

    // test
    testScheduler(scheduler,
      startTime,
      vehicles.get(0),
      vehicles.get(1),
      vehicles.get(2),
      vehicles.get(3));
  }

  @Test
  public void treeSetSchedulerTest() throws IllegalAccessException {
    List<Vehicle<?>> vehicles = createVehicles(this.ee);

    long startTime = System.currentTimeMillis();

    Administration admin = TreeSetScheduler.createInstance();
    admin.changeModus(ScheduleModus.WRITE);
    Scheduler scheduler = admin.getScheduler();

    long scheduleDuration = System.currentTimeMillis();

    scheduler.scheduleItem(new ScheduleItem(vehicles.get(0),
      startTime + 1000,
      1000));
    scheduler.scheduleItem(new ScheduleItem(vehicles.get(1),
      startTime + 2000,
      1000));
    scheduler.scheduleItem(new ScheduleItem(vehicles.get(2),
      startTime + 3000,
      1000));
    scheduler.scheduleItem(new ScheduleItem(vehicles.get(3),
      startTime + 4000,
      1000));

    SchedulerTest.log.info("Duration (in millis) of the TreeSetScheduler: "
      + (System.currentTimeMillis() - scheduleDuration));

    // test
    testScheduler(scheduler,
      startTime,
      vehicles.get(0),
      vehicles.get(1),
      vehicles.get(2),
      vehicles.get(3));
  }
}
