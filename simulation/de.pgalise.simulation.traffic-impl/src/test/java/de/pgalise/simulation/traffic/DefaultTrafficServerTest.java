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

import de.pgalise.simulation.shared.city.NavigationNode;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.sensorFramework.FileOutputServer;
import de.pgalise.simulation.sensorFramework.SensorRegistry;
import de.pgalise.simulation.sensorFramework.Server;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateBussesEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.pgalise.testutils.TestUtils;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.internal.DefaultSensorRegistry;
import de.pgalise.simulation.sensorFramework.output.tcpip.AbstractTcpIpOutput;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.FileBasedCityInfrastructureDataService;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateRandomBusData;
import de.pgalise.simulation.traffic.event.CreateRandomCarData;
import de.pgalise.simulation.traffic.event.CreateRandomTruckData;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.CreateVehiclesEvent;
import de.pgalise.simulation.traffic.internal.server.DefaultTrafficServer;
import de.pgalise.simulation.traffic.internal.server.scheduler.DefaultScheduleItem;
import de.pgalise.simulation.traffic.internal.server.sensor.DefaultTrafficSensorFactory;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.model.vehicle.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleData;
import de.pgalise.simulation.traffic.model.vehicle.TruckData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandlerManager;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.util.graph.disassembler.Disassembler;
import de.pgalise.util.graph.internal.QuadrantDisassembler;
import java.util.Date;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import org.geotools.geometry.jts.JTS;

/**
 * Tests the {@link TrafficServer}
 *
 * @author Mustafa
 * @version 1.0 (Feb 20, 2013)
 */
@ManagedBean
@LocalClient
public class DefaultTrafficServerTest {

	private static EJBContainer container;
	@EJB
	private RandomSeedService rss;
	/**
	 * Log
	 */
	private static final Logger log = LoggerFactory.getLogger(
		DefaultTrafficServerTest.class);

	/**
	 * CityInfrastructureDataService
	 */
	private static CityInfrastructureData city;

	/**
	 * Service dictionary
	 */
	private static ServiceDictionary sd;

	/**
	 * Bus stops file
	 */
	private static final String BUS_STOPS = System.getProperty("user.dir") + "/src/test/resources/stops.txt";

	/**
	 * OSM file
	 */
	private static final String OSM = System.getProperty("user.dir") + "/src/test/resources/oldenburg_pg.osm";

	/**
	 * Sensor output file
	 */
	private static final String CSV_OUTPUT = System.getProperty("user.dir") + "/stream_output.csv";

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
	private static TrafficSensorFactory sensorFactory;

	/**
	 * Implementation of SensorRegistry.
	 */
	private static SensorRegistry SENSOR_REGISTRY;
	@EJB
	private IdGenerator idGenerator;
	@EJB
	private FileBasedCityInfrastructureDataService cityFactory;

	public DefaultTrafficServerTest() throws NamingException, IOException {
		container.getContext().bind("inject",
			this);

		cityFactory.parse(new File(OSM),
			new File(BUS_STOPS));
		city = cityFactory.createCityInfrastructureData();

		WeatherController wc = createNiceMock(WeatherController.class);
		EnergyController ec = createNiceMock(EnergyController.class);

		sd = createMock(ServiceDictionary.class);
		expect(sd.getRandomSeedService()).andStubReturn(rss);
		expect(sd.getController(WeatherController.class)).andStubReturn(wc);
		expect(sd.getController(EnergyController.class)).andStubReturn(ec);

		replay(sd,
			wc);

		EntityManager entityManager = EasyMock.createMock(EntityManager.class);
		SENSOR_REGISTRY = new DefaultSensorRegistry(entityManager);

		server = new FileOutputServer(new File(CSV_OUTPUT),
			null,
			6666);
		server.open();
		sensorFactory = new DefaultTrafficSensorFactory(sd.getRandomSeedService(),
			idGenerator,
			sd.getController(WeatherController.class),
			sd.getController(EnergyController.class),
			new AbstractTcpIpOutput("127.0.0.1",
				6666,
				new TcpIpKeepOpenStrategy()),
			1);
		DefaultTrafficServerTest.sd.getRandomSeedService().init(SIMULATION_START.
			getTime());
	}

