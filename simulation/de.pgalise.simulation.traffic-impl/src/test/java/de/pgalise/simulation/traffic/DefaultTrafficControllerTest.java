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
package de.pgalise.simulation.traffic;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.sensorFramework.FileOutputServer;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.Server;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.entity.BicycleData;
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.CarData;
import de.pgalise.simulation.traffic.entity.MotorcycleData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficTrip;
import de.pgalise.simulation.traffic.entity.TruckData;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateBussesEvent;
import de.pgalise.simulation.traffic.event.CreateRandomBusData;
import de.pgalise.simulation.traffic.event.CreateRandomCarData;
import de.pgalise.simulation.traffic.event.CreateRandomTruckData;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.CreateVehiclesEvent;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.DefaultGpsNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredNoInterferer;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandlerManager;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.service.FileBasedCityDataService;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;
import de.pgalise.testutils.TestUtils;
import de.pgalise.testutils.traffic.TrafficTestUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createNiceMock;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the DefaultTrafficController
 *
 * @author Mustafa
 * @version 1.0 (Feb 15, 2013)
 */
@LocalClient
@ManagedBean
public class DefaultTrafficControllerTest {

  @EJB
  private IdGenerator idGenerator;
  private final Output tcpIpOutput = EasyMock.createNiceMock(Output.class);

  public DefaultTrafficControllerTest() {
  }

  @Before
  public void setUp() throws NamingException, IOException {
    TestUtils.getContext().bind("inject",
      this);
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);

    cityFactory.parseStream(Thread.currentThread().getContextClassLoader().
      getResourceAsStream(OSM));

    WeatherController wc = createNiceMock(WeatherController.class);
    EnergyController ec = createNiceMock(EnergyController.class);

    EntityManager entityManager = EasyMock.createMock(EntityManager.class);
    SENSOR_REGISTRY = new HashSet<>();

