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
package de.pgalise.simulation.traffic.model.vehicle;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.BicycleData;
import de.pgalise.simulation.traffic.entity.CarData;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.testutils.TestUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import org.jgrapht.alg.DijkstraShortestPath;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the attitude of a {@link Vehicle}
 *
 * @author Marina
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
@LocalClient
@ManagedBean
public class VehicleTest {

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(VehicleTest.class);

  /**
   * TrafficGraphExtensions
   */
  @EJB
  private TrafficGraphExtensions ee;
  @EJB
  private CarFactory factory;
  @EJB
  private BicycleFactory bicycleFactory;
  @EJB
  private RandomSeedService rss;
  @EJB
  private IdGenerator idGenerator;
  private Output output = EasyMock.createNiceMock(Output.class);

  /**
   * File path of the test file
   */
  public static final String FILEPATH;

  static {
    try {
      FILEPATH = File.createTempFile("pgalise",
        null).getAbsolutePath();
    } catch (IOException ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  public VehicleTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    // delete test file
    File file = new File(FILEPATH);
    if (file.exists()) {
      file.delete();
    }
  }

  @Test
  public void drivingTest() throws InterruptedException {
    TrafficNode a = new TrafficNode(
      new Coordinate(1,
        1));
    TrafficNode b = new TrafficNode(
      new Coordinate(1,
        2));
    TrafficNode c = new TrafficNode(
      new Coordinate(2,
        2));
    TrafficGraph graph = new DefaultTrafficGraph();
    graph.addVertex(a);
    graph.addVertex(b);
    graph.addVertex(c);
    TrafficEdge ab = graph.addEdge(a,
      b);
    TrafficEdge bc = graph.addEdge(b,
      c);
    DijkstraShortestPath<TrafficNode, TrafficEdge> algo = new DijkstraShortestPath<>(
      graph,
      a,
      c);
    List<TrafficEdge> shortestPath = algo.getPathEdgeList();
    log.debug("Shortest path: " + shortestPath.toString());

    Vehicle<CarData> car = factory.createVehicle(graph.edgeSet(),
      output);

    car.setCurrentNode(a);
    car.setPosition(a);
    car.setVelocity(1);
    car.setName("test car");

    car.setPath(shortestPath);

    assertEquals(VehicleStateEnum.NOT_STARTED,
      car.getVehicleState());

    // shoudn't do anything
    // UpdatedData data = car.getUpdate(0);
    // log.debug(data.toString());
    assertEquals(ab,
      car.getCurrentEdge());
    assertEquals(bc,
      car.getNextEdge());
    car.update(0);
    log.debug(String.format("x=%s, y=%s",
      car.getPosition().getX(),
      car.getPosition().getY()));
    assertEquals(VehicleStateEnum.DRIVING,
      car.getVehicleState());
    assertTrue(
      car.getPosition().getX() == 0 && car.getPosition().getY() == 0);

    car.update(1000);
    log.debug(String.format("x=%s, y=%s",
      car.getPosition().getX(),
      car.getPosition().getY()));
    assertEquals(VehicleStateEnum.DRIVING,
      car.getVehicleState());
    assertTrue(
      car.getPosition().getX() == 1 && car.getPosition().getY() == 0);
    // dürfte sich noch nicht geändert haben
    assertEquals(ab,
      car.getCurrentEdge());
    assertEquals(bc,
      car.getNextEdge());

    // bei b angekommen
    car.update(1000);
    log.debug(String.format("x=%s, y=%s",
      car.getPosition().getX(),
      car.getPosition().getY()));
    assertEquals(VehicleStateEnum.DRIVING,
      car.getVehicleState());
    assertEquals(2,
      car.getPosition().getX(),
      0);
    assertEquals(0,
      car.getPosition().getY(),
      0);
    assertEquals(bc,
      car.getCurrentEdge());
    assertEquals(null,
      car.getNextEdge());

    car.update(1000);
    log.debug(String.format("x=%s, y=%s",
      car.getPosition().getX(),
      car.getPosition().getY()));
    assertEquals(VehicleStateEnum.DRIVING,
      car.getVehicleState());
    assertTrue(
      car.getPosition().getX() == 2 && car.getPosition().getY() == 1);

    car.update(1000);
    log.debug(String.format("x=%s, y=%s",
      car.getPosition().getX(),
      car.getPosition().getY()));
    assertEquals(VehicleStateEnum.REACHED_TARGET,
      car.getVehicleState());
    assertTrue(car.getPosition().getX() == 2 && car.getPosition().getY() == 2);
    assertEquals(null,
      car.getCurrentEdge());
    assertEquals(null,
      car.getNextEdge());

    car.update(1000);
    log.debug(String.format("x=%s, y=%s",
      car.getPosition().getX(),
      car.getPosition().getY()));
    assertEquals(VehicleStateEnum.REACHED_TARGET,
      car.getVehicleState());
    assertTrue(car.getPosition().getX() == 2 && car.getPosition().getY() == 2);
  }

  @Test
  public void multipleItermediateNodeTest() {
    TrafficGraph graph = new DefaultTrafficGraph();
    TrafficNode a = new TrafficNode(
      new Coordinate(
        0,
        0));
    TrafficNode b = new TrafficNode(
      new Coordinate(
        0,
        1));
    TrafficNode c = new TrafficNode(
      new Coordinate(
        -1 * Math.sqrt(15),
        2));
    TrafficNode d = new TrafficNode(
      new Coordinate(
        0,
        3));
    TrafficNode e = new TrafficNode(
      new Coordinate(
        0,
        4));
    graph.addVertex(a);
    graph.addVertex(b);
    graph.addVertex(c);
    graph.addVertex(d);
    graph.addVertex(e);

    TrafficEdge ab = graph.addEdge(
      a,
      b);
    graph.setEdgeWeight(ab,
      1);
    TrafficEdge bc = graph.addEdge(
      b,
      c);
    graph.setEdgeWeight(bc,
      1);
    TrafficEdge cd = graph.addEdge(
      c,
      d);
    graph.setEdgeWeight(cd,
      1);
    TrafficEdge de = graph.addEdge(
      d,
      e);
    graph.setEdgeWeight(de,
      1);
    DijkstraShortestPath<TrafficNode, TrafficEdge> algo = new DijkstraShortestPath<>(
      graph,
      a,
      e);
    List<TrafficEdge> shortestPath = algo.getPathEdgeList();

    Vehicle<BicycleData> v = bicycleFactory.createVehicle(output);
    v.setVelocity(9.5);
    v.setPath(shortestPath);

    assertEquals(0,
      v.getPosition().getX(),
      0);
    assertEquals(0,
      v.getPosition().getY(),
      0);

    v.update(1000);
    assertEquals(0,
      v.getPosition().getX(),
      0.0001);
    assertEquals(3.5,
      v.getPosition().getY(),
      0.0001);

    v.update(1000);
    assertEquals(0,
      v.getPosition().getX(),
      0.0001);
    assertEquals(4,
      v.getPosition().getY(),
      0.0001);
    assertEquals(VehicleStateEnum.REACHED_TARGET,
      v.getVehicleState());
  }

  @Test
  public void registerOnEdgeTest() {
    TrafficGraph graph = new DefaultTrafficGraph();
    TrafficNode a = new TrafficNode(
      new Coordinate(
        0,
        0));
    TrafficNode b = new TrafficNode(
      new Coordinate(
        2,
        0));
    TrafficNode c = new TrafficNode(
      new Coordinate(
        2,
        2));
    graph.addVertex(a);
    graph.addVertex(b);
    graph.addVertex(c);

    TrafficEdge ab = graph.addEdge(
      a,
      b);
    graph.setEdgeWeight(ab,
      1);
    TrafficEdge bc = graph.addEdge(
      b,
      c);
    graph.setEdgeWeight(bc,
      1);
    DijkstraShortestPath<TrafficNode, TrafficEdge> algo = new DijkstraShortestPath<>(
      graph,
      a,
      c);
    List<TrafficEdge> shortestPath = algo.getPathEdgeList();
    log.debug("Shortest path: " + shortestPath.toString());

    /**
     * Node a = this.addNode(graph, "a", 0, 0); Node b = this.addNode(graph,
     * "b", 2, 0); Node c = this.addNode(graph, "c", 2, 2);
     */
    // Creating the cars
    Vehicle<?> carA = factory.createVehicle(graph.edgeSet(),
      output);
    carA.setTrafficGraphExtensions(ee);
    carA.setName("carA");
    carA.setPath(shortestPath);
    carA.setVelocity(1);
    carA.setCurrentNode(shortestPath.get(0).getSource());

    List<TrafficEdge> revPath = new LinkedList<>(shortestPath);
    Collections.reverse(revPath);
    Collections.reverse(revPath);

    Vehicle<?> carB = factory.createVehicle(graph.edgeSet(),
      output);
    carB.setTrafficGraphExtensions(ee);
    carB.setName("carB");
    carB.setPath(revPath);
    carB.setVelocity(1);
    carB.setCurrentNode(revPath.get(0).getSource());

    carA.update(1000);
    carB.update(1000);
    Set<Vehicle<?>> abList = ab.getVehicles();
    Set<Vehicle<?>> baList = ab.getVehicles();
    Set<Vehicle<?>> bcList = bc.getVehicles();
    Set<Vehicle<?>> cbList = bc.getVehicles();
    assertEquals(1,
      abList.size(),
      0);
    assertNull(baList);
    assertNull(bcList);
    assertEquals(1,
      cbList.size(),
      0);

    carA.update(1000);
    carB.update(1000);
    abList = ab.getVehicles();
    baList = ab.getVehicles();
    bcList = bc.getVehicles();
    cbList = bc.getVehicles();
    assertEquals(0,
      abList.size());
    assertEquals(1,
      baList.size());
    assertEquals(1,
      bcList.size());
    assertEquals(0,
      cbList.size());

    carA.update(1000);
    carB.update(1000);
    abList = ab.getVehicles();
    baList = ab.getVehicles();
    bcList = bc.getVehicles();
    cbList = bc.getVehicles();
    assertEquals(0,
      abList.size());
    assertEquals(1,
      baList.size());
    assertEquals(1,
      bcList.size());
    assertEquals(0,
      cbList.size());

    carA.update(1000);
    carB.update(1000);
    abList = ab.getVehicles();
    baList = ab.getVehicles();
    bcList = bc.getVehicles();
    cbList = bc.getVehicles();
    assertEquals(0,
      abList.size());
    assertEquals(0,
      baList.size());
    assertEquals(0,
      bcList.size());
    assertEquals(0,
      cbList.size());
  }

  @Test
  public void serializableTest() throws FileNotFoundException, IOException, ClassNotFoundException {
    log.info("################");
    log.info("serializableTest");
    log.info("################");

    TrafficGraph graph = new DefaultTrafficGraph();
    TrafficNode a = new TrafficNode(
      new Coordinate(
        0,
        0));
    TrafficNode b = new TrafficNode(
      new Coordinate(
        2,
        0));
    TrafficNode c = new TrafficNode(
      new Coordinate(
        2,
        2));
    graph.addVertex(a);
    graph.addVertex(b);
    graph.addVertex(c);

    TrafficEdge ab = graph.addEdge(
      a,
      b);
    graph.setEdgeWeight(ab,
      1);
    TrafficEdge bc = graph.addEdge(
      b,
      c);
    graph.setEdgeWeight(bc,
      1);

    Vehicle<CarData> originalCar = factory.createVehicle(graph.edgeSet(),
      output);

    DijkstraShortestPath<TrafficNode, TrafficEdge> algo = new DijkstraShortestPath<>(
      graph,
      a,
      c);
    List<TrafficEdge> shortestPath = algo.getPathEdgeList();

    originalCar.setPath(shortestPath);
    saveVehicle(originalCar,
      FILEPATH);

    Vehicle<?> deserializedCar = deserializeVehicle(FILEPATH,
      graph,
      originalCar.getPath().get(0).getSource(),
      originalCar.getPath().get(originalCar.getPath().size() - 1).getSource());

    assertEquals(originalCar.getPath().get(0).getId(),
      deserializedCar.getPath().get(0).getId());

    assertEquals(originalCar.getPath().get(
      originalCar.getPath().size() - 1).getId(),
      deserializedCar.getPath().get(deserializedCar.getPath().size() - 1).
      getId());

    assertEquals(originalCar.getData().getType(),
      deserializedCar.getData().getType());
  }

  /**
   * Save vehicle to file
   *
   * @param car Vehicle
   * @param path File path
   */
  private void saveVehicle(Vehicle<?> car,
    String path) throws FileNotFoundException, IOException {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
      path))) {
      out.writeObject(car);
    }
  }

  /**
   * Load vehicle from file
   *
   * @param path file path
   * @param graph Graph
   * @param startNodeId Start node
   * @param targetNodeId Traget node
   * @return Vehicle
   */
  @SuppressWarnings(
    "unchecked")
  private Vehicle<?> deserializeVehicle(String path,
    TrafficGraph graph,
    TrafficNode startNodeId,
    TrafficNode targetNodeId)
    throws FileNotFoundException, IOException, ClassNotFoundException {

    Vehicle<?> vehicle;
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
      vehicle = (Vehicle<?>) in.readObject();
      vehicle.setTrafficGraphExtensions(ee);
      vehicle.setPath(this.getShortestPath(startNodeId,
        targetNodeId,
        graph));
    }

    return vehicle;
  }

  /**
   * Calculate shortest path
   *
   * @param start Node to start
   * @param dest Target node
   * @return shortest path
   */
  private List<TrafficEdge> getShortestPath(TrafficNode start,
    TrafficNode dest,
    TrafficGraph graph) {
    DijkstraShortestPath<TrafficNode, TrafficEdge> algo = new DijkstraShortestPath<>(
      graph,
      start,
      dest);
    return algo.getPathEdgeList();
  }
}
