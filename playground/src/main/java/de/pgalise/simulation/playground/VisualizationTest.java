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
 
package de.pgalise.simulation.playground;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.sensorFramework.FileOutputServer;
import de.pgalise.simulation.sensorFramework.Server;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.Node;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.shared.controller.ServerConfiguration.Entity;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateBussesEvent;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorHelperPhotovoltaik;
import de.pgalise.simulation.shared.sensor.SensorHelperSmartMeter;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.shared.traffic.BusRoute;
import de.pgalise.simulation.shared.traffic.VehicleInformation;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;
import de.pgalise.util.GTFS.service.DefaultBusService;
import de.pgalise.util.cityinfrastructure.impl.DefaultBuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.impl.OSMCityInfrastructureData;

/**
 * The VisualizationTest can start a simulation without the control center. You can change the constants for several
 * settings.
 * 
 * @author Timo
 */
public class VisualizationTest {
	/**
	 * Start of the simulation
	 */
	private static final Calendar SIMULATION_START = new GregorianCalendar(2011, 8, 11, 8, 0);

	/**
	 * End of the simulation
	 */
	private static final Calendar SIMULATION_END = new GregorianCalendar(2011, 9, 17, 11, 59);

	/**
	 * Update interval of the simulation
	 */
	private static final int SIMULATION_INTERVAL_IN_MILLIES = 60000;

	/**
	 * The simulation will wait for time after every step.
	 */
	private static final int SIMULATION_CLOCKTIME_INTERVAL_IN_MILLIES = 1000;

	/**
	 * How many random cars:
	 */
	private static final int RANDOM_CARS_NUMBER = 100;

	/**
	 * How many cars will drive to obama. OBBAAAMMAAA
	 */
	private static final int OBAMA_CAR_NUMBER = 0;

	/**
	 * How many random bikes:
	 */
	private static final int RANDOM_BIKES_NUMBER = 0;

	/**
	 * How many random trucks:
	 */
	private static final int RANDOM_TRUCKS_NUMBER = 0;

	/**
	 * How many random trucks:
	 */
	private static final int RANDOM_MOTORCYCLES_NUMBER = 0;

	/**
	 * URL for the operation servlet:
	 */
	private static final String OC_SERVLET_URL = "http://localhost:8080/operationCenter/OCSimulationServlet";

	/**
	 * Random seed service to produce initial sensors and events.
	 */
	private static final RandomSeedService RANDOM_SEED_SERVICE = new DefaultRandomSeedService();

	/**
	 * Use a mock for the operation center. Useful if you don't want to deploy it.
	 */
	private static final boolean USE_OC_MOCK = true;

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(VisualizationTest.class);

	/**
	 * Inputstream for OSM
	 */
	private static final InputStream INPUTSTREAM_OSM = OSMCityInfrastructureData.class
			.getResourceAsStream("/oldenburg_pg.osm");

	/**
	 * Inputstream for busstops
	 */
	private static final InputStream INPUTSTREAM_BUSSTOPS = OSMCityInfrastructureData.class
			.getResourceAsStream("/stops.txt");

	/**
	 * Used City
	 */
	private static final City CITY = new City();

	/**
	 * Set with all used sensor ids.
	 */
	private static final Set<Integer> USED_SENSOR_IDS = new HashSet<>();

	private static final boolean START_DUMMY_SERVER = true;
	private static String CSV_OUTPUT = System.getProperty("user.dir") + "/stream_output.csv";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Server server = null;
		if (START_DUMMY_SERVER) {
			server = new FileOutputServer(new File(CSV_OUTPUT), null, 6666);
			server.open();
		}

