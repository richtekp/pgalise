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
//package de.pgalise.simulation.traffic.eventhandler;
//
//import static org.easymock.EasyMock.createMock;
//import static org.easymock.EasyMock.createNiceMock;
//import static org.easymock.EasyMock.expect;
//import static org.easymock.EasyMock.replay;
//import static org.junit.Assert.assertEquals;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.GregorianCalendar;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//import java.util.UUID;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import de.pgalise.simulation.energy.EnergyController;
//import de.pgalise.simulation.sensorFramework.FileOutputServer;
//import de.pgalise.simulation.sensorFramework.SensorFactory;
//import de.pgalise.simulation.sensorFramework.SensorRegistry;
//import de.pgalise.simulation.sensorFramework.Server;
//import de.pgalise.simulation.sensorFramework.internal.DefaultSensorRegistry;
//import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
//import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
//import de.pgalise.simulation.service.RandomSeedService;
//import de.pgalise.simulation.service.ServiceDictionary;
//import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
//import de.pgalise.simulation.shared.city.CityInfrastructureData;
//import de.pgalise.simulation.service.InitParameter;
//import de.pgalise.simulation.shared.controller.StartParameter;
//import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
//import de.pgalise.simulation.shared.event.EventList;
//import de.pgalise.simulation.traffic.event.CreateBussesEvent;
//import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
//import de.pgalise.simulation.traffic.event.RoadBarrierTrafficEvent;
//import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
//import de.pgalise.simulation.shared.exception.InitializationException;
//import com.vividsolutions.jts.geom.Coordinate;
//import de.pgalise.simulation.sensorFramework.SensorHelper;
//import de.pgalise.simulation.shared.sensor.SensorInterfererType;
//import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
//import de.pgalise.simulation.traffic.BusRoute;
//import de.pgalise.simulation.traffic.TrafficTrip;
//import de.pgalise.simulation.traffic.VehicleInformation;
//import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
//import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
//import de.pgalise.simulation.traffic.BusRoute;
//import de.pgalise.simulation.traffic.DefaultOSMCityInfrastructureDataService;
//import de.pgalise.simulation.traffic.TrafficServerTest;
//import de.pgalise.simulation.traffic.internal.server.DefaultTrafficServer;
//import de.pgalise.simulation.traffic.model.vehicle.BusData;
//import de.pgalise.simulation.traffic.model.vehicle.CarData;
//import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
//import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
//import de.pgalise.simulation.traffic.server.TrafficServerLocal;
//import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
//import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandlerManager;
//import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
//import de.pgalise.simulation.traffic.internal.server.scheduler.DefaultScheduleItem;
//import de.pgalise.simulation.weather.service.WeatherController;
//import de.pgalise.staticsensor.internal.DefaultSensorFactory;
//import de.pgalise.util.GTFS.service.DefaultBusService;
//import javax.persistence.EntityManager;
//import org.easymock.EasyMock;
//
///**
// * Tests the {@link RoadBarrierTrafficEvent}.
// * 
// * @author Andreas Rehfeldt
// * @version 1.0 (Feb 20, 2013)
// */
//public class RoadBarrierTest {
//
//	/**
//	 * Log
//	 */
//	private static final Logger log = LoggerFactory.getLogger(RoadBarrierTest.class);
//
//	/**
//	 * CityInfrastructureData
//	 */
//	private static CityInfrastructureData city;
//
//	/**
//	 * Sensor output file
//	 */
//	private static String CSV_OUTPUT = System.getProperty("user.dir") + "/stream_output.csv";
//
//	/**
//	 * Set with all used sensor ids.
//	 */
//	private static final Set<Integer> USED_SENSOR_IDS = new HashSet<>();
//
//	/**
//	 * Simulation start
//	 */
//	private static final long SIMULATION_START = new GregorianCalendar(2014, 1, 18).getTimeInMillis();
//
//	/**
//	 * Simulation end
//	 */
//	private static final long SIMULATION_END = new GregorianCalendar(2014, 1, 19).getTimeInMillis();
//
//	/**
//	 * Service dictionary
//	 */
//	private static ServiceDictionary sd;
//
//	/**
//	 * Implementation of SensorRegistry.
//	 */
//	private static SensorRegistry SENSOR_REGISTRY;
//
//	/**
//	 * Sensor factory
//	 */
//	private static SensorFactory sensorFactory;
//
//	/**
//	 * OSM file
//	 */
//	private static String OSM = System.getProperty("user.dir") + "/src/test/resources/oldenburg_pg.osm";
//
//	/**
//	 * Bus stops file
//	 */
//	private static String BUS_STOPS = System.getProperty("user.dir") + "/src/test/resources/stops.txt";
//
//	/**
//	 * Output server
//	 */
//	private static Server server;
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		DefaultOSMCityInfrastructureDataService cityFactory = new DefaultOSMCityInfrastructureDataService();
//		city = cityFactory.createCityInfrastructureData(new File(OSM), new File(BUS_STOPS), null);
//
//		RandomSeedService rss = new DefaultRandomSeedService();
//
//		WeatherController wc = createNiceMock(WeatherController.class);
//		EnergyController ec = createNiceMock(EnergyController.class);
//
//		sd = createMock(ServiceDictionary.class);
//		expect(sd.getRandomSeedService()).andStubReturn(rss);
//		expect(sd.getController(WeatherController.class)).andStubReturn(wc);
//		expect(sd.getController(EnergyController.class)).andStubReturn(ec);
//
//		replay(sd, wc);
//
//		EntityManager entityManager = EasyMock.createMock(EntityManager.class);
//		SENSOR_REGISTRY = new DefaultSensorRegistry(entityManager);
//
//		server = new FileOutputServer(new File(CSV_OUTPUT), null, 6666);
//		server.open();
//		sensorFactory = new DefaultSensorFactory(sd.getRandomSeedService(), sd.getController(WeatherController.class),
//				sd.getController(EnergyController.class), new TcpIpOutput("127.0.0.1", 6666,
//						new TcpIpKeepOpenStrategy()));
//		RoadBarrierTest.sd.getRandomSeedService().init(SIMULATION_START);
//	}
//
//	@After
//	public void tearDown() throws Throwable {
//		File file = new File(CSV_OUTPUT);
//		if (file.exists()) {
//			file.delete();
//		}
//	}
//
//	/**
//	 * Returns a new unique sensor id.
//	 * 
//	 * @param randomSeedService
//	 * @return unique ID
//	 */
//	private static int getUniqueSensorID() {
//		Random random = new Random(sd.getRandomSeedService().getSeed(TrafficServerTest.class.getName()));
//		int sensorID = random.nextInt();
//		while (sensorID < 1 || USED_SENSOR_IDS.contains(sensorID)) {
//			sensorID = random.nextInt();
//		}
//		USED_SENSOR_IDS.add(sensorID);
//		return sensorID;
//	}
//
//	// @Test
//	public void vehicleTest() throws Exception {
//		log.debug("###########");
//		log.debug("TEST: Set road barrier with new path for vehicle");
//		log.debug("###########");
//
//		TrafficServerLocal server0 = createTrafficServer(null);
//
//		StartParameter startParam = new StartParameter();
//		server0.start(startParam);
//
//		// Create car
//		Vehicle<CarData> car = server0.getCarFactory().createRandomCar(
//				
//				new SensorHelper(getUniqueSensorID(), null, SensorTypeEnum.GPS_CAR, new ArrayList<SensorInterfererType>(),
//						""));
//		car.setName("K.I.T.T");
//
//		Path path;
//		do {
//			TrafficTrip trip = server0.createTrip(server0.getCityZone(), car.getData().getType());
//			path = server0.getShortestPath(server0.getGraph().getNode(trip.getStartNode()),
//					server0.getGraph().getNode(trip.getTargetNode()));
//		} while (path.getNodeCount() < 5);
//
//		// Set path
//		log.debug(path.toString());
//		car.setPath(path);
//
//		// Blocked node
//		Node closedNode = car.getPath().getNodePath().get(2);
//
//		log.debug("#### STEP 0 ##########################################################################");
//
//		server0.getScheduler().scheduleItem(new DefaultScheduleItem(car, SIMULATION_START, 1000));
//
//		long currentTime = SIMULATION_START;
//		EventList eventList = new EventList(null, currentTime, UUID.randomUUID());
//
//		/*
//		 * Test: One car should be scheduled
//		 */
//		assertEquals(1, (server0.getScheduler().getScheduledItems().size()));
//
//		/*
//		 * UPDATE
//		 */
//		server0.update(eventList);
//		SENSOR_REGISTRY.update(eventList);
//
//		/*
//		 * Test: car path
//		 */
//		Assert.assertTrue(car.getPath().equals(path));
//
//		/*
//		 * Test: RoadBarrier are installed
//		 */
//		assertEquals(0, (server0.getBlockedRoads(currentTime).size()));
//
//		log.debug("#### STEP +1000 ##########################################################################");
//
//		currentTime += 1000;
//		long numberOfEdgesInGraph = server0.getGraph().getEdgeCount();
//
//		// Create RoadBarrier
//		RoadBarrierTrafficEvent event = new RoadBarrierTrafficEvent(server0, currentTime, currentTime + 1000, currentTime, currentTime+1000, 
//				new Coordinate(), closedNode.getId());
//		eventList = new EventList(Arrays.asList(event), currentTime, UUID.randomUUID());
//		server0.update(eventList);
//		SENSOR_REGISTRY.update(eventList);
//
//		/*
//		 * Test: new car path
//		 */
//		Assert.assertTrue(!car.getPath().equals(path));
//
//		/*
//		 * Test: RoadBarrier are installed
//		 */
//		long numberOfBlockedRoads = server0.getBlockedRoads(currentTime).size();
//		assertEquals(3, numberOfBlockedRoads);
//
//		/*
//		 * Test: Removed edges from graph
//		 */
//		assertEquals((numberOfEdgesInGraph - numberOfBlockedRoads), server0.getGraph().getEdgeCount());
//
//		log.debug("#### STEP +2000 ##########################################################################");
//
//		currentTime += 1000;
//		eventList = new EventList(null, currentTime, UUID.randomUUID());
//		server0.update(eventList);
//		SENSOR_REGISTRY.update(eventList);
//
//		/*
//		 * Test: new car path
//		 */
//		Assert.assertTrue(!car.getPath().equals(path));
//
//		/*
//		 * Test: RoadBarrier are installed
//		 */
//		numberOfBlockedRoads = server0.getBlockedRoads(currentTime).size();
//		assertEquals(0, numberOfBlockedRoads);
//
//		/*
//		 * Test: Removed edges from graph
//		 */
//		assertEquals(numberOfEdgesInGraph, server0.getGraph().getEdgeCount());
//	}
//
//	// @Test
//	public void vehicleNoPathTest() throws Exception {
//		log.debug("###########");
//		log.debug("TEST - No new path");
//		log.debug("###########");
//
//		TrafficServerLocal server0 = createTrafficServer(null);
//
//		StartParameter startParam = new StartParameter();
//		server0.start(startParam);
//
//		// Create car
//		Vehicle<CarData> car = server0.getCarFactory().createRandomCar(
//				
//				new SensorHelper(getUniqueSensorID(), null, SensorTypeEnum.GPS_CAR, new ArrayList<SensorInterfererType>(),
//						""));
//		car.setName("K.I.T.T");
//
//		Path path;
//		do {
//			TrafficTrip trip = server0.createTrip(server0.getCityZone(), car.getData().getType());
//			path = server0.getShortestPath(server0.getGraph().getNode(trip.getStartNode()),
//					server0.getGraph().getNode(trip.getTargetNode()));
//		} while (path.getNodeCount() < 5);
//
//		// Set path
//		log.debug(path.toString());
//		car.setPath(path);
//
//		// Blocked node
//		Node closedNode = car.getPath().getNodePath().get(1);
//		log.debug("Node " + closedNode + " Current node=" + car.getCurrentNode() + " Next node=" + car.getNextNode());
//		log.debug("#### STEP 0 ##########################################################################");
//
//		server0.getScheduler().scheduleItem(new DefaultScheduleItem(car, SIMULATION_START, 1000));
//
//		long currentTime = SIMULATION_START;
//		EventList eventList = new EventList(null, currentTime, UUID.randomUUID());
//
//		/*
//		 * Test: One car should be scheduled
//		 */
//		assertEquals(1, (server0.getScheduler().getScheduledItems().size()));
//
//		/*
//		 * UPDATE
//		 */
//		server0.update(eventList);
//		SENSOR_REGISTRY.update(eventList);
//
//		/*
//		 * Test: car path
//		 */
//		Assert.assertTrue(car.getPath().equals(path));
//
//		/*
//		 * Test: RoadBarrier are installed
//		 */
//		assertEquals(0, (server0.getBlockedRoads(currentTime).size()));
//		log.debug("Node " + closedNode + " Current node=" + car.getCurrentNode() + " Next node=" + car.getNextNode());
//		log.debug("#### STEP +1000 ##########################################################################");
//
//		currentTime += 1000;
//		long numberOfEdgesInGraph = server0.getGraph().getEdgeCount();
//
//		// Create RoadBarrier
//		RoadBarrierTrafficEvent event = new RoadBarrierTrafficEvent(server0, currentTime, currentTime + 1000, currentTime, currentTime+1000,
//				new Coordinate(), closedNode.getId());
//		eventList = new EventList(Arrays.asList(event), currentTime, UUID.randomUUID());
//		server0.update(eventList);
//		SENSOR_REGISTRY.update(eventList);
//
//		/*
//		 * Test: new car path
//		 */
//		Assert.assertTrue(car.getPath().equals(path));
//
//		/*
//		 * Test: RoadBarrier are installed
//		 */
//		long numberOfBlockedRoads = server0.getBlockedRoads(currentTime).size();
//		assertEquals(3, numberOfBlockedRoads);
//
//		/*
//		 * Test: Removed edges from graph
//		 */
//		assertEquals((numberOfEdgesInGraph - numberOfBlockedRoads), server0.getGraph().getEdgeCount());
//		log.debug("Node " + closedNode + " Current node=" + car.getCurrentNode() + " Next node=" + car.getNextNode());
//		log.debug("#### STEP +2000 ##########################################################################");
//
//		currentTime += 1000;
//		eventList = new EventList(null, currentTime, UUID.randomUUID());
//		server0.update(eventList);
//		SENSOR_REGISTRY.update(eventList);
//
//		/*
//		 * Test: new car path
//		 */
//		Assert.assertTrue(car.getPath().equals(path));
//
//		/*
//		 * Test: RoadBarrier are installed
//		 */
//		numberOfBlockedRoads = server0.getBlockedRoads(currentTime).size();
//		assertEquals(0, numberOfBlockedRoads);
//
//		/*
//		 * Test: Removed edges from graph
//		 */
//		assertEquals(numberOfEdgesInGraph, server0.getGraph().getEdgeCount());
//		log.debug("Node " + closedNode + " Current node=" + car.getCurrentNode() + " Next node=" + car.getNextNode());
//	}
//
//	@Test
//	public void busTest() throws Exception {
//		log.debug("###########");
//		log.debug("TEST: Set road barrier with new path for bus");
//		log.debug("###########");
//
//		List<AbstractTrafficEvent> trafficEventList = new ArrayList<>();
//		List<BusRoute> busRoutes = new ArrayList<>();
//		BusRoute b301a = new BusRoute("301", "Eversten", 3);
//		busRoutes.add(b301a);
//		List<CreateRandomVehicleData> busDataList = new ArrayList<>();
//		int tnbt = (new DefaultBusService()).getTotalNumberOfBusTrips(busRoutes, SIMULATION_START);
//		for (int i = 0; i < tnbt; i++) {
//			List<SensorHelper> sensorLists = new ArrayList<>();
//			sensorLists.add(new SensorHelper(getUniqueSensorID(), new Coordinate(), SensorTypeEnum.GPS_BUS,
//					new ArrayList<SensorInterfererType>(), ""));
//			sensorLists.add(new SensorHelper(getUniqueSensorID(), new Coordinate(), SensorTypeEnum.INFRARED,
//					new ArrayList<SensorInterfererType>(), ""));
//			busDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation( true,
//					VehicleTypeEnum.BUS, VehicleModelEnum.BUS_CITARO, null, null)));
//		}
//		TrafficServerLocal trafficServerLocal = EasyMock.createNiceMock(TrafficServerLocal.class);
//		trafficEventList.add(new CreateBussesEvent(trafficServerLocal, SIMULATION_START, 0, busDataList, busRoutes));
//		EventList eventList = new EventList(trafficEventList, SIMULATION_START, UUID.randomUUID());
//
//		TrafficServerLocal server0 = createTrafficServer(null);
//
//		StartParameter startParam = new StartParameter();
//		server0.start(startParam);
//
//		server0.update(eventList);
//
//		log.debug("#### STEP 0 ##########################################################################");
//
//		List<DefaultScheduleItem> vehicleItems = server0.getScheduler().getExpiredItems(SIMULATION_END);
//		Vehicle<?> bus = vehicleItems.get(0).getVehicle();
//		vehicleItems.get(0).setDepartureTime(SIMULATION_START);
//		Path path = bus.getPath();
//
//		// Set path
//		log.debug(path.toString());
//
//		// // Create bus stops
//		// List<String> busStopOrder = new ArrayList<>();
//		// Map<String, Node> busStops = new HashMap<>();
//		//
//		// Node busNode = bus.getPath().getNodePath().get(0);
//		// busStopOrder.add(busNode.getId());
//		// busStops.put(busNode.getId(), busNode);
//		//
//		// busNode = bus.getPath().getNodePath().get(2);
//		// busStopOrder.add(busNode.getId());
//		// busStops.put(busNode.getId(), busNode);
//		//
//		// busNode = bus.getPath().getNodePath().get(4);
//		// busStopOrder.add(busNode.getId());
//		// busStops.put(busNode.getId(), busNode);
//		//
//		// busNode = bus.getPath().getNodePath().get(6);
//		// busStopOrder.add(busNode.getId());
//		// busStops.put(busNode.getId(), busNode);
//		//
//		// busNode = bus.getPath().getNodePath().get(bus.getPath().getNodePath().size() - 1);
//		// busStopOrder.add(busNode.getId());
//		// busStops.put(busNode.getId(), busNode);
//		//
//		// bus.getData().setBusStops(busStops);
//		// bus.getData().setBusStopOrder(busStopOrder);
//		// bus.getData().setLastBusStop(0);
//
//		// Block bus stop
//		Node closedNode = ((BusData) bus.getData()).getBusStops().get(
//				((BusData) bus.getData()).getBusStopOrder().get(1));
//
//		long currentTime = SIMULATION_START + 1000;
//		eventList = new EventList(null, currentTime, UUID.randomUUID());
//
//		/*
//		 * UPDATE
//		 */
//		server0.update(eventList);
//		SENSOR_REGISTRY.update(eventList);
//
//		/*
//		 * Test: car path
//		 */
//		Assert.assertTrue(bus.getPath().equals(path));
//
//		/*
//		 * Test: RoadBarrier are installed
//		 */
//		assertEquals(0, (server0.getBlockedRoads(currentTime).size()));
//
//		log.debug("#### STEP +1000 ##########################################################################");
//
//		currentTime += 1000;
//		long numberOfEdgesInGraph = server0.getGraph().getEdgeCount();
//
//		// Create RoadBarrier
//		RoadBarrierTrafficEvent event = new RoadBarrierTrafficEvent(server0, currentTime, currentTime + 1000, currentTime, currentTime+1000, 
//				new Coordinate(), closedNode.getId());
//		eventList = new EventList(Arrays.asList(event), currentTime, UUID.randomUUID());
//		server0.update(eventList);
//		SENSOR_REGISTRY.update(eventList);
//
//		/*
//		 * Test: new car path
//		 */
//		Assert.assertTrue(!bus.getPath().equals(path));
//
//		/*
//		 * Test: RoadBarrier are installed
//		 */
//		long numberOfBlockedRoads = server0.getBlockedRoads(currentTime).size();
//		assertEquals(3, numberOfBlockedRoads);
//
//		/*
//		 * Test: Removed edges from graph
//		 */
//		assertEquals((numberOfEdgesInGraph - numberOfBlockedRoads), server0.getGraph().getEdgeCount());
//
//		log.debug("#### STEP +2000 ##########################################################################");
//
//		currentTime += 1000;
//		eventList = new EventList(null, currentTime, UUID.randomUUID());
//		server0.update(eventList);
//		SENSOR_REGISTRY.update(eventList);
//
//		/*
//		 * Test: new car path
//		 */
//		Assert.assertTrue(!bus.getPath().equals(path));
//
//		/*
//		 * Test: RoadBarrier are installed
//		 */
//		numberOfBlockedRoads = server0.getBlockedRoads(currentTime).size();
//		assertEquals(0, numberOfBlockedRoads);
//
//		/*
//		 * Test: Removed edges from graph
//		 */
//		assertEquals(numberOfEdgesInGraph, server0.getGraph().getEdgeCount());
//	}
//
//	/**
//	 * Create the {@link TrafficServer}
//	 * 
//	 * @param serverList
//	 *            List with all traffic servers
//	 * @return TrafficServerLocal
//	 * @throws IllegalStateException
//	 * @throws InitializationException
//	 */
//	private TrafficServerLocal<?> createTrafficServer(List<TrafficServerLocal<VehicleEvent>> serverList) throws IllegalStateException,
//			InitializationException {
//		// durch einen outputstream am besten ersetzen...
//		// TrafficSensorFactory sensorFactory = new CSVTrafficSensorFactory(CSV_OUTPUT, sd.getRandomSeedService(),
//		// sd.getController(WeatherController.class), mapper);
//
//		EntityManager entityManager = EasyMock.createMock(EntityManager.class);
//		SENSOR_REGISTRY = new DefaultSensorRegistry(entityManager);
//
//		Coordinate referencePoint = new Coordinate(52.516667, 13.4);
//		TrafficEventHandlerManager<TrafficEventHandler<VehicleEvent>,VehicleEvent> eventHandlerManager = EasyMock.createNiceMock(TrafficEventHandlerManager.class);
//		TrafficServerLocal<?> server0 = new DefaultTrafficServer(
//			referencePoint, 
//			sd, SENSOR_REGISTRY,
//				eventHandlerManager, serverList, sensorFactory, null);
//
//		InitParameter initParam = new InitParameter();
//		initParam.setCityInfrastructureData(city);
//		initParam.setStartTimestamp(SIMULATION_START);
//		initParam.setEndTimestamp(SIMULATION_END);
//		initParam.setInterval(1000);
//		initParam.setTrafficFuzzyData(new TrafficFuzzyData(1, 0.9, 1));
//		server0.init(initParam);
//		return server0;
//	}
//}
