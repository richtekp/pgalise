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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.ejb.embeddable.EJBContainer;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.event.traffic.CreateBussesEvent;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehiclesEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.traffic.server.route.RandomVehicleTripGenerator;
import de.pgalise.util.GTFS.service.DefaultBusService;
import de.pgalise.util.cityinfrastructure.impl.DefaultBuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.impl.OSMCityInfrastructureData;

/**
 * This test checks if the simulation gives with the same start parameters the same output variables.
 * 
 * @author Andreas
 * @version 1.0
 */
@Ignore
public class SimulationDeterminismTest {

	/*
	 * Settings:
	 */

	/**
	 * Start of the simulation
	 */
	private static final long SIMULATION_START = new GregorianCalendar(2011, 8, 11, 8, 0).getTimeInMillis();

	/**
	 * End of the simulation
	 */
	private static final long SIMULATION_END = new GregorianCalendar(2011, 8, 11, 8, 29).getTimeInMillis();

	/**
	 * Update interval of the simulation
	 */
	private static final int SIMULATION_INTERVAL_IN_MILLIES = 1000 * 10;

	/**
	 * Test file 1
	 */
	private static final String OUTPUT_FILE_1 = "./testoutput_1.csv";

	/**
	 * Test file 2
	 */
	private static final String OUTPUT_FILE_2 = "./testoutput_2.csv";

	/**
	 * How many random vehicles for each type
	 */
	private static final int RANDOM_VEHICLE_TYPE_NUMBER = 5;

	/**
	 * Logger
	 */
	public static final Logger log = LoggerFactory.getLogger(SimulationDeterminismTest.class);

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
	 * Random seed service. Change the timestamp for different seeds.
	 */
	private static final RandomSeedService RANDOM_SEED_SERVICE = new DefaultRandomSeedService();

	/**
	 * Take normal weather information
	 */
	private static final boolean TAKE_NORMAL_WEATHER = true;

	/**
	 * Delete files after tests
	 */
	private static final boolean DELETE_FILES = true;

	/**
	 * Start parameter 1
	 */
	private static InitParameter INIT_PARAMETER_1;

	/**
	 * Start parameter 2
	 */
	private static InitParameter INIT_PARAMETER_2;

	/**
	 * Start parameter
	 */
	private static StartParameter START_PARAMETER;

	/**
	 * The simulation will wait for time after every step.
	 */
	private static final int SIMULATION_CLOCKTIME_INTERVAL_IN_MILLIES = 1000;

	/**
	 * Simulation events
	 */
	private static List<SimulationEventList> SIMULATION_EVENTS_1;

	/**
	 * Simulation events
	 */
	private static List<SimulationEventList> SIMULATION_EVENTS_2;

	/**
	 * Determinism helper for the simulation
	 */
	private static DeterminismHelper DETERMINISMHELPER;

	/**
	 * List of static sensor helpers
	 */
	private static List<SensorHelper> STATIC_SENSORS;

	/**
	 * URL for the operation servlet:
	 */
	private static final String OC_SERVLET_URL = "http://localhost:8080/operationCenter/OCSimulationServlet";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.getProperties().put("simulation.configuration.filepath",
				System.getProperties().get("user.dir") + "/src/main/resources/simulation.conf");

		Properties prop = new Properties();
		prop.load(Controller.class.getResourceAsStream("/jndi.properties"));
		EJBContainer container = EJBContainer.createEJBContainer(prop);

		/* Create helper */
		DETERMINISMHELPER = new DeterminismHelper(container.getContext());

		/* init random seed service: */
		RANDOM_SEED_SERVICE.init(SIMULATION_START);

		/* Random */
		Random random = new Random(RANDOM_SEED_SERVICE.getSeed(SimulationDeterminismTest.class.getName()));

		/* read Server config */
		ServerConfiguration serverConf = DETERMINISMHELPER.produceServerConfiguration();

		/* create city infrastructure */
		CityInfrastructureData cityInfrastructure = new OSMCityInfrastructureData(INPUTSTREAM_OSM,
				INPUTSTREAM_BUSSTOPS, new DefaultBuildingEnergyProfileStrategy());

		City city = new City();

		/* Create simulation events */

		List<WeatherEventHelper> weatherEvents = DETERMINISMHELPER.produceWeatherEventHelperList();
		STATIC_SENSORS = DETERMINISMHELPER.produceStaticSensorHelperList(cityInfrastructure, random);
		List<SimulationEventList> vehicleData = DETERMINISMHELPER.produceVehicleDataLists(RANDOM_VEHICLE_TYPE_NUMBER,
				cityInfrastructure, random, SIMULATION_START);

		SIMULATION_EVENTS_1 = vehicleData;
		SIMULATION_EVENTS_2 = vehicleData;

		/* Create init paramters: */
		INIT_PARAMETER_1 = new InitParameter(cityInfrastructure, serverConf, SIMULATION_START, SIMULATION_END,
				SIMULATION_INTERVAL_IN_MILLIES, SIMULATION_CLOCKTIME_INTERVAL_IN_MILLIES, OC_SERVLET_URL, "",
				new TrafficFuzzyData(1, 0.9, 1), cityInfrastructure.getBoundary());

