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
 
package de.pgalise.simulation.controlCenter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.controlCenter.model.RandomVehicleBundle;
import de.pgalise.simulation.controlCenter.internal.util.service.DefaultCreateRandomVehicleService;
import de.pgalise.simulation.controlCenter.internal.util.service.SensorInterfererService;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 * J-Unit tests for {@link DefaultCreateRandomVehicleService}.<br />
 * <br />
 * Tests if the class can create the right amount of random dynamic sensors and if it considers the already used IDs.
 * 
 * @author Timo
 */
public class DefaultRandomDynamicSensorServiceTest {
	/**
	 * Mock for this test.
	 * 
	 * @author Timo
	 */
	private static class SensorInterfererServiceMock implements SensorInterfererService {
		@Override
		public List<SensorInterfererType> getSensorInterferes(SensorType sensorType, boolean isWithSensorInterferer) {
			return new LinkedList<>();
		}
	}
	private static final double ASSERT_EQUALS_DELTA = 0.09;
	private static final int RANDOM_CAR_AMOUNT = 100;
	private static final double GPS_CAR_RATIO = 0.9;
	private static final int RANDOM_BIKE_AMOUNT = 233;
	private static final double GPS_BIKE_RATIO = 0.8;
	private static final int RANDOM_TRUCK_AMOUNT = 55;
	private static final double GPS_TRUCK_RATIO = 1.0;
	private static final int RANDOM_MOTORCYCLE_AMOUNT = 99;
	private static final double GPS_MOTORCYCLE_RATIO = 0.5;
	private static Set<Sensor<?>> usedSensorIDs = new HashSet<Sensor<?>>(1);
	private static Set<UUID> usedUUIDs = new HashSet<UUID>(1);
	private static int carAmount = 0;
	private static int carsWithGPSAmount = 0;
	private static int truckAmount = 0;
	private static int trucksWithGPSAmount = 0;
	private static int bikeAmount = 0;
	private static int bikesWithGPSAmount = 0;
	private static int motorcycleAmount = 0;
	private static int motorcyclesWithGPSAmount = 0;
	private static Set<Sensor<?>> newSensorIDs = new HashSet<>();

	private static Set<UUID> newUUIDs = new HashSet<>();

	@BeforeClass
	public static void setUp() {
		TrafficServerLocal trafficServerLocal = EasyMock.createNiceMock(
			TrafficServerLocal.class);
		DefaultCreateRandomVehicleService testClass = new DefaultCreateRandomVehicleService(
				new SensorInterfererServiceMock(), trafficServerLocal);

		/* Create random used sensor ids: */
//		DefaultRandomDynamicSensorServiceTest.usedSensorIDs = new HashSet<>();
//		Random random = new Random();
//		while (DefaultRandomDynamicSensorServiceTest.usedSensorIDs.size() < 500) {
//			DefaultRandomDynamicSensorServiceTest.usedSensorIDs.add(Math.abs(random.nextInt()));
//		}

		/* Create random used UUIDs: */
		DefaultRandomDynamicSensorServiceTest.usedUUIDs = new HashSet<>();
		while (DefaultRandomDynamicSensorServiceTest.usedUUIDs.size() < 500) {
			DefaultRandomDynamicSensorServiceTest.usedUUIDs.add(UUID.randomUUID());
		}

		RandomVehicleBundle testData = new RandomVehicleBundle(
			DefaultRandomDynamicSensorServiceTest.RANDOM_CAR_AMOUNT,
			DefaultRandomDynamicSensorServiceTest.GPS_CAR_RATIO,
			DefaultRandomDynamicSensorServiceTest.RANDOM_BIKE_AMOUNT,
			DefaultRandomDynamicSensorServiceTest.GPS_BIKE_RATIO,
			DefaultRandomDynamicSensorServiceTest.RANDOM_TRUCK_AMOUNT,
			DefaultRandomDynamicSensorServiceTest.GPS_TRUCK_RATIO,
			DefaultRandomDynamicSensorServiceTest.RANDOM_MOTORCYCLE_AMOUNT,
			DefaultRandomDynamicSensorServiceTest.GPS_MOTORCYCLE_RATIO,
			DefaultRandomDynamicSensorServiceTest.usedSensorIDs, 
			DefaultRandomDynamicSensorServiceTest.usedUUIDs
		);

		RandomSeedService randomSeedService = EasyMock.createNiceMock(RandomSeedService.class);

		TrafficEvent result = testClass.createRandomDynamicSensors(testData, randomSeedService, true);

		/* Count vehicles and their gps ratio: */
		if (result.getType() == TrafficEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT) {
			for (CreateRandomVehicleData createRandomVehicleData : ((CreateRandomVehiclesEvent<?>) result)
					.getCreateRandomVehicleDataList()) {
				switch (createRandomVehicleData.getVehicleInformation().getVehicleType()) {
					case CAR:
						DefaultRandomDynamicSensorServiceTest.carAmount++;
						for (SensorHelper sensorHelper : createRandomVehicleData.getSensorHelpers()) {
							if (sensorHelper.getSensorType() == SensorTypeEnum.GPS_CAR) {
								DefaultRandomDynamicSensorServiceTest.carsWithGPSAmount++;
								DefaultRandomDynamicSensorServiceTest.newSensorIDs.add(sensorHelper.getSensorID());
								break;
							}
						}
						break;
					case BIKE:
						DefaultRandomDynamicSensorServiceTest.bikeAmount++;
						for (SensorHelper sensorHelper : createRandomVehicleData.getSensorHelpers()) {
							if (sensorHelper.getSensorType() == SensorTypeEnum.GPS_BIKE) {
								DefaultRandomDynamicSensorServiceTest.bikesWithGPSAmount++;
								DefaultRandomDynamicSensorServiceTest.newSensorIDs.add(sensorHelper.getSensorID());
								break;
							}
						}
						break;
					case MOTORCYCLE:
						DefaultRandomDynamicSensorServiceTest.motorcycleAmount++;
						for (SensorHelper sensorHelper : createRandomVehicleData.getSensorHelpers()) {
							if (sensorHelper.getSensorType() == SensorTypeEnum.GPS_MOTORCYCLE) {
								DefaultRandomDynamicSensorServiceTest.motorcyclesWithGPSAmount++;
								DefaultRandomDynamicSensorServiceTest.newSensorIDs.add(sensorHelper.getSensorID());
								break;
							}
						}
						break;
					case TRUCK:
						DefaultRandomDynamicSensorServiceTest.truckAmount++;
						for (SensorHelper sensorHelper : createRandomVehicleData.getSensorHelpers()) {
							if (sensorHelper.getSensorType() == SensorTypeEnum.GPS_TRUCK) {
								DefaultRandomDynamicSensorServiceTest.trucksWithGPSAmount++;
								DefaultRandomDynamicSensorServiceTest.newSensorIDs.add(sensorHelper.getSensorID());
								break;
							}
						}
						break;
					default:
						Assert.assertTrue(false);
						break;
				}
			}
		}
	}

