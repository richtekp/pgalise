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

import de.pgalise.simulation.controlCenter.internal.util.service.CreateRandomVehicleService;
import de.pgalise.simulation.controlCenter.internal.util.service.SensorInterfererService;
import de.pgalise.simulation.controlCenter.model.DefaultCreateRandomVehicleService;
import de.pgalise.simulation.controlCenter.model.RandomVehicleBundle;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.testutils.TestUtils;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * J-Unit tests for {@link DefaultCreateRandomVehicleService}.<br />
 * <br />
 * Tests if the class can create the right amount of random dynamic sensors and
 * if it considers the already used IDs.
 *
 * @author Timo
 */
@LocalClient
@ManagedBean
public class DefaultCreateRandomVehicleServiceTest
{
	private static EJBContainer eJBContainer;
	@EJB
	private RandomSeedService randomSeedService;
	@EJB
	private CreateRandomVehicleService testClass;

	public DefaultCreateRandomVehicleServiceTest() throws NamingException {

		eJBContainer.getContext().bind("inject",
			this);
		
		TrafficServerLocal<?> trafficServerLocal = EasyMock.createNiceMock(
						TrafficServerLocal.class);
//		testClass = new DefaultCreateRandomVehicleService(
//						new SensorInterfererServiceMock(), trafficServerLocal);

		/* Create random used sensor ids: */
//		DefaultCreateRandomVehicleServiceTest.usedSensorIDs = new HashSet<>();
//		Random random = new Random();
//		while (DefaultCreateRandomVehicleServiceTest.usedSensorIDs.size() < 500) {
//			DefaultCreateRandomVehicleServiceTest.usedSensorIDs.add(Math.abs(random.nextInt()));
//		}

		/* Create random used UUIDs: */
		DefaultCreateRandomVehicleServiceTest.usedUUIDs = new HashSet<>();
		while (DefaultCreateRandomVehicleServiceTest.usedUUIDs.size() < 500) {
			DefaultCreateRandomVehicleServiceTest.usedUUIDs.add(UUID.randomUUID());
		}

		RandomVehicleBundle testData = new RandomVehicleBundle(
						DefaultCreateRandomVehicleServiceTest.RANDOM_CAR_AMOUNT,
						DefaultCreateRandomVehicleServiceTest.GPS_CAR_RATIO,
						DefaultCreateRandomVehicleServiceTest.RANDOM_BIKE_AMOUNT,
						DefaultCreateRandomVehicleServiceTest.GPS_BIKE_RATIO,
						DefaultCreateRandomVehicleServiceTest.RANDOM_TRUCK_AMOUNT,
						DefaultCreateRandomVehicleServiceTest.GPS_TRUCK_RATIO,
						DefaultCreateRandomVehicleServiceTest.RANDOM_MOTORCYCLE_AMOUNT,
						DefaultCreateRandomVehicleServiceTest.GPS_MOTORCYCLE_RATIO
		);
		
		TrafficEvent<?> result = testClass.createRandomDynamicSensors(testData,
						randomSeedService, true);

		/* Count vehicles and their gps ratio: */
		if (result.getType() == TrafficEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT) {
			for (CreateRandomVehicleData createRandomVehicleData
									 : ((CreateRandomVehiclesEvent<?>) result)
							.getCreateRandomVehicleDataList()) {
				switch (createRandomVehicleData.getVehicleInformation().getVehicleType()) {
					case CAR:
						DefaultCreateRandomVehicleServiceTest.carAmount++;
						for (Sensor<?, ?> sensorHelper : createRandomVehicleData.
										getSensorHelpers()) {
							if (sensorHelper instanceof GpsSensor) {
								DefaultCreateRandomVehicleServiceTest.carsWithGPSAmount++;
								DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
												sensorHelper);
								break;
							}
						}
						break;
					case BIKE:
						DefaultCreateRandomVehicleServiceTest.bikeAmount++;
						for (Sensor<?, ?> sensorHelper : createRandomVehicleData.
										getSensorHelpers()) {
							if (sensorHelper instanceof GpsSensor) {
								DefaultCreateRandomVehicleServiceTest.bikesWithGPSAmount++;
								DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
												sensorHelper);
								break;
							}
						}
						break;
					case MOTORCYCLE:
						DefaultCreateRandomVehicleServiceTest.motorcycleAmount++;
						for (Sensor<?, ?> sensorHelper : createRandomVehicleData.
										getSensorHelpers()) {
							if (sensorHelper instanceof GpsSensor) {
								DefaultCreateRandomVehicleServiceTest.motorcyclesWithGPSAmount++;
								DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
												sensorHelper);
								break;
							}
						}
						break;
					case TRUCK:
						DefaultCreateRandomVehicleServiceTest.truckAmount++;
						for (Sensor<?, ?> sensorHelper : createRandomVehicleData.
										getSensorHelpers()) {
							if (sensorHelper instanceof GpsSensor) {
								DefaultCreateRandomVehicleServiceTest.trucksWithGPSAmount++;
								DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
												sensorHelper);
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
	
	@Before
	public void setUp() {
	}

	/**
	 * Mock for this test.
	 *
	 * @author Timo
	 */
	private static class SensorInterfererServiceMock implements
					SensorInterfererService
	{

		@Override
		public List<SensorInterfererType> getSensorInterfererTypes(SensorType sensorType,
						boolean isWithSensorInterferer) {
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
	private static final Set<Sensor<?, ?>> usedSensorIDs = new HashSet<>(1);
	private static Set<UUID> usedUUIDs = new HashSet<>(1);
	private static int carAmount = 0;
	private static int carsWithGPSAmount = 0;
	private static int truckAmount = 0;
	private static int trucksWithGPSAmount = 0;
	private static int bikeAmount = 0;
	private static int bikesWithGPSAmount = 0;
	private static int motorcycleAmount = 0;
	private static int motorcyclesWithGPSAmount = 0;
	private static final Set<Sensor<?, ?>> newSensorIDs = new HashSet<>();

	private static final Set<UUID> newUUIDs = new HashSet<>();

	@BeforeClass
	public static void setUpClass() {
		
		eJBContainer = TestUtils.getContainer();
	}

	/**
	 * Tests if enough GPS sensors are generated.
	 */
	@Test
	public void testGPSGeneration() {
		Assert.assertEquals(DefaultCreateRandomVehicleServiceTest.GPS_CAR_RATIO,
						(double) DefaultCreateRandomVehicleServiceTest.carsWithGPSAmount
										/ (double) DefaultCreateRandomVehicleServiceTest.carAmount,
						DefaultCreateRandomVehicleServiceTest.ASSERT_EQUALS_DELTA);
		Assert.assertEquals(DefaultCreateRandomVehicleServiceTest.GPS_BIKE_RATIO,
						(double) DefaultCreateRandomVehicleServiceTest.bikesWithGPSAmount
										/ (double) DefaultCreateRandomVehicleServiceTest.bikeAmount,
						DefaultCreateRandomVehicleServiceTest.ASSERT_EQUALS_DELTA);
		Assert.assertEquals(DefaultCreateRandomVehicleServiceTest.GPS_TRUCK_RATIO,
						(double) DefaultCreateRandomVehicleServiceTest.trucksWithGPSAmount
										/ (double) DefaultCreateRandomVehicleServiceTest.truckAmount,
						DefaultCreateRandomVehicleServiceTest.ASSERT_EQUALS_DELTA);
		Assert.assertEquals(
						DefaultCreateRandomVehicleServiceTest.GPS_MOTORCYCLE_RATIO,
						(double) DefaultCreateRandomVehicleServiceTest.motorcyclesWithGPSAmount
						/ (double) DefaultCreateRandomVehicleServiceTest.carAmount,
						DefaultCreateRandomVehicleServiceTest.ASSERT_EQUALS_DELTA);
	}

	/**
	 * Tests if enough unique ids are generated.
	 */
	@Test
	public void testIDs() {
		Set<Sensor<?, ?>> allIDs = new HashSet<>();
		allIDs.addAll(DefaultCreateRandomVehicleServiceTest.newSensorIDs);
		allIDs.addAll(DefaultCreateRandomVehicleServiceTest.usedSensorIDs);
		Assert.assertEquals(DefaultCreateRandomVehicleServiceTest.usedSensorIDs.
						size()
																+ DefaultCreateRandomVehicleServiceTest.newSensorIDs.
						size(), allIDs.size());

		Set<UUID> allUUIDs = new HashSet<>();
		allUUIDs.addAll(DefaultCreateRandomVehicleServiceTest.newUUIDs);
		allUUIDs.addAll(DefaultCreateRandomVehicleServiceTest.usedUUIDs);
		Assert.assertEquals(DefaultCreateRandomVehicleServiceTest.usedUUIDs.size()
																+ DefaultCreateRandomVehicleServiceTest.newUUIDs.
						size(), allUUIDs.size());
	}

	/**
	 * Tests if enough vehicles are generated.
	 */
	@Test
	public void testVehicleGeneration() {
		Assert.
						assertEquals(
										DefaultCreateRandomVehicleServiceTest.RANDOM_BIKE_AMOUNT,
										DefaultCreateRandomVehicleServiceTest.bikeAmount);
		Assert.assertEquals(DefaultCreateRandomVehicleServiceTest.RANDOM_CAR_AMOUNT,
						DefaultCreateRandomVehicleServiceTest.carAmount);
		Assert.assertEquals(
						DefaultCreateRandomVehicleServiceTest.RANDOM_MOTORCYCLE_AMOUNT,
						DefaultCreateRandomVehicleServiceTest.motorcycleAmount);
		Assert.assertEquals(
						DefaultCreateRandomVehicleServiceTest.RANDOM_TRUCK_AMOUNT,
						DefaultCreateRandomVehicleServiceTest.truckAmount);
	}
}