		try {
			System.getProperties().put("simulation.configuration.filepath",
					System.getProperties().get("user.dir") + "/src/main/resources/simulation.conf");

			Properties prop = new Properties();
			prop.load(Controller.class.getResourceAsStream("/jndi.properties"));
			EJBContainer container = EJBContainer.createEJBContainer(prop);
			Context ctx = container.getContext();

			/* init random seed service: */
			RANDOM_SEED_SERVICE.init(SIMULATION_START.getTimeInMillis());

			/* create city infrastructure */
			CityInfrastructureData cityInfrastructure = new OSMCityInfrastructureData(INPUTSTREAM_OSM,
					INPUTSTREAM_BUSSTOPS, new DefaultBuildingEnergyProfileStrategy());

			/* Create init paramter: */
			InitParameter initParameter = new InitParameter(cityInfrastructure, produceServerConfiguration(),
					SIMULATION_START.getTimeInMillis(), SIMULATION_END.getTimeInMillis(),
					SIMULATION_INTERVAL_IN_MILLIES, SIMULATION_CLOCKTIME_INTERVAL_IN_MILLIES, OC_SERVLET_URL, "",
					new TrafficFuzzyData(1, 0.9, 1), cityInfrastructure.getBoundary());

			/* Create start parameter: */
			StartParameter startParameter = new StartParameter(CITY, true, produceWeatherEventHelperList(),
					produceBusRouteList());

			/* Create simulation controller and init/start the simulation: */
			SimulationController simulationController = (SimulationController) ctx
					.lookup("java:global/de.pgalise.simulation.simulationController-impl/de.pgalise.simulation.SimulationController");

			if (USE_OC_MOCK) {
				OCMock oc = new OCMock();
				simulationController._setOperationCenterController(oc);
				simulationController._getEventInitiator()._setOperationCenterController(oc);
			}

			/* Mock controlcenter controller: */
			ControlCenterController controlCenterMock = new ControlCenterControllerMock();
			simulationController._setControlCenterController(controlCenterMock);
			simulationController._getEventInitiator()._setControlCenterController(controlCenterMock);

			simulationController.init(initParameter);

			long startTime = System.currentTimeMillis();
			log.debug("Creating EventLists...");
			List<EventList> eventList = produceSimulationEventLists(cityInfrastructure, startParameter);
			log.debug("Finished creating EventList, elapsed time: " + (System.currentTimeMillis() - startTime) / 1000
					/ 60);

			simulationController.createSensors(produceSensorHelperList(cityInfrastructure));
			for (EventList list : eventList) {
				simulationController.addSimulationEventList(list);
			}

			simulationController.start(startParameter);

			/* Wait till the end */
			simulationController._getEventInitiator()._getEventThread().join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (server != null) {
			server.waitToOpen();
			server.close();
		}
	}

	/**
	 * Creates a list with bus routes.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private static List<BusRoute> produceBusRouteList() throws ClassNotFoundException, SQLException {
		List<BusRoute> busRoutes = new ArrayList<>();
		BusRoute b301a = new BusRoute("301a", "301", "Eversten", 3);
		busRoutes.add(b301a);
		return busRoutes;
		// return (new DefaultBusService().getAllBusRoutes());
	}

	/**
	 * Creates the server configuration. Change this for more traffic servers or for distributed servers.
	 * 
	 * @return
	 */
	private static ServerConfiguration produceServerConfiguration() {
		ServerConfiguration conf = new ServerConfiguration();
		List<Entity> entities = new ArrayList<>();
		entities.add(new Entity(ServiceDictionary.RANDOM_SEED_SERVICE));
		entities.add(new Entity(ServiceDictionary.WEATHER_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.ENERGY_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.FRONT_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.STATIC_SENSOR_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.TRAFFIC_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.TRAFFIC_SERVER));
		conf.getConfiguration().put("127.0.0.1:8080", entities);

		return conf;
	}

