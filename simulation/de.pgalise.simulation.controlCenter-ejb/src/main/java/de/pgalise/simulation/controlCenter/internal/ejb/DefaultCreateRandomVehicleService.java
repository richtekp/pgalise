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

import de.pgalise.simulation.controlCenter.internal.util.service.CreateRandomVehicleService;
import de.pgalise.simulation.controlCenter.internal.util.service.SensorInterfererService;
import de.pgalise.simulation.controlCenter.model.RandomVehicleBundle;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.VehicleInformation;
import de.pgalise.simulation.traffic.event.CreateRandomBicycleData;
import de.pgalise.simulation.traffic.event.CreateRandomCarData;
import de.pgalise.simulation.traffic.event.CreateRandomMotorcycleData;
import de.pgalise.simulation.traffic.event.CreateRandomTruckData;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.ejb.EJB;
import javax.ejb.Stateful;

/**
 * Default implementation of random dynamic sensor service. Creates random car
 * and random bike events to produces the required random dynamic sensors. It
 * uses the {@link SensorInterfererService} to archive its work.
 *
 * @author Timo
 */
@Stateful
public class DefaultCreateRandomVehicleService implements
  CreateRandomVehicleService {

  private static final long serialVersionUID = 1L;

  private SensorInterfererService sensorInterfererService;
  @EJB
  private TrafficServerLocal<?> trafficServerLocal;
  private Output output;
  @EJB
  private TrafficSensorFactory sensorFactory;

  public DefaultCreateRandomVehicleService() {
  }

  /**
   * Contructor
   *
   * @param sensorInterfererService
   * @param trafficServerLocal
   */
  public DefaultCreateRandomVehicleService(
    SensorInterfererService sensorInterfererService,
    TrafficServerLocal<?> trafficServerLocal) {
    this.sensorInterfererService = sensorInterfererService;
    this.trafficServerLocal = trafficServerLocal;
  }

  public void init(InitParameter initParameter) {
    this.output = initParameter.getOutput();
  }

  @Override
  public TrafficEvent<?> createRandomDynamicSensors(
    RandomVehicleBundle randomDynamicSensorBundle,
    RandomSeedService randomSeedService,
    boolean withSensorInterferer) {

    Random random = new Random(randomSeedService.getSeed(
      DefaultCreateRandomVehicleService.class.getName()));
    List<CreateRandomVehicleData> createRandomVehicleDataList = new LinkedList<>();

    /* create cars: */
    for (int i = 0; i < randomDynamicSensorBundle.getRandomCarAmount(); i++) {
      boolean gpsActivated = false;

      GpsSensor sensorID = null;
      if (i < randomDynamicSensorBundle.getRandomCarAmount()
        * randomDynamicSensorBundle.getGpsCarRatio()) {
        gpsActivated = true;
        sensorID = sensorFactory.createGpsSensor(
          withSensorInterferer);
        NavigationNode sensorNode = null;
      }

      createRandomVehicleDataList.add(new CreateRandomCarData(
        sensorID,
        new VehicleInformation(gpsActivated,
          VehicleTypeEnum.CAR,
          VehicleModelEnum.CAR_RANDOM,
          null,
          null)));
    }

    /* create bikes: */
    for (int i = 0; i < randomDynamicSensorBundle.getRandomBikeAmount(); i++) {
      boolean gpsActivated = false;

      GpsSensor sensorID = null;
      if (i < randomDynamicSensorBundle.getRandomBikeAmount()
        * randomDynamicSensorBundle.getGpsBikeRatio()) {
        gpsActivated = true;
        sensorID = sensorFactory.createGpsSensor(
          withSensorInterferer);
        NavigationNode sensorNode = null;
      }

      createRandomVehicleDataList.add(new CreateRandomBicycleData(
        sensorID,
        new VehicleInformation(gpsActivated,
          VehicleTypeEnum.BIKE,
          VehicleModelEnum.BIKE_RANDOM,
          null,
          null)));
    }

    /* create trucks: */
    for (int i = 0; i < randomDynamicSensorBundle.getRandomTruckAmount(); i++) {
      boolean gpsActivated = false;

      GpsSensor sensorID = null;
      if (i < randomDynamicSensorBundle.getRandomTruckAmount()
        * randomDynamicSensorBundle.getGpsTruckRatio()) {
        gpsActivated = true;
        sensorID = sensorFactory.createGpsSensor(
          withSensorInterferer);
        NavigationNode sensorNode = null;
      }

      createRandomVehicleDataList.add(new CreateRandomTruckData(
        sensorID,
        new VehicleInformation(gpsActivated,
          VehicleTypeEnum.TRUCK,
          VehicleModelEnum.TRUCK_RANDOM,
          null,
          null)));
    }

    /* create motorcycle: */
    for (int i = 0; i < randomDynamicSensorBundle.getRandomMotorcycleAmount();
      i++) {
      boolean gpsActivated = false;

      GpsSensor sensorID = null;
      if (i < randomDynamicSensorBundle.getRandomMotorcycleAmount()
        * randomDynamicSensorBundle.getGpsMotorcycleRatio()) {
        gpsActivated = true;
        sensorID = sensorFactory.createGpsSensor(
          withSensorInterferer);
        NavigationNode sensorNode = null;
      }

      createRandomVehicleDataList.add(new CreateRandomMotorcycleData(
        sensorID,
        new VehicleInformation(gpsActivated,
          VehicleTypeEnum.MOTORCYCLE,
          VehicleModelEnum.MOTORCYCLE_RANDOM,
          null,
          null)));
    }

    return new CreateRandomVehiclesEvent(trafficServerLocal,
      System.
      currentTimeMillis(),
      0,
      createRandomVehicleDataList);
  }
}
