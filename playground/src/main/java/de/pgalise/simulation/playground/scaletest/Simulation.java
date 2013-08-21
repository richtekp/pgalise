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
 
package de.pgalise.simulation.playground.scaletest;

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
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.SimulationController;
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
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehicleData;
import de.pgalise.simulation.shared.event.traffic.CreateVehiclesEvent;
import de.pgalise.simulation.shared.event.traffic.TrafficEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.shared.traffic.BusRoute;
import de.pgalise.simulation.shared.traffic.TrafficTrip;
import de.pgalise.simulation.shared.traffic.VehicleInformation;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;
import de.pgalise.util.cityinfrastructure.impl.DefaultBuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.impl.OSMCityInfrastructureData;

public class Simulation {
	//private static final String MY_HOST = "127.0.0.1:4201";
	//private static final String OTHER_HOST = "127.0.0.1:4211";
	// TODO switch here the used hosts
	private static final String MY_HOST = "127.0.0.1:8080";
	private static final String OTHER_HOST = "127.0.0.1:8081";
	
	/**
	 * When set to true the tomee container will run in embedded mode. Service will not be distributed then.
	 */
	private static final boolean RUN_EMBEDDED = false;
	/**
	 * Use a mock for the operation center. Useful if you don't want to deploy it.
	 */
	private static final boolean USE_OC_MOCK = true;

	/* Settings: */
	private static final Logger log = LoggerFactory.getLogger(Simulation.class);

	/**
	 * Start of the simulation
	 */
	private static final Calendar SIMULATION_START = new GregorianCalendar(2011, 8, 11, 8, 0);

	/**
	 * End of the simulation
	 */
	private static final Calendar SIMULATION_END = new GregorianCalendar(2011, 8, 11, 8, 20);

	/**
	 * Update interval of the simulation
	 */
	private static final int SIMULATION_INTERVAL_IN_MILLIES = 1000;

	/**
	 * The simulation will wait for time after every step.
	 */
	private static final int SIMULATION_CLOCKTIME_INTERVAL_IN_MILLIES = 300;

	/**
	 * URL for the operation controller servlet:
	 */
	private static final String OC_SERVLET_URL = "http://127.0.0.1:8080/operationCenter/OCSimulationServlet";

	/**
	 * URL for the control center servlet:
	 */
	private static final String CC_SERVLET_URL = "http://127.0.0.1:8080/controlCenter/ControlCenter";

	/**
	 * Random seed service to produce initial sensors and events.
	 */
	private final RandomSeedService RANDOM_SEED_SERVICE = new DefaultRandomSeedService();

	/**
	 * Used City
	 */
	private final City CITY = new City();

	/**
	 * Set with all used sensor ids.
	 */
	private final Set<Integer> USED_SENSOR_IDS = new HashSet<>();

	private int carCount = 0;

	private Context ctx;

	private EJBContainer container;

	public Simulation() {

	}