	/**
	 * Create sensor helper list.
	 * 
	 * @param cityInfrastructure
	 * @return
	 */
	private static List<SensorHelper> produceSensorHelperList(CityInfrastructureData cityInfrastructure) {
		List<SensorHelper> sensorHelperList = new ArrayList<>();
		/*
		 * add sensor helpers here:
		 */
		// for(int i = 0; i < 1000; i++) {
		// sensorHelperList.add(new SensorHelper(i + 12121, new GeoLocation(12, 12), SensorType.ANEMOMETER, new
		// ArrayList<SensorInterfererType>()));
		// }

		Node inductionLoopNode1 = cityInfrastructure.getNearestStreetNode(53.156132, 8.2028988);
		sensorHelperList.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(inductionLoopNode1.getLatitude(),
				inductionLoopNode1.getLongitude()), SensorType.INDUCTIONLOOP, new LinkedList<SensorInterfererType>(),
				inductionLoopNode1.getId()));

		Node inductionLoopNode2 = cityInfrastructure.getNearestStreetNode(53.153716, 8.2072488);
		sensorHelperList.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(inductionLoopNode2.getLatitude(),
				inductionLoopNode2.getLongitude()), SensorType.INDUCTIONLOOP, new LinkedList<SensorInterfererType>(),
				inductionLoopNode2.getId()));

		Node inductionLoopNode3 = cityInfrastructure.getNearestStreetNode(53.152037, 8.203520);
		sensorHelperList.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(inductionLoopNode3.getLatitude(),
				inductionLoopNode3.getLongitude()), SensorType.INDUCTIONLOOP, new LinkedList<SensorInterfererType>(),
				inductionLoopNode3.getId()));

		Node photovoltaikNode = cityInfrastructure.getNearestNode(53.155280, 8.212693);
		sensorHelperList.add(new SensorHelperPhotovoltaik(getUniqueSensorID(), new GeoLocation(photovoltaikNode
				.getLatitude(), photovoltaikNode.getLongitude()), 50, 1, new LinkedList<SensorInterfererType>(), null));

		Node photovoltaikNode2 = cityInfrastructure.getNearestNode(53.157676, 8.203568);
		sensorHelperList
				.add(new SensorHelperPhotovoltaik(getUniqueSensorID(), new GeoLocation(photovoltaikNode2.getLatitude(),
						photovoltaikNode2.getLongitude()), 50, 1, new LinkedList<SensorInterfererType>(), null));

		Node photovoltaikNode3 = cityInfrastructure.getNearestNode(53.153893, 8.195779);
		sensorHelperList
				.add(new SensorHelperPhotovoltaik(getUniqueSensorID(), new GeoLocation(photovoltaikNode3.getLatitude(),
						photovoltaikNode3.getLongitude()), 50, 1, new LinkedList<SensorInterfererType>(), null));

		Node photovoltaikNode4 = cityInfrastructure.getNearestNode(53.168823, 8.233609);
		sensorHelperList
				.add(new SensorHelperPhotovoltaik(getUniqueSensorID(), new GeoLocation(photovoltaikNode4.getLatitude(),
						photovoltaikNode4.getLongitude()), 50, 1, new LinkedList<SensorInterfererType>(), null));

		Node photovoltaikNode5 = cityInfrastructure.getNearestNode(53.172013, 8.215714);
		sensorHelperList
				.add(new SensorHelperPhotovoltaik(getUniqueSensorID(), new GeoLocation(photovoltaikNode5.getLatitude(),
						photovoltaikNode5.getLongitude()), 50, 1, new LinkedList<SensorInterfererType>(), null));

		Node smartMeterNode = cityInfrastructure.getNearestNode(53.174457, 8.21369);
		sensorHelperList.add(new SensorHelperSmartMeter(getUniqueSensorID(), new GeoLocation(smartMeterNode
				.getLatitude(), smartMeterNode.getLongitude()), 50, 1, new LinkedList<SensorInterfererType>(), null));

		Node smartMeterNode2 = cityInfrastructure.getNearestNode(53.168283, 8.210564);
		sensorHelperList.add(new SensorHelperSmartMeter(getUniqueSensorID(), new GeoLocation(smartMeterNode2
				.getLatitude(), smartMeterNode2.getLongitude()), 50, 1, new LinkedList<SensorInterfererType>(), null));

		Node smartMeterNode3 = cityInfrastructure.getNearestNode(53.160101, 8.196101);
		sensorHelperList.add(new SensorHelperSmartMeter(getUniqueSensorID(), new GeoLocation(smartMeterNode3
				.getLatitude(), smartMeterNode3.getLongitude()), 50, 1, new LinkedList<SensorInterfererType>(), null));

		return sensorHelperList;
	}

	/**
	 * Produce simulation event lists. E.g. for random cars.
	 * 
	 * @param cityInfrastructureData
	 * @return
	 */
	private static List<EventList> produceSimulationEventLists(CityInfrastructureData cityInfrastructureData,
			StartParameter startParameter) {
		List<EventList> simulationEventLists = new ArrayList<>();

		/*
		 * add other simulation event lists here:
		 */

		List<AbstractTrafficEvent> trafficEventList = new ArrayList<>();

		/* create random cars */
		List<CreateRandomVehicleData> vehicleDataList = new LinkedList<>();

		for (int i = 0; i < RANDOM_CARS_NUMBER; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();
			sensorLists.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(), SensorType.GPS_CAR,
					new ArrayList<SensorInterfererType>(), ""));
			vehicleDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.CAR, VehicleModelEnum.CAR_BMW_1, null, id.toString())));
		}
		trafficEventList.add(new CreateRandomVehiclesEvent(UUID.randomUUID(), vehicleDataList));
		simulationEventLists.add(new EventList(trafficEventList, SIMULATION_START.getTimeInMillis(), UUID
				.randomUUID()));

		/* Attraction Ewe Arena: */
		Node eweArena = cityInfrastructureData.getNearestStreetNode(53.146101, 8.226614);
		List<CreateRandomVehicleData> createRandomVehicleDataListEWE = new LinkedList<>();
		for (int i = 0; i < OBAMA_CAR_NUMBER; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();
			sensorLists.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(), SensorType.GPS_CAR,
					new ArrayList<SensorInterfererType>(), ""));
			createRandomVehicleDataListEWE.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id,
					true, VehicleTypeEnum.CAR, VehicleModelEnum.CAR_BMW_1, null, id.toString())));
		}
		trafficEventList.add(new AttractionTrafficEvent(UUID.randomUUID(),
				SIMULATION_START.getTimeInMillis() + 1000 * 60 * 35, // 30 Min
				SIMULATION_START.getTimeInMillis() + 1000 * 60 * 65, // 1 Stunde
				eweArena.getId(), createRandomVehicleDataListEWE));

		/* create busses */
		List<CreateRandomVehicleData> busDataList = new ArrayList<>();
		int tnbt = (new DefaultBusService()).getTotalNumberOfBusTrips(startParameter.getBusRouteList(),
				SIMULATION_START.getTimeInMillis());
		for (int i = 0; i < tnbt; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();
			sensorLists.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(), SensorType.GPS_BUS,
					new ArrayList<SensorInterfererType>(), ""));
			sensorLists.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(), SensorType.INFRARED,
					new ArrayList<SensorInterfererType>(), ""));
			busDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.BUS, VehicleModelEnum.BUS_CITARO, null, id.toString())));
		}
		trafficEventList.add(new CreateBussesEvent(UUID.randomUUID(), busDataList, SIMULATION_START.getTimeInMillis(),
				startParameter.getBusRouteList()));

		/* create random trucks */
		for (int i = 0; i < RANDOM_TRUCKS_NUMBER; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();
			sensorLists.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(), SensorType.GPS_TRUCK,
					new ArrayList<SensorInterfererType>(), ""));
			vehicleDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.TRUCK, VehicleModelEnum.TRUCK_COCA_COLA, null, id.toString())));
		}

		/* create random motorcycles */
		for (int i = 0; i < RANDOM_MOTORCYCLES_NUMBER; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();
			sensorLists.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(), SensorType.GPS_MOTORCYCLE,
					new ArrayList<SensorInterfererType>(), ""));
			vehicleDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.MOTORCYCLE, VehicleModelEnum.MOTORCYCLE_KAWASAKI_NINJA, null, id.toString())));
		}

		/* create random bikes */
		for (int i = 0; i < RANDOM_BIKES_NUMBER; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();
			sensorLists.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(), SensorType.GPS_BIKE,
					new ArrayList<SensorInterfererType>(), ""));
			vehicleDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.BIKE, VehicleModelEnum.BIKE_GAZELLE, null, id.toString())));
		}

		return simulationEventLists;
	}

	/**
	 * Produces weather event helper lists:
	 * 
	 * @return
	 */
	private static List<WeatherEventHelper> produceWeatherEventHelperList() {

		/*
		 * HERE: add weather event helper lists here, if needed!
		 */

		return null;
	}

	/**
	 * Returns a new unique sensor id.
	 * 
	 * @param randomSeedService
	 * @return
	 */
	private static int getUniqueSensorID() {
		Random random = new Random(RANDOM_SEED_SERVICE.getSeed(VisualizationTest.class.getName()));
		int sensorID = random.nextInt();
		while (sensorID < 1 || USED_SENSOR_IDS.contains(sensorID)) {
			sensorID = random.nextInt();
		}
		USED_SENSOR_IDS.add(sensorID);
		return sensorID;
	}

	/**
	 * Mock for operation center controller.
	 * 
	 * @author Timo
	 */
	private static class OCMock extends AbstractController implements OperationCenterController {

		@Override
		public void createSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public void deleteSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
			return false;
		}

		@Override
		public void displayException(Exception exception) throws IllegalStateException {
		}

		@Override
		protected void onInit(InitParameter param) throws InitializationException {
			log.debug("Init OC");
		}

		@Override
		protected void onReset() {
		}

		@Override
		protected void onStart(StartParameter param) {
			log.debug("Start OC");
		}

		@Override
		protected void onStop() {
		}

		@Override
		protected void onResume() {
		}

		@Override
		protected void onUpdate(EventList simulationEventList) {
		}

		@Override
		public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public String getName() {
			return "OperationCenterController";
		}
	}

	/**
	 * Mock for control center controller.
	 * 
	 * @author Timo
	 */
	private static class ControlCenterControllerMock implements ControlCenterController {

		@Override
		public void init(InitParameter param) throws InitializationException, IllegalStateException {
		}

		@Override
		public void reset() throws IllegalStateException {
		}

		@Override
		public void start(StartParameter param) throws IllegalStateException {
		}

		@Override
		public void stop() throws IllegalStateException {
		}

		@Override
		public void update(EventList simulationEventList) throws IllegalStateException {
		}

		@Override
		public StatusEnum getStatus() {
			return null;
		}

		@Override
		public void displayException(Exception exception) throws IllegalStateException {
		}

		@Override
		public String getName() {
			return "ControlCenterController";
		}

	}
}
