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
package de.pgalise.simulation.controlCenter.internal.ejb;

import de.pgalise.simulation.controlCenter.internal.ejb.DefaultCreateRandomVehicleService;
import de.pgalise.simulation.controlCenter.internal.util.service.CreateRandomVehicleService;
import de.pgalise.simulation.controlCenter.internal.util.service.SensorInterfererService;
import de.pgalise.simulation.controlCenter.model.RandomVehicleBundle;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.traffic.event.CreateRandomBicycleData;
import de.pgalise.simulation.traffic.event.CreateRandomCarData;
import de.pgalise.simulation.traffic.event.CreateRandomMotorcycleData;
import de.pgalise.simulation.traffic.event.CreateRandomTruckData;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.testutils.TestUtils;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
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
public class DefaultCreateRandomVehicleServiceTest {

  @EJB
  private RandomSeedService randomSeedService;
  @EJB
  private CreateRandomVehicleService testClass;
  private final Output tcpIpOutput = EasyMock.createNiceMock(Output.class);

  public DefaultCreateRandomVehicleServiceTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
  }

  /**
   * Mock for this test.
   *
   * @author Timo
   */
  private static class SensorInterfererServiceMock implements
    SensorInterfererService {

    @Override
    public List<SensorInterfererType> getSensorInterfererTypes(
      SensorType sensorType,
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
  private static final Set<Sensor<?, ?>> newSensorIDs = new HashSet<>();

  /**
   * Tests if enough GPS sensors are generated.
   */
  @Test
  public void testGPSGeneration() {
    RandomVehicleBundle testData = new RandomVehicleBundle(
      DefaultCreateRandomVehicleServiceTest.RANDOM_CAR_AMOUNT,
      DefaultCreateRandomVehicleServiceTest.GPS_CAR_RATIO,
      DefaultCreateRandomVehicleServiceTest.RANDOM_BIKE_AMOUNT,
      DefaultCreateRandomVehicleServiceTest.GPS_BIKE_RATIO,
      DefaultCreateRandomVehicleServiceTest.RANDOM_TRUCK_AMOUNT,
      DefaultCreateRandomVehicleServiceTest.GPS_TRUCK_RATIO,
      DefaultCreateRandomVehicleServiceTest.RANDOM_MOTORCYCLE_AMOUNT,
      DefaultCreateRandomVehicleServiceTest.GPS_MOTORCYCLE_RATIO);

    TrafficEvent<?> result = testClass.createRandomDynamicSensors(testData,
      randomSeedService,
      true);
    int carAmount = 0, carsWithGPSAmount = 0, bikeAmount = 0, bikesWithGPSAmount = 0, motorcycleAmount = 0, motorcyclesWithGPSAmount = 0, truckAmount = 0, trucksWithGPSAmount = 0;

    /* Count vehicles and their gps ratio: */
    Assert.assertEquals(result.getType(),
      TrafficEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT);
    for (CreateRandomVehicleData createRandomVehicleData
      : ((CreateRandomVehiclesEvent<?>) result)
      .getCreateRandomVehicleDataList()) {
      if (createRandomVehicleData.getClass().equals(CreateRandomCarData.class)) {
        carAmount++;
        if (createRandomVehicleData.getVehicleInformation().isGpsActivated()) {
          carsWithGPSAmount++;
        }
        DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
          ((CreateRandomCarData) createRandomVehicleData).getGpsSensor());
      } else if (createRandomVehicleData.getClass().equals(
        CreateRandomBicycleData.class)) {
        bikeAmount++;
        if (createRandomVehicleData.getVehicleInformation().isGpsActivated()) {
          bikesWithGPSAmount++;
        }
        DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
          ((CreateRandomBicycleData) createRandomVehicleData).getGpsSensor());
      } else if (createRandomVehicleData.getClass().equals(
        CreateRandomMotorcycleData.class)) {
        motorcycleAmount++;
        if (createRandomVehicleData.getVehicleInformation().isGpsActivated()) {
          motorcyclesWithGPSAmount++;
        }
        DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
          ((CreateRandomMotorcycleData) createRandomVehicleData).
          getGpsSensor());
      } else if (createRandomVehicleData.getClass().equals(
        CreateRandomTruckData.class)) {
        truckAmount++;
        if (createRandomVehicleData.getVehicleInformation().isGpsActivated()) {
          trucksWithGPSAmount++;
        }
        DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
          ((CreateRandomTruckData) createRandomVehicleData).getGpsSensor());
      } else {
        throw new IllegalArgumentException();
      }
    }

    Assert.assertEquals(DefaultCreateRandomVehicleServiceTest.GPS_CAR_RATIO,
      (double) carsWithGPSAmount
      / (double) carAmount,
      DefaultCreateRandomVehicleServiceTest.ASSERT_EQUALS_DELTA);
    Assert.assertEquals(DefaultCreateRandomVehicleServiceTest.GPS_BIKE_RATIO,
      (double) bikesWithGPSAmount
      / (double) bikeAmount,
      DefaultCreateRandomVehicleServiceTest.ASSERT_EQUALS_DELTA);
    Assert.assertEquals(DefaultCreateRandomVehicleServiceTest.GPS_TRUCK_RATIO,
      (double) trucksWithGPSAmount
      / (double) truckAmount,
      DefaultCreateRandomVehicleServiceTest.ASSERT_EQUALS_DELTA);
    Assert.assertEquals(
      DefaultCreateRandomVehicleServiceTest.GPS_MOTORCYCLE_RATIO,
      (double) motorcyclesWithGPSAmount
      / (double) carAmount,
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
      size(),
      allIDs.size());
  }

  /**
   * Tests if enough vehicles are generated.
   */
  @Test
  public void testVehicleGeneration() {

    int carAmount = 0, carsWithGPSAmount = 0, bikeAmount = 0, bikesWithGPSAmount = 0, motorcycleAmount = 0, motorcyclesWithGPSAmount = 0, truckAmount = 0, trucksWithGPSAmount = 0;

    RandomVehicleBundle testData = new RandomVehicleBundle(
      DefaultCreateRandomVehicleServiceTest.RANDOM_CAR_AMOUNT,
      DefaultCreateRandomVehicleServiceTest.GPS_CAR_RATIO,
      DefaultCreateRandomVehicleServiceTest.RANDOM_BIKE_AMOUNT,
      DefaultCreateRandomVehicleServiceTest.GPS_BIKE_RATIO,
      DefaultCreateRandomVehicleServiceTest.RANDOM_TRUCK_AMOUNT,
      DefaultCreateRandomVehicleServiceTest.GPS_TRUCK_RATIO,
      DefaultCreateRandomVehicleServiceTest.RANDOM_MOTORCYCLE_AMOUNT,
      DefaultCreateRandomVehicleServiceTest.GPS_MOTORCYCLE_RATIO);

    TrafficEvent<?> result = testClass.createRandomDynamicSensors(testData,
      randomSeedService,
      true);

    Assert.assertEquals(result.getType(),
      TrafficEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT);
    /* Count vehicles and their gps ratio: */
    for (CreateRandomVehicleData createRandomVehicleData
      : ((CreateRandomVehiclesEvent<?>) result)
      .getCreateRandomVehicleDataList()) {
      if (createRandomVehicleData.getClass().equals(CreateRandomCarData.class)) {
        carAmount++;
        if (createRandomVehicleData.getVehicleInformation().isGpsActivated()) {
          carsWithGPSAmount++;
        }
        DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
          ((CreateRandomCarData) createRandomVehicleData).getGpsSensor());
      } else if (createRandomVehicleData.getClass().equals(
        CreateRandomBicycleData.class)) {
        bikeAmount++;
        if (createRandomVehicleData.getVehicleInformation().isGpsActivated()) {
          bikesWithGPSAmount++;
        }
        DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
          ((CreateRandomBicycleData) createRandomVehicleData).getGpsSensor());
      } else if (createRandomVehicleData.getClass().equals(
        CreateRandomMotorcycleData.class)) {
        motorcycleAmount++;
        if (createRandomVehicleData.getVehicleInformation().isGpsActivated()) {
          motorcyclesWithGPSAmount++;
        }
        DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
          ((CreateRandomMotorcycleData) createRandomVehicleData).
          getGpsSensor());
      } else if (createRandomVehicleData.getClass().equals(
        CreateRandomTruckData.class)) {
        truckAmount++;
        if (createRandomVehicleData.getVehicleInformation().isGpsActivated()) {
          trucksWithGPSAmount++;
        }
        DefaultCreateRandomVehicleServiceTest.newSensorIDs.add(
          ((CreateRandomTruckData) createRandomVehicleData).getGpsSensor());
      } else {
        throw new IllegalArgumentException();
      }
    }

    Assert.
      assertEquals(
        DefaultCreateRandomVehicleServiceTest.RANDOM_BIKE_AMOUNT,
        bikeAmount);
    Assert.assertEquals(
      DefaultCreateRandomVehicleServiceTest.RANDOM_CAR_AMOUNT,
      carAmount);
    Assert.assertEquals(
      DefaultCreateRandomVehicleServiceTest.RANDOM_MOTORCYCLE_AMOUNT,
      motorcycleAmount);
    Assert.assertEquals(
      DefaultCreateRandomVehicleServiceTest.RANDOM_TRUCK_AMOUNT,
      truckAmount);
  }
}