    server = new FileOutputServer(new File(CSV_OUTPUT),
      null,
      6666);
    server.open();
    randomSeedService.init(SIMULATION_START.
      getTime());
  }

  /**
   * Test for initialization and start
   *
   * @throws IllegalStateException
   * @throws InitializationException
   * @throws javax.naming.NamingException
   */
  @Test
  public void initAndStartTest() throws IllegalStateException, InitializationException, NamingException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(1,
      1.0,
      1);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      trafficFuzzyData);
    TrafficStartParameter startParam = new TrafficStartParameter();

    TrafficControllerLocal<?> ctrl
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    ctrl.init(initParam);
    ctrl.start(startParam);
  }

  @Test
  /**
   * Test for stop and resume
   *
   * @throws IllegalStateException
   * @throws InitializationException
   */
  public void stopAndResumeTest() throws IllegalStateException, InitializationException, NamingException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(1,
      1.0,
      1);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      trafficFuzzyData);
    TrafficStartParameter startParam = new TrafficStartParameter();

    TrafficControllerLocal<?> ctrl
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");

    ctrl.init(
      initParam);
    ctrl.start(startParam);
    ctrl.stop();
    ctrl.start(null);
  }

  @Test
  public void resetTest() throws IllegalStateException, InitializationException, NamingException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(1,
      1.0,
      1);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      trafficFuzzyData);
    TrafficStartParameter startParam = new TrafficStartParameter();

    TrafficControllerLocal<?> ctrl = (TrafficControllerLocal) TestUtils.
      getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    ctrl.
      init(initParam);
    ctrl.start(startParam);
    ctrl.stop();
    ctrl.reset();
  }

  @Test
  public void updateTest() throws IllegalStateException, InitializationException, NamingException, FileNotFoundException, IOException {
    log.debug("###########");
    log.debug("UPDATE TEST");
    log.debug("###########");
    // File file = new File(DefaultTrafficServerTest.CSV_OUTPUT);

    TrafficControllerLocal<VehicleEvent> instance
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    instance.reset();
    instance.start(startParam);
    BaseCoordinate location;
    NavigationNode nodeForStaticSensor = null;

    if (VEHICLE_TYPE.equals("car") || VEHICLE_TYPE.equals("all")) {
      TrafficNode a = new TrafficNode(idGenerator.getNextId(),
        new BaseCoordinate(idGenerator.getNextId(), 30,
          30));
      TrafficNode b = new TrafficNode(idGenerator.getNextId(),
        new BaseCoordinate(idGenerator.getNextId(), 60,
          60));
      TrafficEdge ab = new TrafficEdge(idGenerator.getNextId(),
        a,
        b);
      Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
        output,
        null,
        new DefaultGpsNoInterferer());
      Vehicle<CarData> car = instance.getCarFactory().createRandomCar(
        new HashSet<>(Arrays.asList(ab)),
        output);
      car.setName("K.I.T.T");
      TrafficTrip trip = instance.createTrip(instance.getCityZone(),
        car.getData().getType());
      car.setPath(instance.getShortestPath(trip.getStartNode(),
        trip.getTargetNode()));

      nodeForStaticSensor = car.getNodePath().get(0);

      instance.getScheduler().scheduleItem(new ScheduleItem(car,
        SIMULATION_START.getTime() + 1000,
        instance.getUpdateIntervall()));
    }
    if (VEHICLE_TYPE.equals("truck") || VEHICLE_TYPE.equals("all")) {
      Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
        output,
        null,
        new DefaultGpsNoInterferer());
      Vehicle<TruckData> truck = instance.getTruckFactory().createRandomTruck(
        output);
      truck.setName("Coca Cola Truck");
      TrafficTrip tripTruck = instance.createTrip(instance.getCityZone(),
        truck.getData().getType());
      truck.setPath(instance.getShortestPath(tripTruck.getStartNode(),
        tripTruck.getTargetNode()));

      instance.getScheduler().scheduleItem(new ScheduleItem(truck,
        SIMULATION_START.getTime() + 1000,
        instance.getUpdateIntervall()));
    }
    if (VEHICLE_TYPE.equals("bike") || VEHICLE_TYPE.equals("all")) {
      Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
        output,
        null,
        new DefaultGpsNoInterferer());
      Vehicle<BicycleData> bike = instance.getBikeFactory().
        createRandomBicycle(output);
      bike.setName("tlottmann's Fahrrad");
      TrafficTrip tripBike = instance.createTrip(instance.getCityZone(),
        bike.getData().getType());
      bike.setPath(instance.getShortestPath(tripBike.getStartNode(),
        tripBike.getTargetNode()));

      instance.getScheduler().scheduleItem(new ScheduleItem(bike,
        SIMULATION_START.getTime() + 1000,
        instance.getUpdateIntervall()));
    }

    if (VEHICLE_TYPE.equals("motorcycle") || VEHICLE_TYPE.equals("all")) {
      Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
        output,
        null,
        new DefaultGpsNoInterferer());
      Vehicle<MotorcycleData> motorcycle = instance.getMotorcycleFactory().
        createRandomMotorcycle();
      motorcycle.setName("Jens' Kawasaki");
      TrafficTrip tripMotorcycle = instance.createTrip(instance.getCityZone(),
        motorcycle.getData().getType());
      motorcycle.setPath(instance.getShortestPath(tripMotorcycle.getStartNode(),
        tripMotorcycle.getTargetNode()));

      instance.getScheduler().scheduleItem(
        new ScheduleItem(motorcycle,
          SIMULATION_START.getTime() + 1000,
          instance.getUpdateIntervall()));
    }

    EventList eventList = new EventList(idGenerator.getNextId(),
      null,
      SIMULATION_START.getTime());

    if (VEHICLE_TYPE.equals("all")) {
      assertEquals(4,
        (instance.getScheduler().getScheduledItems().size()));
    } else {
      assertEquals(1,
        (instance.getScheduler().getScheduledItems().size()));
    }
    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }

    log.debug(
      "##############################################################################");

    location = ((BaseCoordinate) nodeForStaticSensor);
    if (location != null) {
      TrafficSensor sensor = new GpsSensor(idGenerator.getNextId(),
        output,
        null,
        new DefaultGpsNoInterferer());
      instance.createSensor(sensor);
    }

    eventList = new EventList(idGenerator.getNextId(),
      null,
      SIMULATION_START.getTime() + 1000);
    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }

    log.debug(
      "##############################################################################");

    eventList = new EventList(idGenerator.getNextId(),
      null,
      SIMULATION_START.getTime() + 2000);
    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }

    log.debug(
      "##############################################################################");

    eventList = new EventList(idGenerator.getNextId(),
      null,
      SIMULATION_START.getTime() + 3000);
    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }

    // es sollte eine csv datei beschrieben worden sein
    // auto ist 2x gefahren, also müssten 2 zeilen existieren
    CSVParse parser = new CSVParser(new FileReader(
      DefaultTrafficControllerTest.CSV_OUTPUT));

    int lines = 0;
    for (String[] text = parser.getLine(); text != null; text = parser.getLine()) {
      lines++;
      for (String t : text) {
        log.debug(t);
      }
    }
    if (VEHICLE_TYPE.equals("all")) {
      assertEquals(15,
        lines);
    } // (4 fahrzeuge fahren 3 mal -> 12 * gps) + 3 inductionloop
    else {
      assertEquals(2,
        lines);
    }
  }

  @Test
  /**
   * Test for creating, deleting and asking for status of sensors
   *
   * @throws IllegalStateException
   * @throws SensorException
   * @throws InitializationException
   */
  public void sensorTests() throws IllegalStateException, SensorException, InitializationException, NamingException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(1,
      1.0,
      1);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      trafficFuzzyData);
    TrafficStartParameter startParam = createNiceMock(
      TrafficStartParameter.class);

    // create sensors
    TrafficNode someNode = new TrafficNode(idGenerator.getNextId(),
      new BaseCoordinate(idGenerator.getNextId(), 1,
        2));
    TrafficSensor<?> sensor = new InductionLoopSensor(idGenerator.getNextId(),
      tcpIpOutput,
      someNode,
      new InductionLoopNoInterferer());
    sensor.setActivated(true);
    TrafficSensor<?> sensor2 = new InductionLoopSensor(idGenerator.getNextId(),
      tcpIpOutput,
      someNode,
      new InductionLoopNoInterferer());
    sensor2.setActivated(false);

    TrafficControllerLocal<?> ctrl
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    ctrl.init(initParam);
    ctrl.start(startParam);
    ctrl.createSensor(sensor);
    assertTrue(ctrl.isActivated(sensor));
    assertFalse(ctrl.isActivated(sensor2));
    ctrl.deleteSensor(sensor);
  }
  /**
   * Log
   */
  private static final Logger log = LoggerFactory.getLogger(
    DefaultTrafficControllerTest.class);

  /**
   * Bus stops file
   */
  private static final String BUS_STOPS = "stops.txt";

  /**
   * OSM file
   */
  private static final String OSM = "oldenburg_pg.osm";

  /**
   * Sensor output file
   */
  private static final String CSV_OUTPUT;

  static {
    try {
      Path tmpDir = Files.createTempDirectory("pgalise",
        PosixFilePermissions.asFileAttribute(new HashSet<>(
            Arrays.asList(PosixFilePermission.OWNER_READ,
              PosixFilePermission.OWNER_WRITE,
              PosixFilePermission.OWNER_EXECUTE))));
      CSV_OUTPUT = File.createTempFile("pgalise",
        null,
        tmpDir.toFile()).getAbsolutePath();
    } catch (IOException ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  /**
   * Set with all used sensor ids.
   */
  private static final Set<Integer> USED_SENSOR_IDS = new HashSet<>();

  /**
   * Simulation start
   */
  private static final Date SIMULATION_START = new GregorianCalendar(2014,
    1,
    18).getTime();

  /**
   * Simulation end
   */
  private static final Date SIMULATION_END = new GregorianCalendar(2014,
    1,
    19).getTime();

  /**
   * Option to set the vehicle type: car, truck, bike, bus, motorcycle, all
   */
  private static final String VEHICLE_TYPE = "all";

  /**
   * Output server
   */
  private static Server server;

  /**
   * Sensor factory
   */
  @EJB
  private TrafficSensorFactory sensorFactory;

  /**
   * Implementation of SensorRegistry.
   */
  private static Set<TrafficSensor<?>> SENSOR_REGISTRY = new HashSet<>();
  @EJB
  private FileBasedCityDataService cityFactory;
  @EJB
  private WeatherControllerLocal weatherController;
  @EJB
  private EnergyControllerLocal energyController;
  @EJB
  private RandomSeedService randomSeedService;
  private Output output = EasyMock.createNiceMock(Output.class);
  private TrafficStartParameter startParam = new TrafficStartParameter();
  private TrafficInitParameter initParameter = new TrafficInitParameter();

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    log.debug("CLEAR ALL");
    // server.waitToOpen();
    server.close();
    log.debug("Tests finished");

    // delete test file
    File file = new File(CSV_OUTPUT);
    if (file.exists()) {
      file.delete();
    }
  }

  @After
  public void tearDown() {
    File file = new File(DefaultTrafficControllerTest.CSV_OUTPUT);
    if (file.exists()) {
      file.delete();
    }
    SENSOR_REGISTRY.clear();
  }

  /*
   * @Test
   */
  public void moveVehicleTest() throws IllegalStateException, InitializationException, IllegalAccessException, NamingException {
    log.debug("#################");
    log.debug("MOVE VEHICLE TEST");
    log.debug("#################");

    Context localContext = new InitialContext();
    TrafficControllerLocal<VehicleEvent> ctrl
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");

    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);

    TrafficNode a = new TrafficNode(idGenerator.getNextId(),
      new BaseCoordinate(idGenerator.getNextId(), 30,
        30));
    TrafficNode b = new TrafficNode(idGenerator.getNextId(),
      new BaseCoordinate(idGenerator.getNextId(), 60,
        60));
    TrafficEdge ab = new TrafficEdge(idGenerator.getNextId(),
      a,
      b);
    TrafficTrip trip0 = ctrl.createTrip(city.getGeoInfo().retrieveBoundary(),
      VehicleTypeEnum.CAR);
    TrafficTrip trip1 = ctrl.createTrip(city.getGeoInfo().retrieveBoundary(),
      VehicleTypeEnum.CAR);

    List<TrafficEdge> path = ctrl.getShortestPath(a,
      b);

    ctrl.init(initParameter);
    ctrl.start(startParam);

    Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
      output,
      null,
      null);
    Vehicle<CarData> car = ctrl.getCarFactory().createRandomCar(
      new HashSet<>(Arrays.asList(ab)),
      output);
    car.setName("Car A");
    car.setPath(path);
    ctrl.getScheduler().scheduleItem(new ScheduleItem(car,
      SIMULATION_START.getTime(),
      ctrl.getUpdateIntervall()));

    Sensor<?, ?> sensor2 = new GpsSensor(idGenerator.getNextId(),
      output,
      null,
      new DefaultGpsNoInterferer());
    Vehicle<CarData> car3 = ctrl.getCarFactory().createRandomCar(
      new HashSet<>(Arrays.asList(ab)),
      output);
    car3.setName("Car B");
    car3.setPath(path);
    ctrl.getScheduler().scheduleItem(new ScheduleItem(car3,
      SIMULATION_START.getTime() + 1000,
      ctrl.getUpdateIntervall()));

    // 2.auto
    int i = -1000;
    List<Vehicle<? extends VehicleData>> addedVehicles = new LinkedList<>();
    int droveOverBoundaries = 0;
    while ((i / 1000) < 6131) {
      EventList<VehicleEvent> eventList = new EventList<>(idGenerator.
        getNextId(),
        null,
        SIMULATION_START.getTime() + i);
      i += 1000;
      ctrl.update(eventList);
      ctrl.processMovedVehicles();

      if (ctrl.getScheduler().getExpiredItems(SIMULATION_START.getTime() + i).
        size() == 2) {
        droveOverBoundaries = 1;
      }

      if (ctrl.getScheduler().getExpiredItems(SIMULATION_START.getTime() + i).
        size() == 1 && droveOverBoundaries == 1) {
        log.info((i / 1000) + " updates needed to drive over the boundaries");// 5137
        Vehicle<?> v = createVehicle(ctrl,
          i / 1000);
        v.setVehicleState(VehicleStateEnum.STOPPED);
        addedVehicles.add(v);
        droveOverBoundaries = 2;
      }

      if (droveOverBoundaries == 2 && ctrl.getScheduler().getExpiredItems(
        SIMULATION_START.getTime() + i).
        isEmpty()) {
        droveOverBoundaries = 3;
      } else if (droveOverBoundaries == 3) {
        for (Vehicle<?> v : addedVehicles) {
          v.setVehicleState(VehicleStateEnum.DRIVING);
        }
        droveOverBoundaries++;
      }
      if (ctrl.getScheduler().getExpiredItems(SIMULATION_START.getTime() + i).
        isEmpty()
        && (ctrl.getScheduler().getExpiredItems(
          SIMULATION_START.getTime() + i).
        isEmpty())) {
        log.info(((i + 1) / 1000) + " iterations needed to break the loop ");// 6131
        break;
      }
    }
    if ((i / 1000) > 6131) {
      assertTrue(false);
    }
  }

  private Vehicle<?> createVehicle(TrafficControllerLocal server2,
    int i)
    throws IllegalAccessException {
    Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
      output,
      null,
      new DefaultGpsNoInterferer());
    TrafficNode a = new TrafficNode(idGenerator.getNextId(),
      new BaseCoordinate(idGenerator.getNextId(), 30,
        30));
    TrafficNode b = new TrafficNode(idGenerator.getNextId(),
      new BaseCoordinate(idGenerator.getNextId(), 60,
        60));
    TrafficEdge ab = new TrafficEdge(idGenerator.getNextId(),
      a,
      b);
    Vehicle<CarData> car2 = server2.getCarFactory().createRandomCar(
      new HashSet<TrafficEdge>(Arrays.asList(ab)),
      output);
    car2.setName("Car " + i);
    TrafficNode startNode = new TrafficNode(idGenerator.getNextId(),
      new Coordinate(1,
        2)), endNode = new TrafficNode(idGenerator.getNextId(),
        new Coordinate(3,
          6));
    car2.setPath(server2.getShortestPath(startNode,
      endNode));

    server2.getScheduler().scheduleItem(
      new ScheduleItem(car2,
        SIMULATION_START.getTime() + (5135 * 1000),
        server2.getUpdateIntervall()));

    return car2;
  }

  @Test
  public void createRandomCarTest() throws IllegalStateException, InitializationException, NamingException {
    log.debug("##########################");
    log.debug("HANDLE RANDOMCAREVENT TEST");
    log.debug("##########################");
    int carCount = 10;

    EventList eventList = new EventList(idGenerator.getNextId(),
      Arrays.asList(createRandomVehicleEvent(
          carCount,
          VehicleTypeEnum.CAR,
          VehicleModelEnum.CAR_BMW_1)),
      SIMULATION_START.getTime());

    initTrafficServer(null);

    TrafficStartParameter startParam = new TrafficStartParameter();
    TrafficControllerLocal<VehicleEvent> instance
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    instance.reset();
    instance.start(startParam);

    instance.update(eventList);

    assertEquals(
      carCount, // 2*size weil ein auto 2 x eingetragen wird
      // (2 abfahrszeiten)
      (instance.getScheduler().getScheduledItems().size() + instance.
      getScheduler()
      .getExpiredItems(SIMULATION_START.getTime()).size()));
  }

  @Test
  public void createRandomTruckTest() throws IllegalStateException, InitializationException, NamingException {
    log.debug("##########################");
    log.debug("HANDLE RANDOMTRUCKEVENT TEST");
    log.debug("##########################");
    int truckCount = 5;

    EventList eventList = new EventList(idGenerator.getNextId(),
      Arrays.asList(createRandomVehicleEvent(
          truckCount,
          VehicleTypeEnum.TRUCK,
          VehicleModelEnum.TRUCK_COCA_COLA)),
      SIMULATION_START.getTime());

    TrafficStartParameter startParam = new TrafficStartParameter();
    TrafficControllerLocal<VehicleEvent> instance
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    instance.reset();
    instance.start(startParam);

    instance.update(eventList);

    assertEquals(
      truckCount, // 2*size weil ein truck 2 x eingetragen
      // wird (2 abfahrszeiten)
      (instance.getScheduler().getScheduledItems().size() + instance.
      getScheduler()
      .getExpiredItems(SIMULATION_START.getTime()).size()));
  }

  @Test
  public void createRandomBusTest() throws IllegalStateException, InitializationException, NamingException {
    log.debug("##########################");
    log.debug("HANDLE BUSEVENT TEST");
    log.debug("##########################");

    List<AbstractTrafficEvent> trafficEventList = new ArrayList<>();
    List<BusRoute> busRoutes = new ArrayList<>();
    BusRoute b301a = new BusRoute(1L,
      "301",
      "Eversten");
    busRoutes.add(b301a);
    List<CreateRandomBusData> busDataList = new ArrayList<>();
    int tnbt
      = 1;
//			(new DefaultBusService()).getTotalNumberOfBusTrips(busRoutes,
//			SIMULATION_START);
    for (int i = 0; i < tnbt; i++) {
      GpsSensor sensor = new GpsSensor(idGenerator.getNextId(),
        output,
        null,
        1,
        new DefaultGpsNoInterferer());
      InfraredSensor sensor2 = new InfraredSensor(idGenerator.getNextId(),
        output,
        null,
        null,
        new InfraredNoInterferer());
      busDataList.add(new CreateRandomBusData(sensor2,
        sensor,
        new VehicleInformation(true,
          VehicleTypeEnum.BUS,
          VehicleModelEnum.BUS_CITARO,
          null,
          UUID.randomUUID().toString())));
    }
    TrafficControllerLocal<?> trafficServerLocal = EasyMock.createNiceMock(
      TrafficControllerLocal.class);
    trafficEventList.add(new CreateBussesEvent(trafficServerLocal,
      SIMULATION_START.getTime(),
      0,
      busDataList,
      busRoutes));

    EventList eventList = new EventList(idGenerator.getNextId(),
      trafficEventList,
      SIMULATION_START.getTime());

    TrafficControllerLocal<VehicleEvent> instance
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    instance.reset();
    instance.start(startParam);

    instance.update(eventList);

    assertEquals(
      tnbt,
      (instance.getScheduler().getScheduledItems().size() + instance.
      getScheduler()
      .getExpiredItems(SIMULATION_START.getTime()).size()));

    eventList = new EventList(idGenerator.getNextId(),
      null,
      1392697560000L);

    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }

    log.debug(
      "##############################################################################");

    eventList = new EventList(idGenerator.getNextId(),
      null,
      1392697560000L + 1000);
    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }

    log.debug(
      "##############################################################################");

    eventList = new EventList(idGenerator.getNextId(),
      null,
      1392697560000L + 2000);
    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }

    log.debug(
      "##############################################################################");

    eventList = new EventList(idGenerator.getNextId(),
      null,
      1392697560000L + 3000);
    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }
  }

  @Test
  public void createVehiclesTest() throws Exception {
    log.debug("###########");
    log.debug("CREATE VEHICLE EVENT TEST");
    log.debug("###########");

    TrafficControllerLocal<VehicleEvent> instance
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    instance.reset();
    instance.start(startParam);

    TrafficNode a = new TrafficNode(idGenerator.getNextId(),
      new Coordinate(30,
        30));
    TrafficNode b = new TrafficNode(idGenerator.getNextId(),
      new Coordinate(60,
        60));
    TrafficEdge ab = new TrafficEdge(idGenerator.getNextId(),
      a,
      b);
    GpsSensor gpsSensor = new GpsSensor(idGenerator.getNextId(),
      output,
      null,
      new DefaultGpsNoInterferer());
    Vehicle<CarData> car = instance.getCarFactory().createRandomCar(
      new HashSet<>(Arrays.asList(ab)),
      output);
    car.setName("K.I.T.T");
    TrafficTrip trip = instance.createTrip(instance.getCityZone(),
      car.getData().getType());
    car.setPath(instance.getShortestPath(trip.getStartNode(),
      trip.getTargetNode()));

    instance.getScheduler().scheduleItem(new ScheduleItem(car,
      SIMULATION_START.getTime() + 1000,
      instance.getUpdateIntervall()));

    assertEquals(1,
      (instance.getScheduler().getScheduledItems().size()));

    EventList eventList = new EventList(idGenerator.getNextId(),
      null,
      SIMULATION_START.getTime());

    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }

    for (int i = 1000; i <= 10000; i += 1000) {
      log.debug(
        "* * * * * * * * * * Update: " + i / 1000 + " * * * * * * * * * *");

      if (i == 3000) {
        List<TrafficEvent> list = new ArrayList<>();
        List<CreateRandomVehicleData> vehicleDataList = new ArrayList<>();

        List<Sensor<?, ?>> sensorLists = new ArrayList<>();
        sensorLists.add(gpsSensor);
        TrafficNode startNode = new TrafficNode(idGenerator.getNextId(),
          new Coordinate(1,
            2)), endNode = new TrafficNode(idGenerator.getNextId(),
            new Coordinate(2,
              3));
        trip = new TrafficTrip(startNode,
          endNode,
          SIMULATION_START.getTime() + 4000);
        vehicleDataList.add(new CreateRandomCarData(gpsSensor,
          new VehicleInformation(
            true,
            VehicleTypeEnum.CAR,
            VehicleModelEnum.CAR_BMW_1,
            trip,
            "K.A.R.R")));

        list.add(new CreateVehiclesEvent(instance,
          SIMULATION_START.getTime(),
          SIMULATION_END.getTime(),
          vehicleDataList));
        eventList = new EventList(idGenerator.getNextId(),
          list,
          SIMULATION_START.getTime() + i);
      } else {
        eventList = new EventList(idGenerator.getNextId(),
          null,
          SIMULATION_START.getTime() + i);
      }

      if (i == 4000) {
        assertEquals(1,
          (instance.getScheduler().getScheduledItems().size()));
      }

      instance.update(eventList);
      for (Sensor sensor : SENSOR_REGISTRY) {
        sensor.update(eventList);
      }

      if (i == 4000) {
        assertEquals(2,
          instance.getScheduler().getExpiredItems(
            SIMULATION_START.getTime() + 4000).size());
      }
    }
  }

  @Test
  public void createAttractionTest() throws Exception {
    log.debug("###########");
    log.debug("CREATE ATTRACTION EVENT TEST");
    log.debug("###########");

    TrafficNode a = new TrafficNode(idGenerator.getNextId(),
      new Coordinate(30,
        30));
    TrafficNode b = new TrafficNode(idGenerator.getNextId(),
      new Coordinate(60,
        60));
    TrafficEdge ab = new TrafficEdge(idGenerator.getNextId(),
      a,
      b);
    GpsSensor gpsSensor = new GpsSensor(idGenerator.getNextId(),
      output,
      null,
      new DefaultGpsNoInterferer());

    TrafficControllerLocal<VehicleEvent> instance
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");

    Vehicle<CarData> car = instance.getCarFactory().createRandomCar(
      new HashSet<>(Arrays.asList(ab)),
      output);
    car.setName("K.I.T.T");
    TrafficTrip trip = instance.createTrip(instance.getCityZone(),
      car.getData().getType());
    car.setPath(instance.getShortestPath(trip.getStartNode(),
      trip.getTargetNode()));

    instance.getScheduler().scheduleItem(new ScheduleItem(car,
      SIMULATION_START.getTime() + 1000,
      instance.getUpdateIntervall()));

    assertEquals(1,
      (instance.getScheduler().getScheduledItems().size()));

    EventList eventList = new EventList(idGenerator.getNextId(),
      null,
      SIMULATION_START.getTime());

    instance.update(eventList);
    for (Sensor sensor : SENSOR_REGISTRY) {
      sensor.update(eventList);
    }

    for (int i = 1000; i <= 100000; i += 1000) {
      log.debug(
        "* * * * * * * * * * Update: " + i / 1000 + " * * * * * * * * * *");

      if (i == 3000) {
        List<AbstractTrafficEvent> list = new ArrayList<>();
        List<CreateRandomVehicleData> vehicleDataList = new ArrayList<>();

        vehicleDataList.add(new CreateRandomCarData(gpsSensor,
          new VehicleInformation(
            true,
            VehicleTypeEnum.CAR,
            VehicleModelEnum.CAR_BMW_1,
            null,
            "K.A.R.R")));

        TrafficNode node = new TrafficNode(idGenerator.getNextId(),
          new Coordinate(4,
            4));
        list.add(new AttractionTrafficEvent(instance,
          SIMULATION_START.getTime(),
          SIMULATION_END.getTime(),
          SIMULATION_START.getTime() + 4000,
          SIMULATION_START.getTime() + 8000,
          node,
          vehicleDataList
        ));
        eventList = new EventList(idGenerator.getNextId(),
          list,
          SIMULATION_START.getTime() + i);
      } else {
        eventList = new EventList(idGenerator.getNextId(),
          null,
          SIMULATION_START.getTime() + i);
      }

      // KARR hin
      if (i == 4000) {
        assertEquals(1,
          (instance.getScheduler().getScheduledItems().size()));
      }

      instance.update(eventList);
      for (Sensor sensor : SENSOR_REGISTRY) {
        sensor.update(eventList);
      }

      // KARR hin (KARR zurück fährt noch nicht) + KITT
      if (i == 4000) {
        assertEquals(2,
          instance.getScheduler().getExpiredItems(
            SIMULATION_START.getTime() + 4000).size());
      }
      // KARR hin, KARR zurück + KITT
      // if (i == 8000)
      // assertEquals(3, server.getScheduler().getExpiredItems(SIMULATION_START + 4000).size());
    }
  }

  /**
   * Create the {@link TrafficServer}
   *
   * @param serverList List with all traffic servers
   * @return TrafficServerLocal
   * @throws IllegalStateException
   * @throws InitializationException
   */
  private void initTrafficServer(
    List<TrafficControllerLocal<VehicleEvent>> serverList) throws IllegalStateException,
    InitializationException,
    NamingException {
    // durch einen outputstream am besten ersetzen...
    // TrafficSensorFactory sensorFactory = new CSVTrafficSensorFactory(CSV_OUTPUT, sd.getRandomSeedService(),
    // sd.getController(WeatherController.class), mapper);

    EntityManager entityManager = EasyMock.createMock(EntityManager.class);
    SENSOR_REGISTRY = new HashSet<>();

    Coordinate referencePoint = new Coordinate(52.516667,
      13.4);
    TrafficEventHandlerManager<TrafficEventHandler<VehicleEvent>, VehicleEvent> eventHandlerManager = EasyMock.
      createNiceMock(
        TrafficEventHandlerManager.class);

    TrafficControllerLocal<VehicleEvent> instance = (TrafficControllerLocal) TestUtils.
      getContext().
      lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficServer!de.pgalise.simulation.traffic.server.TrafficServerLocal");

    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      null);
    initParam.setCity(city);
    initParam.setStartTimestamp(SIMULATION_START);
    initParam.setEndTimestamp(SIMULATION_END);
    initParam.setInterval(1000);
    initParam.setTrafficFuzzyData(new TrafficFuzzyData(1,
      0.9,
      1));

    instance.init(initParam);
  }

  /**
   * Produce simulation event lists. E.g. for random cars.
   *
   * @param cityInfrastructureData CityInfrastructureDataService
   * @param count Number of vehicles
   * @return CreateRandomVehiclesEvent
   */
  private CreateRandomVehiclesEvent<?> createRandomVehicleEvent(
    int count,
    VehicleTypeEnum vehicleType,
    VehicleModelEnum vehicleModel) throws NamingException {
    /* create random cars */
    List<CreateRandomVehicleData> vehicleDataList = new ArrayList<>();
    List<Sensor<?, ?>> sensorLists = new ArrayList<>();

    GpsSensor sensor = new GpsSensor(idGenerator.getNextId(),
      output,
      null,
      new DefaultGpsNoInterferer());
    sensorLists.add(sensor);

    for (int i = 0; i < count; i++) {
      if (vehicleType == VehicleTypeEnum.TRUCK) {
        vehicleDataList.add(new CreateRandomTruckData(sensor,
          new VehicleInformation(true,
            vehicleType,
            vehicleModel,
            null,
            null)));
      } else if (vehicleType == VehicleTypeEnum.CAR) {
        vehicleDataList.add(new CreateRandomCarData(sensor,
          new VehicleInformation(true,
            vehicleType,
            vehicleModel,
            null,
            null)));
      }
    }
    TrafficControllerLocal<VehicleEvent> instance = (TrafficControllerLocal) TestUtils.
      getContext().
      lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficServer!de.pgalise.simulation.traffic.server.TrafficServerLocal");
    return new CreateRandomVehiclesEvent(instance,
      SIMULATION_START.getTime(),
      SIMULATION_END.getTime(),
      vehicleDataList);
  }
}