	@BeforeClass
	public static void init() throws IOException {
		container = TestUtils.getContainer();
	}

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

	@Before
	public void setUp() throws IOException {
	}

	@After
	public void tearDown() {
		File file = new File(DefaultTrafficServerTest.CSV_OUTPUT);
		if (file.exists()) {
			file.delete();
		}
		SENSOR_REGISTRY.removeAllSensors();
	}

	/*
	 * @Test
	 */
	public void moveVehicleTest() throws IllegalStateException, InitializationException, IllegalAccessException {
		log.debug("#################");
		log.debug("MOVE VEHICLE TEST");
		log.debug("#################");
		Disassembler dis = new QuadrantDisassembler();
		List<Geometry> gs = dis.disassemble(JTS.toGeometry(new Envelope(0,
			0,
			100,
			200)),
			2);

		TrafficServerLocal<VehicleEvent> server2 = createTrafficServer(null);
		server2.setCityZone(gs.get(1));

		TrafficServerLocal<VehicleEvent> server1 = createTrafficServer(Arrays.
			asList(server2));
		server1.setCityZone(gs.get(0));

		List<TrafficEdge> path = getNodes(server1,
			server2,
			1,
			gs);

		TrafficStartParameter startParam = new TrafficStartParameter();

		server1.start(startParam);
		server2.start(startParam);

		Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
			null,
			null,
			null);
		Vehicle<CarData> car = server1.getCarFactory().createRandomCar(null);
		car.setName("Car A");
		car.setPath(path);
		server1.getScheduler().scheduleItem(new DefaultScheduleItem(car,
			SIMULATION_START.getTime(),
			server1.getUpdateIntervall()));

		Sensor<?, ?> sensor2 = new GpsSensor(idGenerator.getNextId(),
			null,
			null,
			null);
		Vehicle<CarData> car3 = server1.getCarFactory().createRandomCar(null);
		car3.setName("Car B");
		car3.setPath(path);
		server1.getScheduler().scheduleItem(new DefaultScheduleItem(car3,
			SIMULATION_START.getTime() + 1000,
			server1.getUpdateIntervall()));