		INIT_PARAMETER_2 = new InitParameter(cityInfrastructure, serverConf, SIMULATION_START, SIMULATION_END,
				SIMULATION_INTERVAL_IN_MILLIES, SIMULATION_CLOCKTIME_INTERVAL_IN_MILLIES, OC_SERVLET_URL, "",
				new TrafficFuzzyData(1, 0.9, 1), cityInfrastructure.getBoundary());

		/* Create start parameter: */
		START_PARAMETER = new StartParameter(city, TAKE_NORMAL_WEATHER, weatherEvents,
				new DefaultBusService().getAllBusRoutes());
	}

	@After
	public void tearDown() throws Throwable {
		// Delete all files
		DETERMINISMHELPER.deleteTestFile(OUTPUT_FILE_1, DELETE_FILES);
		DETERMINISMHELPER.deleteTestFile(OUTPUT_FILE_2, DELETE_FILES);
	}

	@Test
	public void testDeterminism() throws Throwable {
		// Delete old files
		DETERMINISMHELPER.deleteTestFile(OUTPUT_FILE_1, true);
		DETERMINISMHELPER.deleteTestFile(OUTPUT_FILE_2, true);

		/*
		 * start the simulation 1
		 */
		log.debug("-------- ! START SIMULATION 1 ! ---------");
		long simulation1Duration = System.currentTimeMillis();
		DETERMINISMHELPER.startSimulation(INIT_PARAMETER_1, START_PARAMETER, SIMULATION_EVENTS_1, STATIC_SENSORS);
		simulation1Duration = System.currentTimeMillis() - simulation1Duration;
		log.debug("-------- ! END SIMULATION 1 ! ---------");
		String seed1 = "" + RANDOM_SEED_SERVICE.getSeed(RandomVehicleTripGenerator.class.getName());

		/*
		 * start the simulation 2
		 */
		log.debug("-------- ! START SIMULATION 2 ! ---------");
		long simulation2Duration = System.currentTimeMillis();
		DETERMINISMHELPER.startSimulation(INIT_PARAMETER_2, START_PARAMETER, SIMULATION_EVENTS_2, STATIC_SENSORS);
		simulation2Duration = System.currentTimeMillis() - simulation2Duration;
		log.debug("-------- ! END SIMULATION 2 ! ---------");
		String seed2 = "" + RANDOM_SEED_SERVICE.getSeed(RandomVehicleTripGenerator.class.getName());

		/*
		 * Evaluate the result
		 */

		log.debug("-------- ! EVALUATE ! ---------");

		log.debug("RandomSeed: " + seed1 + " - " + seed2);

		// Read file 1
		log.debug("-------- ! READ FILE 1 ! ---------");
		List<SensorTransmit> list1 = DETERMINISMHELPER.readTestFile(OUTPUT_FILE_1);

		// Read file 2
		log.debug("-------- ! READ FILE 2 ! ---------");
		List<SensorTransmit> list2 = DETERMINISMHELPER.readTestFile(OUTPUT_FILE_2);

		// Calculate sizes
		int transmits1 = list1.size();
		int transmits2 = list2.size();

		int vehicles1 = calculateTrafficEvents(SIMULATION_EVENTS_1);
		int vehicles2 = calculateTrafficEvents(SIMULATION_EVENTS_2);

		// Write info file
		log.debug("-------- ! WRITE SCALE TEST ! ---------");
		ScaleTestHelper scale = new ScaleTestHelper();
		scale.benchmark(START_PARAMETER, STATIC_SENSORS.size(), INIT_PARAMETER_1, simulation1Duration, vehicles1,
				transmits1, INIT_PARAMETER_2, simulation2Duration, vehicles2, transmits2);

		// Test the files
		log.debug("-------- ! TEST ! ---------");

		// test the length
		assertEquals(transmits1, transmits2, 0);

		// test the content
		for (SensorTransmit sensorTransmit : list1) {
			if (list2.contains(sensorTransmit)) {
				list2.remove(sensorTransmit);
			} else {
				log.warn(sensorTransmit.toString());
				assertTrue(false);
			}
		}
	}

	/**
	 * Calculate traffic events
	 * 
	 * @param simulationEvents
	 *            Simulations Event
	 * @return Number of traffic events
	 */
	private int calculateTrafficEvents(List<SimulationEventList> simulationEvents) {
		int size = 0;

		for (SimulationEventList simulationEventList : simulationEvents) {
			for (SimulationEvent simevent : simulationEventList.getEventList()) {
				if (simevent instanceof CreateBussesEvent) {
					CreateBussesEvent busEvent = (CreateBussesEvent) simevent;
					size += busEvent.getCreateRandomVehicleDataList().size();
				} else if (simevent instanceof CreateRandomVehiclesEvent) {
					CreateRandomVehiclesEvent vehicleEvent = (CreateRandomVehiclesEvent) simevent;
					size += vehicleEvent.getCreateRandomVehicleDataList().size();
				}
			}
		}
		return size;
	}
}
