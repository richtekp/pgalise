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
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.energy.internal.CSVEnergyConsumptionManager;
import de.pgalise.simulation.energy.internal.profile.CSVProfileLoader;
import de.pgalise.simulation.energy.profile.EnergyProfileLoader;
import de.pgalise.simulation.service.GPSMapper;
import de.pgalise.simulation.service.internal.DefaultGPSMapper;
import de.pgalise.simulation.shared.city.Building;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.geolocation.GeoLocation;
import javax.vecmath.Vector2d;

/**
 * Tests the public methods of the CSVEnergyConsumptionManager.
 * 
 * @author Andreas Rehfeldt
 * @author Timo
 * @version 1.0 (Oct 28, 2012)
 */
public class CSVEnergyConsumptionManagerTest {

	/**
	 * End timestamp
	 */
	private static long endTimestamp;

	/**
	 * Start timestamp
	 */
	private static long startTimestamp;

	/**
	 * GPS mapper
	 */
	private static final GPSMapper gpsMapper = new DefaultGPSMapper();

	/**
	 * Test location as GeoLocation
	 */
	private static final GeoLocation testLocationAsGL = new GeoLocation(53.136765, 8.216524);

	/**
	 * Test location as Vector2d
	 */
	private static final Vector2d testLocation = CSVEnergyConsumptionManagerTest.gpsMapper
			.convertToVector(CSVEnergyConsumptionManagerTest.testLocationAsGL);
	
	/**
	 * The used energy profile loader.
	 */
	private static final EnergyProfileLoader energyProfileLoader = new CSVProfileLoader();

	/**
	 * Test class
	 */
	public static CSVEnergyConsumptionManager testclass;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Map<EnergyProfileEnum, List<Building>> map = new HashMap<EnergyProfileEnum, List<Building>>();
		List<Building> buildingList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			buildingList.add(new Building(new GeoLocation(), new GeoLocation(), new GeoLocation(53.136765, 8.216524)));
		}
		map.put(EnergyProfileEnum.HOUSEHOLD, buildingList);
		CityInfrastructureData citydata = EasyMock.createNiceMock(CityInfrastructureData.class);
		EasyMock.expect(citydata.getBuildings(CSVEnergyConsumptionManagerTest.testLocationAsGL, 5)).andStubReturn(map);
		EasyMock.replay(citydata);

		CSVEnergyConsumptionManagerTest.testclass = new CSVEnergyConsumptionManager();
		testclass.setLoader(energyProfileLoader);
		
		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 1, 1, 20, 0, 0);
		CSVEnergyConsumptionManagerTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2012, 1, 3, 20, 14, 0);
		CSVEnergyConsumptionManagerTest.endTimestamp = cal.getTimeInMillis();
	}

	@Test
	public void testGetEnergyConsumptionInKWhTimestamp() {
		/*
		 * Test preparations
		 */
		if (CSVEnergyConsumptionManagerTest.
				testclass.
				getProfiles().
				get(EnergyProfileEnum.HOUSEHOLD) == null) {
			CSVEnergyConsumptionManagerTest.testclass.init(CSVEnergyConsumptionManagerTest.startTimestamp,
					CSVEnergyConsumptionManagerTest.endTimestamp, null);
		}

		Calendar cal = new GregorianCalendar();
		cal.set(2012, 1, 1, 20, 14, 0);
		long testTime = cal.getTimeInMillis();

		/*
		 * TEST RUN
		 */
		double actValue = CSVEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(testTime,
				EnergyProfileEnum.HOUSEHOLD, CSVEnergyConsumptionManagerTest.testLocation);

		/*
		 * Test 1 : Test the testTime
		 */
		Assert.assertEquals(0.05381885854844445, actValue, 0.001);
	}

	@Test
	public void testLoadEnergyProfiles() {
		/*
		 * TEST RUN
		 */
		CSVEnergyConsumptionManagerTest.testclass.init(CSVEnergyConsumptionManagerTest.startTimestamp,
				CSVEnergyConsumptionManagerTest.endTimestamp, null);

		/*
		 * Test 1 : load all profiles correctly
		 */
		for (EnergyProfileEnum profileEnum : EnergyProfileEnum.values()) {
			Assert.assertNotNull(CSVEnergyConsumptionManagerTest.testclass.getProfiles().get(profileEnum));
		}
	}

}
