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
 
package de.pgalise.simulation.energy.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.energy.EnergyConsumptionManager;
import de.pgalise.simulation.energy.EnergyEventStrategy;
import de.pgalise.simulation.energy.internal.DefaultEnergyController;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.city.Boundary;
import de.pgalise.simulation.shared.city.Building;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.service.StatusEnum;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import de.pgalise.simulation.shared.exception.InitializationException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.simulation.shared.traffic.BusRoute;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * JUnit tests for {@link DefaultEnergyController}
 * 
 * @author Timo
 * @author Andreas
 */
public class DefaultEnergyControllerTest {

	/**
	 * Test City
	 */
	private static City city;

	/**
	 * End of the simulation
	 */
	private static long endTime;

	/**
	 * Start of the simulation
	 */
	private static long startTime;

	/**
	 * Test class
	 */
	private static DefaultEnergyController testClass;

	/**
	 * Weather Controller
	 */
	private static WeatherController weather;

	/**
	 * Geolocation
	 */
	private static final Coordinate testLocation = new Coordinate(53.136765, 8.216524);

	/**
	 * Init parameters
	 */
	private static InitParameter initParameter;

	/**
	 * Start parameters
	 */
	private static StartParameter startParameter;

	/**
	 * Service dictionary to e.g. find weather controller.
	 */
	private static ServiceDictionary serviceDictionary;

	/**
	 * Traffic information
	 */
	private static CityInfrastructureData information;
	
	/**
	 * The used energy consumption manager
	 */
	private static EnergyConsumptionManager energyConsumptionManager;
	
	/**
	 * The used energy event strategy
	 */
	private static EnergyEventStrategy energyEventStrategy;
	
	/**
	 * Test time
	 */
	private static long testTime;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Calendar cal = new GregorianCalendar();

		
		// city
		Polygon referenceArea = GeotoolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(new Coordinate[] {
			new Coordinate(1,
			1),
			new Coordinate(1,
			2),
			new Coordinate(2,
			2),
			new Coordinate(2,
			1),
			new Coordinate(1,
			1)
		});
		City city = new City("Berlin",
			3375222,
			80,
			true,
			true,
			referenceArea);

		// City information
		Map<EnergyProfileEnum, List<Building>> map = new HashMap<>();
		List<Building> buildingList = new ArrayList<>();
		map.put(EnergyProfileEnum.HOUSEHOLD, buildingList);
		for (int i = 0; i < 100; i++) {
			buildingList.add(new Building(new Coordinate(), new Coordinate(), new Coordinate(53.136765, 8.216524)));
		}

		DefaultEnergyControllerTest.information = EasyMock.createNiceMock(CityInfrastructureData.class);
		EasyMock.expect(
				DefaultEnergyControllerTest.information.getBuildings(DefaultEnergyControllerTest.testLocation, 3))
				.andStubReturn(map);
		EasyMock.replay(DefaultEnergyControllerTest.information);

		// Start
		cal.set(2012, 1, 1, 0, 0, 0);
		DefaultEnergyControllerTest.startTime = cal.getTimeInMillis();

		// End
		cal.set(2012, 1, 5, 0, 0, 0);
		DefaultEnergyControllerTest.endTime = cal.getTimeInMillis();

		// Interval
		long interval = 1000;
		
		// Test time
		cal.set(2012, 1, 2, 11, 35, 0);
		testTime = cal.getTimeInMillis();

		// Weather Controller:
		DefaultEnergyControllerTest.weather = EasyMock.createNiceMock(WeatherController.class);
		EasyMock.replay(DefaultEnergyControllerTest.weather);

		// Service Dictionary:
		DefaultEnergyControllerTest.serviceDictionary = EasyMock.createNiceMock(ServiceDictionary.class);
		EasyMock.expect(DefaultEnergyControllerTest.serviceDictionary.getController(WeatherController.class))
				.andStubReturn(DefaultEnergyControllerTest.weather);
		EasyMock.replay(DefaultEnergyControllerTest.serviceDictionary);

		DefaultEnergyControllerTest.initParameter = new InitParameter(DefaultEnergyControllerTest.information,
				EasyMock.createNiceMock(ServerConfiguration.class), DefaultEnergyControllerTest.startTime,
				DefaultEnergyControllerTest.endTime, interval, interval, "http://localhost:8080/operationCenter",
				"", null,
				new Boundary(new Coordinate(), new Coordinate()));

		city = new City("Berlin",
			3375222,
			80,
			true,
			true,
			referenceArea);
		DefaultEnergyControllerTest.startParameter = new StartParameter(city,
				true, new ArrayList<WeatherEventHelper>(), new LinkedList<BusRoute>());
		
		// EnergyEventStrategy
		energyEventStrategy = EasyMock.createNiceMock(EnergyEventStrategy.class);

		// EnergyConsumptionManager
		energyConsumptionManager = EasyMock.createNiceMock(EnergyConsumptionManager.class);
		EasyMock.expect(energyConsumptionManager.getEnergyConsumptionInKWh(testTime, EnergyProfileEnum.HOUSEHOLD, testLocation)).andStubReturn(1.0);
		EasyMock.replay(energyConsumptionManager);

		// Create class
		DefaultEnergyControllerTest.testClass = new DefaultEnergyController();
		DefaultEnergyControllerTest.testClass.setServiceDictionary(DefaultEnergyControllerTest.serviceDictionary);
		DefaultEnergyControllerTest.testClass.setEnergyConsumptionManager(energyConsumptionManager);
		DefaultEnergyControllerTest.testClass.setEnergyEventStrategy(energyEventStrategy);
	}

	@Test
	public void testController() throws IllegalStateException, InitializationException {

		/*
		 * Init the controller
		 */
		DefaultEnergyControllerTest.testClass.init(DefaultEnergyControllerTest.initParameter);
		Assert.assertEquals(StatusEnum.INITIALIZED, testClass.getStatus());

		/*
		 * Start the controller
		 */
		DefaultEnergyControllerTest.testClass.start(DefaultEnergyControllerTest.startParameter);
		Assert.assertEquals(StatusEnum.STARTED, testClass.getStatus());


		/*
		 * RUN TEST
		 */

		// Get value
		double value = DefaultEnergyControllerTest.testClass.getEnergyConsumptionInKWh(testTime,
				DefaultEnergyControllerTest.testLocation, 3);

		Assert.assertEquals(100.0, value, 0.5);

		/*
		 * Stop the controller
		 */
		DefaultEnergyControllerTest.testClass.stop();
		Assert.assertEquals(StatusEnum.STOPPED, testClass.getStatus());

		/*
		 * Reset the controller
		 */
		DefaultEnergyControllerTest.testClass.reset();
		Assert.assertEquals(StatusEnum.INIT, testClass.getStatus());
	}
}