		// 2.auto
		int i = -1000;
		List<Vehicle<? extends VehicleData>> addedVehicles = new LinkedList<>();
		int droveOverBoundaries = 0;
		while ((i / 1000) < 6131) {
			EventList eventList = new EventList<>(idGenerator.getNextId(),
				null,
				SIMULATION_START.getTime() + i);
			i += 1000;
			server1.update(eventList);
			server2.update(eventList);
			server1.processMovedVehicles();
			server2.processMovedVehicles();

			if (server1.getScheduler().getExpiredItems(SIMULATION_START.getTime() + i).
				size() == 2) {
				droveOverBoundaries = 1;
			}

			if (server1.getScheduler().getExpiredItems(SIMULATION_START.getTime() + i).
				size() == 1 && droveOverBoundaries == 1) {
				log.info((i / 1000) + " updates needed to drive over the boundaries");// 5137
				Vehicle<?> v = createVehicle(server2,
					i / 1000);
				v.setVehicleState(VehicleStateEnum.STOPPED);
				addedVehicles.add(v);
				droveOverBoundaries = 2;
			}

			if (droveOverBoundaries == 2 && server1.getScheduler().getExpiredItems(
				SIMULATION_START.getTime() + i).
				isEmpty()) {
				droveOverBoundaries = 3;
			} else if (droveOverBoundaries == 3) {
				for (Vehicle<?> v : addedVehicles) {
					v.setVehicleState(VehicleStateEnum.DRIVING);
				}
				droveOverBoundaries++;
			}
			if (server1.getScheduler().getExpiredItems(SIMULATION_START.getTime() + i).
				isEmpty()
				&& (server2.getScheduler().getExpiredItems(
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

	private Vehicle<?> createVehicle(TrafficServerLocal server2,
		int i)
		throws IllegalAccessException {
		Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
			null,
			null,
			null);
		Vehicle<CarData> car2 = server2.getCarFactory().createRandomCar(null);
		car2.setName("Car " + i);
		TrafficNode startNode = new TrafficNode(new JaxRSCoordinate(1,
			2)), endNode = new TrafficNode(new JaxRSCoordinate(3,
					6));
		car2.setPath(server2.getShortestPath(startNode,
			endNode));

		server2.getScheduler().scheduleItem(
			new DefaultScheduleItem(car2,
				SIMULATION_START.getTime() + (5135 * 1000),
				server2.getUpdateIntervall()));

		return car2;
	}

	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	/**
	 * @return nodes from two distinct areas
	 */
	private List<TrafficEdge> getNodes(TrafficServerLocal<?> server,
		TrafficServerLocal<?> server2,
		int id,
		List<Geometry> gs) {
		int found = 0;
		TrafficNode nodes[] = new TrafficNode[2];
		while (found < 2) {
			TrafficTrip trip = server.createTrip(gs.get(0),
				VehicleTypeEnum.CAR);
			TrafficTrip trip2 = server2.createTrip(gs.get(1),
				VehicleTypeEnum.CAR);

			if (nodes[0] == null) {
				if (gs.get(0).covers(
					GEOMETRY_FACTORY.createPoint(trip.getStartNode().getGeoLocation()))) {
					log.debug("Found node in the first area: "
						+ trip.getStartNode()
						+ ", "
						+ trip.getStartNode());
					nodes[0] = trip.getStartNode();
					found++;
				}
			}
			if (nodes[1] == null) {
				if (gs.get(1).covers(
					GEOMETRY_FACTORY.createPoint(trip2.getStartNode().getGeoLocation()))) {
					log.debug("Found node in the second area: "
						+ trip2.getStartNode()
						+ ", "
						+ trip2.getStartNode());
					nodes[1] = trip2.getStartNode();
					found++;
				}
			}
			if (nodes[0] != null && nodes[1] != null) {
				try {
					List<TrafficEdge> path = server.getShortestPath(nodes[0],
						nodes[1]);
					return path;
				} catch (Exception e) {
					log.warn("No path found between the nodes");
					nodes[0] = null;
					nodes[1] = null;
					found = 0;
				}
			}
		}
		return null;
	}

	@Test
	public void createRandomCarTest() throws IllegalStateException, InitializationException {
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

		TrafficServerLocal server0 = createTrafficServer(null);

		StartParameter startParam = new StartParameter();
		server0.start(startParam);

		server0.update(eventList);

		assertEquals(
			carCount, // 2*size weil ein auto 2 x eingetragen wird
			// (2 abfahrszeiten)
			(server0.getScheduler().getScheduledItems().size() + server0.
			getScheduler()
			.getExpiredItems(SIMULATION_START.getTime()).size()));
	}

	@Test
	public void createRandomTruckTest() throws IllegalStateException, InitializationException {
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

		TrafficServerLocal server0 = createTrafficServer(null);

		StartParameter startParam = new StartParameter();
		server0.start(startParam);

		server0.update(eventList);

		assertEquals(
			truckCount, // 2*size weil ein truck 2 x eingetragen
			// wird (2 abfahrszeiten)
			(server0.getScheduler().getScheduledItems().size() + server0.
			getScheduler()
			.getExpiredItems(SIMULATION_START.getTime()).size()));
	}

	@Test
	public void createRandomBusTest() throws IllegalStateException, InitializationException {
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
			UUID id = UUID.randomUUID();
			GpsSensor sensor = new GpsSensor(idGenerator.getNextId(),
				null,
				null,
				0,
				null);
			InfraredSensor sensor2 = new InfraredSensor(idGenerator.getNextId(),
				null,
				null,
				null,
				null);
			busDataList.add(new CreateRandomBusData(sensor2,
				sensor,
				new VehicleInformation(true,
					VehicleTypeEnum.BUS,
					VehicleModelEnum.BUS_CITARO,
					null,
					id.toString())));
		}
		TrafficServerLocal<?> trafficServerLocal = EasyMock.createNiceMock(
			TrafficServerLocal.class);
		trafficEventList.add(new CreateBussesEvent(trafficServerLocal,
			SIMULATION_START.getTime(),
			0,
			busDataList,
			busRoutes));

		EventList eventList = new EventList(idGenerator.getNextId(),
			trafficEventList,
			SIMULATION_START.getTime());

		TrafficServerLocal server0 = createTrafficServer(null);

		StartParameter startParam = new StartParameter();
		server0.start(startParam);

		server0.update(eventList);

		assertEquals(
			tnbt,
			(server0.getScheduler().getScheduledItems().size() + server0.
			getScheduler()
			.getExpiredItems(SIMULATION_START.getTime()).size()));

		eventList = new EventList(idGenerator.getNextId(),
			null,
			1392697560000L);

		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);

		log.debug(
			"##############################################################################");

		eventList = new EventList(idGenerator.getNextId(),
			null,
			1392697560000L + 1000);
		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);

		log.debug(
			"##############################################################################");

		eventList = new EventList(idGenerator.getNextId(),
			null,
			1392697560000L + 2000);
		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);