	public void init() {
		Properties props = new Properties();
		try {
			props.load(Controller.class.getResourceAsStream("/jndi.properties"));
			props.put("openejb.client.connection.pool.timeout", 3600000);
			props.put("openejb.client.connectionpool.timeout", 3600000);
			props.put("foo", "new://Container?type=STATEFUL");
			props.put("foo.timeout", "3600000");
			props.put("bar", "new://TransactionManager");
			props.put("bar.defaultTransactionTimeoutSeconds", 3600);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (RUN_EMBEDDED) {
			System.getProperties().put("simulation.configuration.filepath",
					System.getProperties().get("user.dir") + "/src/main/resources/simulation.conf");
			System.getProperties().put("simulation.configuration.scaletest.path",
					System.getProperties().get("user.dir"));
			container = EJBContainer.createEJBContainer(props);
			ctx = container.getContext();
		} else {
			props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
			// TODO switch here the used protocol
			// props.put(Context.PROVIDER_URL, "http://" + MY_HOST + "/tomee/ejb");
			props.put(Context.PROVIDER_URL, "http://" + MY_HOST +"/tomee/ejb");
			try {
				ctx = new InitialContext(props);
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	public void run(int carCount) {
		this.carCount = carCount;
		try {
			/**
			 * Inputstream for OSM
			 */
			InputStream INPUTSTREAM_OSM = OSMCityInfrastructureData.class.getResourceAsStream("/oldenburg_pg.osm");

			/**
			 * Inputstream for busstops
			 */
			InputStream INPUTSTREAM_BUSSTOPS = OSMCityInfrastructureData.class.getResourceAsStream("/stops.txt");

			/* create city infrastructure */
			CityInfrastructureData cityInfrastructure = new OSMCityInfrastructureData(INPUTSTREAM_OSM,
					INPUTSTREAM_BUSSTOPS, new DefaultBuildingEnergyProfileStrategy());

			/* Create init paramter: */
			InitParameter initParameter = new InitParameter(cityInfrastructure, produceServerConfiguration(),
					SIMULATION_START.getTimeInMillis(), SIMULATION_END.getTimeInMillis(),
					SIMULATION_INTERVAL_IN_MILLIES, SIMULATION_CLOCKTIME_INTERVAL_IN_MILLIES, OC_SERVLET_URL,
					CC_SERVLET_URL, new TrafficFuzzyData(1, 0.9, 1), cityInfrastructure.getBoundary());

			/* Create start parameter: */
			StartParameter startParameter = new StartParameter(CITY, true, produceWeatherEventHelperList(),
					produceBusRouteList());

			/* Create simulation controller and init/start the simulation: */
			final SimulationController simulationController;
			if (RUN_EMBEDDED) {
				simulationController = (SimulationController) ctx
						.lookup("java:global/de.pgalise.simulation.simulationController-impl/de.pgalise.simulation.SimulationController");
			} else {
				simulationController = (SimulationController) ctx
						.lookup("de.pgalise.simulation.SimulationControllerRemote");
			}

			if (USE_OC_MOCK) {
				OCMock oc = new OCMock();
				simulationController._setOperationCenterController(oc);
				simulationController._getEventInitiator()._setOperationCenterController(oc);

				/* Mock controlcenter controller: */
				ControlCenterController controlCenterMock = new ControlCenterControllerMock();
				simulationController._setControlCenterController(controlCenterMock);
				simulationController._getEventInitiator()._setControlCenterController(controlCenterMock);
			}

			simulationController.init(initParameter);

			long startTime = System.currentTimeMillis();
			log.debug("Creating EventLists...");
			List<SimulationEventList> eventList = produceSimulationEventLists(cityInfrastructure, startParameter);
			log.debug("Finished creating EventList, elapsed time: " + (System.currentTimeMillis() - startTime) / 1000
					/ 60);

			simulationController.createSensors(produceSensorHelperList(cityInfrastructure));
			for (SimulationEventList list : eventList) {
				simulationController.addSimulationEventList(list);
			}

			simulationController.start(startParameter);

			final Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (simulationController.getSimulationTimestamp() < SIMULATION_END.getTimeInMillis()) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					simulationController.stop();
					simulationController.reset();
				}
			});
			thread.start();
			thread.join();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (container != null) {
			try {
				container.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the server configuration. Change this for more traffic servers or for distributed servers.
	 * 
	 * @return
	 */
	private ServerConfiguration produceServerConfiguration() {
		ServerConfiguration conf = new ServerConfiguration();
		List<Entity> entities = new ArrayList<>();
		entities.add(new Entity(ServiceDictionary.RANDOM_SEED_SERVICE));
		entities.add(new Entity(ServiceDictionary.WEATHER_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.ENERGY_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.FRONT_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.STATIC_SENSOR_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.TRAFFIC_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.TRAFFIC_SERVER));
		conf.getConfiguration().put(MY_HOST, entities);

		// TODO andere server konfigurieren
//		if (!RUN_EMBEDDED) {
//			entities = new ArrayList<>();
//			entities.add(new Entity(ServiceDictionary.TRAFFIC_SERVER));
//			conf.getConfiguration().put(OTHER_HOST, entities);
//		}

		return conf;
	}

	/**
	 * Returns a random GeoLocation inside the given cityinfrastructuredata
	 * 
	 * @return random GeoLocation
	 */
	private Node getRandomGeoLocation(CityInfrastructureData city, Random random) {

		Node resultNode = city.getNearestNode(
		/* random latitude in boundary */
		city.getBoundary().getSouthWest().x
				+ (random.nextDouble() * (city.getBoundary().getNorthEast().x - city
						.getBoundary().getSouthWest().x)),

		/* random longitude in boundary */
		city.getBoundary().getSouthWest().y
				+ (random.nextDouble() * (city.getBoundary().getNorthEast().y - city
						.getBoundary().getSouthWest().y)));

		return resultNode;
	}

	/**
	 * Creates a list with bus routes.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private List<BusRoute> produceBusRouteList() throws ClassNotFoundException, SQLException {
		List<BusRoute> busRoutes = new ArrayList<>();
		BusRoute b301a = new BusRoute("301a", "301", "Eversten", 3);
		busRoutes.add(b301a);
		return busRoutes;
		// return (new DefaultBusService().getAllBusRoutes());
	}

	/**
	 * Create sensor helper list.
	 * 
	 * @param cityInfrastructure
	 * @return
	 */
	private List<SensorHelper> produceSensorHelperList(CityInfrastructureData cityInfrastructure) {
		List<SensorHelper> sensorHelperList = new ArrayList<>();
		/*
		 * HERE add sensor helpers here:
		 */
		// for(int i = 0; i < 1000; i++) {
		// sensorHelperList.add(new SensorHelper(i + 12121, new GeoLocation(12, 12), SensorType.ANEMOMETER, new
		// ArrayList<SensorInterfererType>()));
		// }
		Node randomNode = getRandomGeoLocation(cityInfrastructure,
				new Random(RANDOM_SEED_SERVICE.getSeed(Simulation.class.getName())));
		GeoLocation geoLocation = new GeoLocation(randomNode.getLatitude(), randomNode.getLongitude());

		// KITT fährt darüber
		sensorHelperList.add(new SensorHelper(getUniqueSensorID(), geoLocation, SensorType.INDUCTIONLOOP,
				new LinkedList<SensorInterfererType>(), "71400386"));
		// sensorHelperList.add(new SensorHelperPhotovoltaik(123456, geoLocation, 122345, 50, new
		// LinkedList<SensorInterfererType>(), null));

		return sensorHelperList;
	}

	/**
	 * Produce simulation event lists. E.g. for random cars.
	 * 
	 * @param cityInfrastructureData
	 * @return
	 */
	private List<SimulationEventList> produceSimulationEventLists(CityInfrastructureData cityInfrastructureData,
			StartParameter startParameter) {
		List<SimulationEventList> simulationEventLists = new ArrayList<>();

		/*
		 * add other simulation event lists here:
		 */

		List<TrafficEvent> trafficEventList = new ArrayList<>();

		/* create random cars */
		List<CreateRandomVehicleData> vehicleDataList = new LinkedList<>();

		for (int i = 0; i < carCount; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();
			sensorLists.add(new SensorHelper(getUniqueSensorID(), new GeoLocation(), SensorType.GPS_CAR,
					new ArrayList<SensorInterfererType>(), ""));
			TrafficTrip trip = new TrafficTrip(null, null, SIMULATION_START.getTimeInMillis() + i * 1000);// autos
																											// starte
																											// alle 1
																											// sek
																											// versetzt
			CreateRandomVehicleData data = new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.CAR, VehicleModelEnum.CAR_BMW_1, trip, "Car" + i));
			vehicleDataList.add(data);
		}

		trafficEventList.add(new CreateVehiclesEvent(UUID.randomUUID(), vehicleDataList));
		simulationEventLists.add(new SimulationEventList(trafficEventList, SIMULATION_START.getTimeInMillis(), UUID
				.randomUUID()));

		return simulationEventLists;
	}

	/**
	 * Produces weather event helper lists:
	 * 
	 * @return
	 */
	private List<WeatherEventHelper> produceWeatherEventHelperList() {

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
	private int getUniqueSensorID() {
		Random random = new Random(RANDOM_SEED_SERVICE.getSeed(Simulation.class.getName()));
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
		protected void onUpdate(SimulationEventList simulationEventList) {
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
		public void update(SimulationEventList simulationEventList) throws IllegalStateException {
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
