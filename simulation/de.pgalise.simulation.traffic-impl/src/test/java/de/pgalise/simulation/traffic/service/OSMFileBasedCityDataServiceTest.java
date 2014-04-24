/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.entity.Way;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.testutils.TestUtils;
import de.pgalise.util.cityinfrastructure.impl.GraphConstructor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class OSMFileBasedCityDataServiceTest {

  static {
    if (System.getProperty("org.jboss.logging.provider") == null) {
      try {
        InputStream testPropertiesInputStream = Thread.currentThread().
          getContextClassLoader().getResourceAsStream("test.properties");
        Properties testProperties = new Properties();
        testProperties.load(testPropertiesInputStream);
        System.setProperty("org.jboss.logging.provider",
          testProperties.getProperty("org.jboss.logging.provider"));
      } catch (IOException ex) {
        throw new ExceptionInInitializerError(ex);
      }
    }
  }

  private final static String OSM_FILE_NAME
    = "dbis_institute_berlin_reduced.osm";
//    "oldenburg_pg.osm";
  @EJB
  private IdGenerator idGenerator;
  @EJB
  private GraphConstructor graphConstructor;

  public OSMFileBasedCityDataServiceTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
      this);
  }

  /**
   * Test of getBoundary method, of class OSMFileBasedCityDataService.
   */
  @Test
  @Ignore
  public void testGetBoundary() throws Exception {
    System.out.println("getBoundary");
    OSMFileBasedCityDataService instance = new OSMFileBasedCityDataService();
    Envelope expResult = null;
    Polygon result = instance.createCity().retrieveBoundary();
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of initialize method, of class OSMFileBasedCityDataService.
   */
  @Test
  @Ignore
  public void testInitialize() throws Exception {
    System.out.println("initialize");
    OSMFileBasedCityDataService instance = new OSMFileBasedCityDataService();
    instance.initialize();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getTrafficGraph method, of class OSMFileBasedCityDataService.
   */
  @Test
  @Ignore
  public void testGetTrafficGraph() throws Exception {
    System.out.println("getTrafficGraph");
    OSMFileBasedCityDataService instance = new OSMFileBasedCityDataService();
    TrafficGraph expResult = null;
    TrafficGraph result = instance.getTrafficGraph();
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getBuildingEnergyProfileMap method, of class
   * OSMFileBasedCityDataService.
   */
  @Test
  @Ignore
  public void testGetBuildings() throws Exception {
    System.out.println("getBuildings");
    BaseCoordinate geolocation = null;
    int radiusInMeter = 0;
    OSMFileBasedCityDataService instance = new OSMFileBasedCityDataService();
    Map<EnergyProfileEnum, List<Building>> expResult = null;
    Map<EnergyProfileEnum, List<Building>> result = instance.
      getBuildingEnergyProfileMap(
        geolocation,
        radiusInMeter);
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getBuildingsInRadius method, of class OSMFileBasedCityDataService.
   */
  @Test
  public void testGetBuildingsInRadius() throws Exception {
    int radiusInMeter = 500;
    TrafficGraph trafficGraph = new DefaultTrafficGraph();
    TrafficCity cityInfrastructureData = new TrafficCity(idGenerator.getNextId(),
      new CityInfrastructureData(idGenerator.getNextId()));
    OSMFileBasedCityDataService osmParser = new OSMFileBasedCityDataService(
      idGenerator,
      trafficGraph,
      graphConstructor,
      cityInfrastructureData);
    InputStream osmIN = Thread.currentThread().getContextClassLoader().
      getResourceAsStream(OSM_FILE_NAME);
    osmParser.parseStream(osmIN);
    List<TrafficNode> randomNodeList = new LinkedList<>(osmParser.createCity().
      getCityInfrastructureData().getNodes());
    Collections.shuffle(randomNodeList);
    NavigationNode randomNode
      = randomNodeList.get((int) (Math.random() * randomNodeList.size()));
    BaseCoordinate centerPoint = new BaseCoordinate(
      randomNode.getX(),
      randomNode.getY()
    );
    for (Building building : osmParser.getBuildingsInRadius(centerPoint,
      radiusInMeter)) {
      assertTrue(this.getDistanceInMeter(centerPoint,
        building.retrieveCenterPoint()) <= radiusInMeter);
    }
  }

  /**
   * Test of getNearestStreetNode method, of class OSMFileBasedCityDataService.
   */
  @Test
  public void testGetNearestStreetNode() throws Exception {
    /* Take one node and test, if it will be returned: */
    TrafficGraph trafficGraph = new DefaultTrafficGraph();
    TrafficCity cityInfrastructureData = new TrafficCity(idGenerator.getNextId(),
      new CityInfrastructureData(idGenerator.getNextId()));
    OSMFileBasedCityDataService osmParser = new OSMFileBasedCityDataService(
      idGenerator,
      trafficGraph,
      graphConstructor,
      cityInfrastructureData);
    InputStream osmIN = Thread.currentThread().getContextClassLoader().
      getResourceAsStream(OSM_FILE_NAME);
    osmParser.parseStream(osmIN);
    List<TrafficNode> randomNodeList = new LinkedList<>(osmParser.createCity().
      getCityInfrastructureData().
      getNodes());
    Collections.shuffle(randomNodeList);
    NavigationNode givenNode = randomNodeList.get(
      (int) (Math.random() * randomNodeList.size()));
    NavigationNode returnedNode = osmParser.getNearestNode(givenNode.
      getX(),
      givenNode.getY());
    assertTrue(givenNode.getX() == returnedNode.
      getX()
      && givenNode.getY() == returnedNode.
      getY());

    boolean isDifferent = false;
    for (NavigationNode node : osmParser.createCity().
      getCityInfrastructureData().
      getNodes()) {
      if (!node.equals(osmParser.getNearestStreetNode(node.
        getX(),
        node.getY()))) {
        isDifferent = true;
        break;
      }
    }
    assertTrue(isDifferent);
  }

  /**
   * Test of parseFile method, of class OSMFileBasedCityDataService.
   */
  @Test
  public void testParseStream() throws Exception {
    InputStream osmIN = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("dbis_institute_berlin_reduced_few_nodes.osm");
    TrafficCity cityInfrastructureData = new TrafficCity(idGenerator.getNextId(),
      new CityInfrastructureData(idGenerator.getNextId()));
    TrafficGraph trafficGraph = new DefaultTrafficGraph();
    OSMFileBasedCityDataService instance
      = new OSMFileBasedCityDataService(idGenerator,
        trafficGraph,
        graphConstructor,
        cityInfrastructureData);
    instance.parseStream(osmIN);
    assertEquals(2,
      instance.createCity().getCityInfrastructureData().getNodes().size());
    assertEquals(instance.createCity().retrieveBoundary().getEnvelopeInternal().
      getMinX(),
      53,
      1);
    assertEquals(instance.createCity().retrieveBoundary().getEnvelopeInternal().
      getMaxX(),
      53,
      1);
    assertEquals(instance.createCity().retrieveBoundary().getEnvelopeInternal().
      getMinY(),
      13,
      1);
    assertEquals(instance.createCity().retrieveBoundary().getEnvelopeInternal().
      getMaxY(),
      13,
      1);
  }

  /**
   * Test of getNodesInBoundary method, of class OSMFileBasedCityDataService.
   */
  @Test
  @Ignore
  public void testGetNodesInBoundary() throws Exception {
    System.out.println("getNodesInBoundary");
    Envelope boundary = null;
    OSMFileBasedCityDataService instance = new OSMFileBasedCityDataService();
    List<NavigationNode> expResult = null;
    List<NavigationNode> result = instance.getNodesInBoundary(boundary);
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getNearestJunctionNode method, of class
   * OSMFileBasedCityDataService.
   */
  @Test
  @Ignore
  public void testGetNearestJunctionNode() throws Exception {
    System.out.println("getNearestJunctionNode");
    double latitude = 0.0;
    double longitude = 0.0;
    OSMFileBasedCityDataService instance = new OSMFileBasedCityDataService();
    NavigationNode expResult = null;
    NavigationNode result = instance.getNearestJunctionNode(latitude,
      longitude);
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of createCityInfrastructureData method, of class
   * OSMFileBasedCityDataService.
   */
  @Test
  @Ignore
  public void testCreateCityInfrastructureData() throws Exception {
    System.out.println("createCityInfrastructureData");
    OSMFileBasedCityDataService instance = new OSMFileBasedCityDataService();
    CityInfrastructureData expResult = null;
    TrafficCity result = instance.createCity();
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Tests if there are bus stops as nodes.
   */
  @Test
  public void testBusStops() throws IOException {
    TrafficGraph trafficGraph = new DefaultTrafficGraph();
    TrafficCity cityInfrastructureData = new TrafficCity(idGenerator.getNextId(),
      new CityInfrastructureData(idGenerator.getNextId()));
    OSMFileBasedCityDataService osmParser = new OSMFileBasedCityDataService(
      idGenerator,
      trafficGraph,
      graphConstructor,
      cityInfrastructureData);
    InputStream osmIN = Thread.currentThread().getContextClassLoader().
      getResourceAsStream(OSM_FILE_NAME);
    osmParser.parseStream(osmIN);
    boolean foundBusstop = false;
    outerLoop:
    for (NavigationNode node : osmParser.createCity().
      getCityInfrastructureData().getNodes()) {
      if (node instanceof BusStop) {
        foundBusstop = true;
        break outerLoop;
      }
    }

    assertTrue(foundBusstop);
  }

  /**
   * Gives the distance in meter between start and target.
   *
   * @param start BaseBoundary
   * @param targetBaseBoundaryo
   * @return distance
   */
  private double getDistanceInMeter(Coordinate start,
    Coordinate target) {

    if ((start.x == target.y)
      && (start.x == target.y)) {
      return 0.0;
    }

    double f = 1 / 298.257223563;
    double a = 6378.137;
    double F = ((start.x + target.x) / 2) * (Math.PI / 180);
    double G = ((start.x - target.x) / 2) * (Math.PI / 180);
    double l = ((start.y - target.y) / 2) * (Math.PI / 180);
    double S = Math.pow(Math.sin(G),
      2) * Math.pow(Math.cos(l),
        2) + Math.pow(Math.cos(F),
        2)
      * Math.pow(Math.sin(l),
        2);
    double C = Math.pow(Math.cos(G),
      2) * Math.pow(Math.cos(l),
        2) + Math.pow(Math.sin(F),
        2)
      * Math.pow(Math.sin(l),
        2);
    double w = Math.atan(Math.sqrt(S / C));
    double D = 2 * w * a;
    double R = Math.sqrt(S * C) / w;
    double H1 = (3 * R - 1) / (2 * C);
    double H2 = (3 * R + 1) / (2 * S);

    double distance = D
      * (1 + f * H1 * Math.pow(Math.sin(F),
        2) * Math.pow(Math.cos(G),
        2) - f * H2 * Math.pow(Math.cos(F),
        2)
      * Math.pow(Math.sin(G),
        2));

    return distance * 1000.0;
  }

}