	/**
	 * Tests if enough GPS sensors are generated.
	 */
	@Test
	public void testGPSGeneration() {
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.GPS_CAR_RATIO,
				(double) DefaultRandomDynamicSensorServiceTest.carsWithGPSAmount
						/ (double) DefaultRandomDynamicSensorServiceTest.carAmount,
				DefaultRandomDynamicSensorServiceTest.ASSERT_EQUALS_DELTA);
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.GPS_BIKE_RATIO,
				(double) DefaultRandomDynamicSensorServiceTest.bikesWithGPSAmount
						/ (double) DefaultRandomDynamicSensorServiceTest.bikeAmount,
				DefaultRandomDynamicSensorServiceTest.ASSERT_EQUALS_DELTA);
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.GPS_TRUCK_RATIO,
				(double) DefaultRandomDynamicSensorServiceTest.trucksWithGPSAmount
						/ (double) DefaultRandomDynamicSensorServiceTest.truckAmount,
				DefaultRandomDynamicSensorServiceTest.ASSERT_EQUALS_DELTA);
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.GPS_MOTORCYCLE_RATIO,
				(double) DefaultRandomDynamicSensorServiceTest.motorcyclesWithGPSAmount
						/ (double) DefaultRandomDynamicSensorServiceTest.carAmount,
				DefaultRandomDynamicSensorServiceTest.ASSERT_EQUALS_DELTA);
	}

	/**
	 * Tests if enough unique ids are generated.
	 */
	@Test
	public void testIDs() {
		Set<Sensor<?>> allIDs = new HashSet<>();
		allIDs.addAll(DefaultRandomDynamicSensorServiceTest.newSensorIDs);
		allIDs.addAll(DefaultRandomDynamicSensorServiceTest.usedSensorIDs);
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.usedSensorIDs.size()
				+ DefaultRandomDynamicSensorServiceTest.newSensorIDs.size(), allIDs.size());

		Set<UUID> allUUIDs = new HashSet<>();
		allUUIDs.addAll(DefaultRandomDynamicSensorServiceTest.newUUIDs);
		allUUIDs.addAll(DefaultRandomDynamicSensorServiceTest.usedUUIDs);
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.usedUUIDs.size()
				+ DefaultRandomDynamicSensorServiceTest.newUUIDs.size(), allUUIDs.size());
	}

	/**
	 * Tests if enough vehicles are generated.
	 */
	@Test
	public void testVehicleGeneration() {
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.RANDOM_BIKE_AMOUNT,
				DefaultRandomDynamicSensorServiceTest.bikeAmount);
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.RANDOM_CAR_AMOUNT,
				DefaultRandomDynamicSensorServiceTest.carAmount);
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.RANDOM_MOTORCYCLE_AMOUNT,
				DefaultRandomDynamicSensorServiceTest.motorcycleAmount);
		Assert.assertEquals(DefaultRandomDynamicSensorServiceTest.RANDOM_TRUCK_AMOUNT,
				DefaultRandomDynamicSensorServiceTest.truckAmount);
	}
}