		log.debug(
			"##############################################################################");

		eventList = new EventList(idGenerator.getNextId(),
			null,
			1392697560000L + 3000);
		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);
	}

	@Test
	public void updateTest() throws Exception {
		log.debug("###########");
		log.debug("UPDATE TEST");
		log.debug("###########");
		// File file = new File(DefaultTrafficServerTest.CSV_OUTPUT);

		TrafficServerLocal server0 = createTrafficServer(null);

		StartParameter startParam = new StartParameter();
		server0.start(startParam);
		JaxRSCoordinate location;
		NavigationNode nodeForStaticSensor = null;

		if (VEHICLE_TYPE.equals("car") || VEHICLE_TYPE.equals("all")) {
			Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
				null,
				null,
				null);
			Vehicle<CarData> car = server0.getCarFactory().createRandomCar(null);
			car.setName("K.I.T.T");
			TrafficTrip trip = server0.createTrip(server0.getCityZone(),
				car.getData().getType());
			car.setPath(server0.getShortestPath(trip.getStartNode(),
				trip.getTargetNode()));

			nodeForStaticSensor = car.getNodePath().get(0);

			server0.getScheduler().scheduleItem(new DefaultScheduleItem(car,
				SIMULATION_START.getTime() + 1000,
				server0.getUpdateIntervall()));
		}
		if (VEHICLE_TYPE.equals("truck") || VEHICLE_TYPE.equals("all")) {
			Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
				null,
				null,
				null);
			Vehicle<TruckData> truck = server0.getTruckFactory().createRandomTruck(
				null);
			truck.setName("Coca Cola Truck");
			TrafficTrip tripTruck = server0.createTrip(server0.getCityZone(),
				truck.getData().getType());
			truck.setPath(server0.getShortestPath(tripTruck.getStartNode(),
				tripTruck.getTargetNode()));

			server0.getScheduler().scheduleItem(new DefaultScheduleItem(truck,
				SIMULATION_START.getTime() + 1000,
				server0.getUpdateIntervall()));
		}
		if (VEHICLE_TYPE.equals("bike") || VEHICLE_TYPE.equals("all")) {
			Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
				null,
				null,
				null);
			Vehicle<BicycleData> bike = server0.getBikeFactory().createRandomBicycle(
				null);
			bike.setName("tlottmann's Fahrrad");
			TrafficTrip tripBike = server0.createTrip(server0.getCityZone(),
				bike.getData().getType());
			bike.setPath(server0.getShortestPath(tripBike.getStartNode(),
				tripBike.getTargetNode()));

			server0.getScheduler().scheduleItem(new DefaultScheduleItem(bike,
				SIMULATION_START.getTime() + 1000,
				server0.getUpdateIntervall()));
		}

		if (VEHICLE_TYPE.equals("motorcycle") || VEHICLE_TYPE.equals("all")) {
			Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
				null,
				null,
				null);
			Vehicle<MotorcycleData> motorcycle = server0.getMotorcycleFactory().
				createRandomMotorcycle(null);
			motorcycle.setName("Jens' Kawasaki");
			TrafficTrip tripMotorcycle = server0.createTrip(server0.getCityZone(),
				motorcycle.getData().getType());
			motorcycle.setPath(server0.getShortestPath(tripMotorcycle.getStartNode(),
				tripMotorcycle.getTargetNode()));

			server0.getScheduler().scheduleItem(
				new DefaultScheduleItem(motorcycle,
					SIMULATION_START.getTime() + 1000,
					server0.getUpdateIntervall()));
		}

		EventList eventList = new EventList(idGenerator.getNextId(),
			null,
			SIMULATION_START.getTime());

		if (VEHICLE_TYPE.equals("all")) {
			assertEquals(4,
				(server0.getScheduler().getScheduledItems().size()));
		} else {
			assertEquals(1,
				(server0.getScheduler().getScheduledItems().size()));
		}
		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);

		log.debug(
			"##############################################################################");

		location = ((JaxRSCoordinate) nodeForStaticSensor.getGeoLocation());
		if (location != null) {
			Sensor<?, ?> sensor = new GpsSensor(idGenerator.getNextId(),
				null,
				null,
				null);
			server0.createSensor(sensor);
		}

		eventList = new EventList(idGenerator.getNextId(),
			null,
			SIMULATION_START.getTime() + 1000);
		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);

		log.debug(
			"##############################################################################");

		eventList = new EventList(idGenerator.getNextId(),
			null,
			SIMULATION_START.getTime() + 2000);
		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);

		log.debug(
			"##############################################################################");

		eventList = new EventList(idGenerator.getNextId(),
			null,
			SIMULATION_START.getTime() + 3000);
		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);

		// es sollte eine csv datei beschrieben worden sein
		// auto ist 2x gefahren, also müssten 2 zeilen existieren
		CSVParse parser = new CSVParser(new FileReader(
			DefaultTrafficServerTest.CSV_OUTPUT));

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
	public void createVehiclesTest() throws Exception {
		log.debug("###########");
		log.debug("CREATE VEHICLE EVENT TEST");
		log.debug("###########");

		TrafficServerLocal server0 = createTrafficServer(null);

		StartParameter startParam = new StartParameter();
		server0.start(startParam);

		GpsSensor gpsSensor = new GpsSensor(idGenerator.getNextId(),
			null,
			null,
			null);
		Vehicle<CarData> car = server0.getCarFactory().createRandomCar(null);
		car.setName("K.I.T.T");
		TrafficTrip trip = server0.createTrip(server0.getCityZone(),
			car.getData().getType());
		car.setPath(server0.getShortestPath(trip.getStartNode(),
			trip.getTargetNode()));

		server0.getScheduler().scheduleItem(new DefaultScheduleItem(car,
			SIMULATION_START.getTime() + 1000,
			server0.getUpdateIntervall()));

		assertEquals(1,
			(server0.getScheduler().getScheduledItems().size()));

		EventList eventList = new EventList(idGenerator.getNextId(),
			null,
			SIMULATION_START.getTime());

		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);

		for (int i = 1000; i <= 10000; i += 1000) {
			log.debug(
				"* * * * * * * * * * Update: " + i / 1000 + " * * * * * * * * * *");

			if (i == 3000) {
				List<TrafficEvent> list = new ArrayList<>();
				List<CreateRandomVehicleData> vehicleDataList = new ArrayList<>();

				List<Sensor<?, ?>> sensorLists = new ArrayList<>();
				sensorLists.add(gpsSensor);
				TrafficNode startNode = new TrafficNode(new JaxRSCoordinate(1,
					2)), endNode = new TrafficNode(new JaxRSCoordinate(2,
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

				list.add(new CreateVehiclesEvent(server0,
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
					(server0.getScheduler().getScheduledItems().size()));
			}

			server0.update(eventList);
			SENSOR_REGISTRY.update(eventList);

			if (i == 4000) {
				assertEquals(2,
					server0.getScheduler().getExpiredItems(
						SIMULATION_START.getTime() + 4000).size());
			}
		}
	}

	@Test
	public void createAttractionTest() throws Exception {
		log.debug("###########");
		log.debug("CREATE ATTRACTION EVENT TEST");
		log.debug("###########");

		TrafficServerLocal server0 = createTrafficServer(null);

		StartParameter startParam = new StartParameter();
		server0.start(startParam);

		GpsSensor gpsSensor = new GpsSensor(idGenerator.getNextId(),
			null,
			null,
			null);
		Vehicle<CarData> car = server0.getCarFactory().createRandomCar(null);
		car.setName("K.I.T.T");
		TrafficTrip trip = server0.createTrip(server0.getCityZone(),
			car.getData().getType());
		car.setPath(server0.getShortestPath(trip.getStartNode(),
			trip.getTargetNode()));

		server0.getScheduler().scheduleItem(new DefaultScheduleItem(car,
			SIMULATION_START.getTime() + 1000,
			server0.getUpdateIntervall()));

		assertEquals(1,
			(server0.getScheduler().getScheduledItems().size()));

		EventList eventList = new EventList(idGenerator.getNextId(),
			null,
			SIMULATION_START.getTime());

		server0.update(eventList);
		SENSOR_REGISTRY.update(eventList);

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

				TrafficNode node = new TrafficNode(new JaxRSCoordinate(4,
					4));
				list.add(new AttractionTrafficEvent(server0,
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
					(server0.getScheduler().getScheduledItems().size()));
			}

			server0.update(eventList);
			SENSOR_REGISTRY.update(eventList);

			// KARR hin (KARR zurück fährt noch nicht) + KITT
			if (i == 4000) {
				assertEquals(2,
					server0.getScheduler().getExpiredItems(
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
	private TrafficServerLocal<VehicleEvent> createTrafficServer(
		List<TrafficServerLocal<VehicleEvent>> serverList) throws IllegalStateException,
		InitializationException {
		// durch einen outputstream am besten ersetzen...
		// TrafficSensorFactory sensorFactory = new CSVTrafficSensorFactory(CSV_OUTPUT, sd.getRandomSeedService(),
		// sd.getController(WeatherController.class), mapper);

		EntityManager entityManager = EasyMock.createMock(EntityManager.class);
		SENSOR_REGISTRY = new DefaultSensorRegistry(entityManager);

		JaxRSCoordinate referencePoint = new JaxRSCoordinate(52.516667,
			13.4);
		TrafficEventHandlerManager<TrafficEventHandler<VehicleEvent>, VehicleEvent> eventHandlerManager = EasyMock.
			createNiceMock(
				TrafficEventHandlerManager.class);
		TrafficServerLocal<VehicleEvent> server0 = new DefaultTrafficServer(
			referencePoint,
			sd,
			SENSOR_REGISTRY,
			eventHandlerManager,
			serverList,
			sensorFactory,
			null);
		server0.setCityZone(JTS.toGeometry(new Envelope(0,
			0,
			100,
			200)));

		TrafficInitParameter initParam = new TrafficInitParameter();
		initParam.setCityInfrastructureData(city);
		initParam.setStartTimestamp(SIMULATION_START);
		initParam.setEndTimestamp(SIMULATION_END);
		initParam.setInterval(1000);
		initParam.setTrafficFuzzyData(new TrafficFuzzyData(1,
			0.9,
			1));

		server0.init(initParam);
		return server0;
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
		VehicleModelEnum vehicleModel) {
		/* create random cars */
		List<CreateRandomVehicleData> vehicleDataList = new ArrayList<>();
		List<Sensor<?, ?>> sensorLists = new ArrayList<>();

		GpsSensor sensor = new GpsSensor(idGenerator.getNextId(),
			null,
			null,
			null);
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
		TrafficServerLocal<?> trafficServerLocal = EasyMock.createNiceMock(
			TrafficServerLocal.class);
		return new CreateRandomVehiclesEvent(trafficServerLocal,
			SIMULATION_START.getTime(),
			SIMULATION_END.getTime(),
			vehicleDataList);
	}
}
