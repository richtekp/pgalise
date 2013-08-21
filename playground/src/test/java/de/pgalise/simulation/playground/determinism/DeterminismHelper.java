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
 
package de.pgalise.simulation.playground.determinism;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.naming.Context;

import org.easymock.EasyMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.Node;
import de.pgalise.simulation.shared.controller.Controller.StatusEnum;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.shared.controller.ServerConfiguration.Entity;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.event.traffic.CreateBussesEvent;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehicleData;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehiclesEvent;
import de.pgalise.simulation.shared.event.traffic.TrafficEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorHelperPhotovoltaik;
import de.pgalise.simulation.shared.sensor.SensorHelperSmartMeter;
import de.pgalise.simulation.shared.sensor.SensorHelperWindPower;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.shared.traffic.BusRoute;
import de.pgalise.simulation.shared.traffic.VehicleInformation;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;
import de.pgalise.util.GTFS.service.DefaultBusService;

/**
 * Helper class to start the simulation more than once with the same parameters
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Feb 13, 2013)
 */
public class DeterminismHelper {

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
		public void displayException(Exception exception) throws IllegalStateException {
		}

		@Override
		public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
			return false;
		}

		@Override
		protected void onInit(InitParameter param) throws InitializationException {
		}

		@Override
		protected void onReset() {
		}

		@Override
		protected void onResume() {
		}

		@Override
		protected void onStart(StartParameter param) {
		}

		@Override
		protected void onStop() {
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
	 * Logger
	 */
	public final Logger log = LoggerFactory.getLogger(DeterminismHelper.class);

	/**
	 * EJB Context
	 */
	private final Context CONTEXT;

	/**
	 * Set with all used sensor ids.
	 */
	private final Set<Integer> USED_SENSOR_IDS = new HashSet<>();

	/**
	 * Constructor
	 * 
	 * @param context
	 *            EJB context
	 */
	public DeterminismHelper(Context context) {
		CONTEXT = context;
	}

	/**
	 * Create sensor helper list with no interferers
	 * 
	 * @param cityInfrastructure
	 *            City infrastructure data
	 * @param random
	 *            Random generator
	 * @return List with sensor helpers
	 */
	public List<SensorHelper> produceStaticSensorHelperList(CityInfrastructureData cityInfrastructure, Random random) {
		List<SensorHelper> sensorHelperList = new ArrayList<>();
		List<SensorInterfererType> interfererList = new ArrayList<>();

		/* create weather sensors */

		List<SensorType> weatherSensorTypeList = new ArrayList<>();
		weatherSensorTypeList.add(SensorType.ANEMOMETER);
		weatherSensorTypeList.add(SensorType.BAROMETER);
		weatherSensorTypeList.add(SensorType.HYGROMETER);
		weatherSensorTypeList.add(SensorType.LUXMETER);
		weatherSensorTypeList.add(SensorType.PYRANOMETER);
		weatherSensorTypeList.add(SensorType.RAIN);
		weatherSensorTypeList.add(SensorType.THERMOMETER);
		weatherSensorTypeList.add(SensorType.WINDFLAG);

		for (SensorType sensorType : weatherSensorTypeList) {
			Node node = getRandomGeoLocation(cityInfrastructure, random);
			sensorHelperList.add(new SensorHelper(getUniqueSensorID(random), new GeoLocation(node.getLatitude(), node.getLongitude()), 
					sensorType, 
					interfererList,
					node.getId()));
		}

		/* create energy sensors */
		Node nodePhotovoltaik = getRandomGeoLocation(cityInfrastructure, random);
		sensorHelperList.add(new SensorHelperPhotovoltaik(getUniqueSensorID(random), new GeoLocation(nodePhotovoltaik.getLatitude(), 
				nodePhotovoltaik.getLongitude()), 20, interfererList, nodePhotovoltaik.getId()));

		Node nodeWindPower = getRandomGeoLocation(cityInfrastructure, random);
		sensorHelperList.add(new SensorHelperWindPower(getUniqueSensorID(random), 
				new GeoLocation(nodeWindPower.getLatitude(), nodeWindPower.getLongitude()), 0.5, 8.0, interfererList, nodeWindPower.getId()));

		Node nodeSmartMeter = getRandomGeoLocation(cityInfrastructure, random);
		sensorHelperList.add(new SensorHelperSmartMeter(getUniqueSensorID(random), new GeoLocation(nodeSmartMeter.getLatitude(), nodeSmartMeter.getLongitude()), 100, interfererList, nodeSmartMeter.getId()));

		/* create topo radar */
		Node topoRadarNode = getRandomGeoLocation(cityInfrastructure, random);
		sensorHelperList.add(new SensorHelper(getUniqueSensorID(random), 
				new GeoLocation(topoRadarNode.getLatitude(), 
						topoRadarNode.getLongitude()), 
						SensorType.TOPORADAR, 
						interfererList,
						topoRadarNode.getId()));

		/* create induction loop */
		Node inductionLoopNode = getRandomGeoLocation(cityInfrastructure, random);
		sensorHelperList.add(new SensorHelper(getUniqueSensorID(random),
				new GeoLocation(inductionLoopNode.getLatitude(), topoRadarNode.getLongitude()), SensorType.INDUCTIONLOOP, interfererList, inductionLoopNode.getId()));

		// Return list
		return sensorHelperList;
	}

	/**
	 * Produce simulation event lists. E.g. for random cars.
	 * 
	 * @param timestamp
	 *            Timestamp for the events
	 * @param vehicleDataList
	 *            List with CreateRandomVehicleData
	 * @return List with events
	 */
	public List<SimulationEventList> produceSimulationEventLists(long timestamp,
			List<CreateRandomVehicleData> vehicleDataList) {
		List<SimulationEventList> simulationEventLists = new ArrayList<>();

		/*
		 * Add other simulation event lists here:
		 */
		if (!vehicleDataList.isEmpty()) {
			List<TrafficEvent> trafficEventList = new ArrayList<>();
			trafficEventList.add(new CreateRandomVehiclesEvent(UUID.randomUUID(), vehicleDataList));
			simulationEventLists.add(new SimulationEventList(trafficEventList, timestamp, UUID.randomUUID()));
		}

		// Return list
		return simulationEventLists;
	}

	/**
	 * Produce a list with vehicle data
	 * 
	 * @param numberOfVehicles
	 *            Number of vehicles for each type
	 * @param cityInfrastructureData
	 *            City infrastructure data
	 * @param random
	 *            Random generator
	 * @param startTimestamp
	 *            Timestamp of the simulation start
	 * @return List with events
	 */
	public List<SimulationEventList> produceVehicleDataLists(int numberOfVehicles,
			CityInfrastructureData cityInfrastructureData, Random random, long startTimestamp) {
		List<SimulationEventList> simulationEventLists = new ArrayList<>();
		List<TrafficEvent> trafficEventList = new ArrayList<>();

		/* create random cars */
		List<CreateRandomVehicleData> vehicleDataList = new ArrayList<>();
		for (int i = 0; i < numberOfVehicles; i++) {
			UUID id = UUID.randomUUID();

			List<SensorHelper> sensorLists = new ArrayList<>();
			sensorLists.add(new SensorHelper(getUniqueSensorID(random), new GeoLocation(), SensorType.GPS_CAR,
					new ArrayList<SensorInterfererType>(), ""));

			vehicleDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.CAR, VehicleModelEnum.CAR_BMW_1, null, id.toString())));
		}

		/* create busses */
		List<BusRoute> busRoutes = new ArrayList<>();
		BusRoute b301a = new BusRoute("301a", "301", "Eversten", 3);
		busRoutes.add(b301a);

		List<CreateRandomVehicleData> busDataList = new ArrayList<>();
		int tnbt = (new DefaultBusService()).getTotalNumberOfBusTrips(busRoutes, startTimestamp);

		for (int i = 0; i < tnbt; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();

			sensorLists.add(new SensorHelper(getUniqueSensorID(random), new GeoLocation(), SensorType.GPS_BUS,
					new ArrayList<SensorInterfererType>(), ""));
			sensorLists.add(new SensorHelper(getUniqueSensorID(random), new GeoLocation(), SensorType.INFRARED,
					new ArrayList<SensorInterfererType>(), ""));

			busDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.BUS, VehicleModelEnum.BUS_CITARO, null, id.toString())));
		}
		trafficEventList.add(new CreateBussesEvent(UUID.randomUUID(), busDataList, startTimestamp, busRoutes));

		/* create random trucks */
		for (int i = 0; i < numberOfVehicles; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();

			sensorLists.add(new SensorHelper(getUniqueSensorID(random), new GeoLocation(), SensorType.GPS_TRUCK,
					new ArrayList<SensorInterfererType>(), ""));

			vehicleDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.TRUCK, VehicleModelEnum.TRUCK_COCA_COLA, null, id.toString())));
		}

		/* create random motorcycles */
		for (int i = 0; i < numberOfVehicles; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();

			sensorLists.add(new SensorHelper(getUniqueSensorID(random), new GeoLocation(), SensorType.GPS_MOTORCYCLE,
					new ArrayList<SensorInterfererType>(), ""));

			vehicleDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.TRUCK, VehicleModelEnum.MOTORCYCLE_KAWASAKI_NINJA, null, id
							.toString())));
		}

		/* create random bikes */
		for (int i = 0; i < numberOfVehicles; i++) {
			UUID id = UUID.randomUUID();
			List<SensorHelper> sensorLists = new ArrayList<>();
			sensorLists.add(new SensorHelper(getUniqueSensorID(random), new GeoLocation(), SensorType.GPS_BIKE,
					new ArrayList<SensorInterfererType>(), ""));

			vehicleDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
					VehicleTypeEnum.BIKE, VehicleModelEnum.BIKE_GAZELLE, null, id.toString())));
		}

		trafficEventList.add(new CreateRandomVehiclesEvent(UUID.randomUUID(), vehicleDataList));
		simulationEventLists.add(new SimulationEventList(trafficEventList, startTimestamp, UUID.randomUUID()));

		// Return list
		return simulationEventLists;
	}

	/**
	 * Returns a new unique sensor id.
	 * 
	 * @param random
	 *            Random Generator
	 * @return new unique sensor id
	 */
	private int getUniqueSensorID(Random random) {
		int sensorID = random.nextInt();
		while (sensorID < 1 || USED_SENSOR_IDS.contains(sensorID)) {
			sensorID = random.nextInt();
		}

		USED_SENSOR_IDS.add(sensorID);

		return sensorID;
	}

	/**
	 * Returns a random GeoLocation inside the given city infrastructure data
	 * 
	 * @return Node
	 */
	public Node getRandomGeoLocation(CityInfrastructureData city, Random random) {
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
	 * Produces weather event helper lists:
	 * 
	 * @return weather events
	 */
	public List<WeatherEventHelper> produceWeatherEventHelperList() {

		/*
		 * Add weather event helper lists here, if needed!
		 */

		return null;
	}

	/**
	 * Starts the simulation
	 * 
	 * @param initParameter
	 *            Init parameters
	 * @param startParameter
	 *            Start parameters
	 * @param simulationEvents
	 *            Simulation events
	 * @param staticSensors
	 *            SensorHelper with static sensors
	 * @throws Exception
	 */
	public void startSimulation(InitParameter initParameter, StartParameter startParameter,
			List<SimulationEventList> simulationEvents, List<SensorHelper> staticSensors) throws Exception {

		/* Create simulation controller and init/start the simulation: */
		SimulationController simulationController = (SimulationController) CONTEXT
				.lookup("java:global/de.pgalise.simulation.simulationController-impl/de.pgalise.simulation.SimulationController");

		log.debug("-------- ! INIT SIMULATION ! ---------");

		OCMock oc = new OCMock();
		simulationController._setOperationCenterController(oc);
		simulationController._getEventInitiator()._setOperationCenterController(oc);
		ControlCenterController ccMock = EasyMock.createNiceMock(ControlCenterController.class);
		simulationController._setControlCenterController(ccMock);
		simulationController._getEventInitiator()._setControlCenterController(ccMock);
		log.debug("Status: " + simulationController.getStatus().name());
		simulationController.init(initParameter);

		simulationController.createSensors(staticSensors);

		log.debug("-------- ! ADD SIMULATION EVENTS! ---------");

		for (SimulationEventList list : simulationEvents) {
			simulationController.addSimulationEventList(list);
		}

		log.debug("-------- ! START SIMULATION ! ---------");

		simulationController.start(startParameter);
		log.debug("Status: " + simulationController.getStatus().name());

		/* Wait till the end */
		simulationController._getEventInitiator()._getEventThread().join();

		// Check the state of the simulation controller
		if (!simulationController.getStatus().equals(StatusEnum.INIT)) {
			// Is pre started or stopped?
			if (simulationController.getStatus().equals(StatusEnum.STOPPED)
					|| simulationController.getStatus().equals(StatusEnum.INITIALIZED)) {
				log.debug("-------- ! RESET SIMULATION ! ---------");
				simulationController.reset();
			} else {
				// Is started?
				log.debug("-------- ! STOP SIMULATION ! ---------");
				simulationController.stop();

				log.debug("-------- ! RESET SIMULATION ! ---------");
				simulationController.reset();
			}

			/* Wait till the end */
			simulationController._getEventInitiator()._getEventThread().join();
		}
	}

	/**
	 * Reads the CSV output stream file
	 * 
	 * @param filepath
	 *            File path to the sensor output
	 * @return List with SensorTransmit
	 * @throws IOException
	 *             File can not be read
	 * @throws FileNotFoundException
	 *             File can not be found
	 */
	public List<SensorTransmit> readTestFile(String filepath) throws FileNotFoundException, IOException {
		File file = new File(filepath);
		if (!file.exists()) {
			throw new FileNotFoundException("File (" + filepath + ") not found!");
		}

		List<SensorTransmit> result = new ArrayList<>();
		try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			SensorTransmit transmit;
			while ((line = reader.readLine()) != null) {
				try {
					StringTokenizer st = new StringTokenizer(line, ";");
					transmit = new SensorTransmit();

					transmit.setTimestamp(Long.parseLong(st.nextToken()));

					transmit.setSensorId(Integer.parseInt(st.nextToken()));

					transmit.setSensortypeId(Integer.parseInt(st.nextToken()));

					transmit.setValue1(Double.parseDouble(st.nextToken()));

					transmit.setValue2(Double.parseDouble(st.nextToken()));

					transmit.setValue3((byte) Double.parseDouble(st.nextToken()));

					transmit.setValue4((short) Double.parseDouble(st.nextToken()));

					transmit.setValue5((short) Double.parseDouble(st.nextToken()));

					transmit.setValue6((short) Double.parseDouble(st.nextToken()));

					// Add to list
					result.add(transmit);
				} catch (Exception e) {
					log.debug("Can not read line: " + line);
					log.debug(e.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * Deletes the test file
	 * 
	 * @param filepath
	 *            File path
	 * @param deleted
	 *            Remove file
	 */
	public void deleteTestFile(String filepath, boolean deleted) {
		File file = new File(filepath);
		if (file.exists()) {
			log.debug("File (" + filepath + ") exists!");
			if (deleted) {
				file.delete();
				log.debug("File (" + filepath + ") is removed!");
			}
		} else {
			log.debug("File (" + filepath + ") doesn't exists!");
		}
	}

	/**
	 * Creates the server configuration. Change this for more traffic servers or for distributed servers.
	 * 
	 * @return ServerConfiguration
	 */
	public ServerConfiguration produceServerConfiguration() {
		ServerConfiguration conf = new ServerConfiguration();
		List<Entity> entities = new ArrayList<>();
		entities.add(new Entity(ServiceDictionary.RANDOM_SEED_SERVICE));
		entities.add(new Entity(ServiceDictionary.WEATHER_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.ENERGY_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.FRONT_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.STATIC_SENSOR_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.TRAFFIC_CONTROLLER));
		entities.add(new Entity(ServiceDictionary.TRAFFIC_SERVER));
		entities.add(new Entity(ServiceDictionary.SIMULATION_CONTROLLER));
		conf.getConfiguration().put("127.0.0.1:8081", entities);

		return conf;
	}

}
