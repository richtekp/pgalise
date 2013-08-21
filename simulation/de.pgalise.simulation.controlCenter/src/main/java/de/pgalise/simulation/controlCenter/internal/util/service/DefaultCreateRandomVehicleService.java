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
 
package de.pgalise.simulation.controlCenter.internal.util.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.google.inject.Inject;

import de.pgalise.simulation.controlCenter.internal.model.RandomVehicleBundle;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehicleData;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehiclesEvent;
import de.pgalise.simulation.shared.event.traffic.TrafficEvent;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.shared.traffic.VehicleInformation;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
/**
 * Default implementation of random dynamic sensor service.
 * Creates random car and random bike events to produces
 * the required random dynamic sensors.
 * It uses the {@link SensorInterfererService} to archive its work.
 * @author Timo
 */
public class DefaultCreateRandomVehicleService implements CreateRandomVehicleService {

	private SensorInterfererService sensorInterfererService;

	/**
	 * Contructor
	 * @param sensorInterfererService
	 */
	@Inject
	public DefaultCreateRandomVehicleService(SensorInterfererService sensorInterfererService) {
		this.sensorInterfererService = sensorInterfererService;
	}

	@Override
	public TrafficEvent createRandomDynamicSensors(
			RandomVehicleBundle randomDynamicSensorBundle, RandomSeedService randomSeedService,
			boolean withSensorInterferer) {

		Set<Integer> usedSensorIDsCopy = new HashSet<>(randomDynamicSensorBundle.getUsedSensorIDs());
		Set<UUID> usedVehicleCopy = new HashSet<>(randomDynamicSensorBundle.getUsedUUIDs());
		Random random = new Random(randomSeedService.getSeed(DefaultCreateRandomVehicleService.class.getName()));
		List<CreateRandomVehicleData> createRandomVehicleDataList = new LinkedList<>();

		/* create cars: */
		for(int i = 0; i < randomDynamicSensorBundle.getRandomCarAmount(); i++) {
			boolean gpsActivated = false;
			UUID vehicleID = new UUID(random.nextLong(), random.nextLong());
			while(usedVehicleCopy.contains(vehicleID)) {
				vehicleID = new UUID(random.nextLong(), random.nextLong());
			}
			usedVehicleCopy.add(vehicleID);

			List<SensorHelper> sensorHelperList = new LinkedList<>();
			if(i < randomDynamicSensorBundle.getRandomCarAmount() * randomDynamicSensorBundle.getGpsCarRatio()) {
				gpsActivated = true;
				int sensorID = Math.abs(random.nextInt());
				while(usedSensorIDsCopy.contains(sensorID)) {
					sensorID = Math.abs(random.nextInt());
				}
				usedSensorIDsCopy.add(sensorID);

				sensorHelperList.add(new SensorHelper(sensorID, 
						new Coordinate(), 
						SensorType.GPS_CAR, 
						this.sensorInterfererService.getSensorInterferes(SensorType.GPS_CAR, withSensorInterferer), ""));
			}

			createRandomVehicleDataList.add(new CreateRandomVehicleData(sensorHelperList, new VehicleInformation(vehicleID, gpsActivated, VehicleTypeEnum.CAR, 
					VehicleModelEnum.CAR_RANDOM, null, "" +vehicleID)));
		}
		
		/* create bikes: */
		for(int i = 0; i < randomDynamicSensorBundle.getRandomBikeAmount(); i++) {
			boolean gpsActivated = false;
			UUID vehicleID = new UUID(random.nextLong(), random.nextLong());
			while(usedVehicleCopy.contains(vehicleID)) {
				vehicleID = new UUID(random.nextLong(), random.nextLong());
			}
			usedVehicleCopy.add(vehicleID);

			List<SensorHelper> sensorHelperList = new LinkedList<>();
			if(i < randomDynamicSensorBundle.getRandomBikeAmount() * randomDynamicSensorBundle.getGpsBikeRatio()) {
				gpsActivated = true;
				int sensorID = Math.abs(random.nextInt());
				while(usedSensorIDsCopy.contains(sensorID)) {
					sensorID = Math.abs(random.nextInt());
				}
				usedSensorIDsCopy.add(sensorID);

				sensorHelperList.add(new SensorHelper(sensorID, new Coordinate(), SensorType.GPS_BIKE, 
						this.sensorInterfererService.getSensorInterferes(SensorType.GPS_BIKE, withSensorInterferer), ""));
			}

			createRandomVehicleDataList.add(new CreateRandomVehicleData(sensorHelperList, new VehicleInformation(vehicleID, gpsActivated, VehicleTypeEnum.BIKE, 
					VehicleModelEnum.BIKE_RANDOM, null, "" +vehicleID)));
		}
		
		/* create trucks: */
		for(int i = 0; i < randomDynamicSensorBundle.getRandomTruckAmount(); i++) {
			boolean gpsActivated = false;
			UUID vehicleID = new UUID(random.nextLong(), random.nextLong());
			while(usedVehicleCopy.contains(vehicleID)) {
				vehicleID = new UUID(random.nextLong(), random.nextLong());
			}
			usedVehicleCopy.add(vehicleID);

			List<SensorHelper> sensorHelperList = new LinkedList<>();
			if(i < randomDynamicSensorBundle.getRandomTruckAmount() * randomDynamicSensorBundle.getGpsTruckRatio()) {
				gpsActivated = true;
				int sensorID = Math.abs(random.nextInt());
				while(usedSensorIDsCopy.contains(sensorID)) {
					sensorID = Math.abs(random.nextInt());
				}
				usedSensorIDsCopy.add(sensorID);

				sensorHelperList.add(new SensorHelper(sensorID, new Coordinate(), SensorType.GPS_TRUCK, 
						this.sensorInterfererService.getSensorInterferes(SensorType.GPS_TRUCK, withSensorInterferer), ""));
			}

			createRandomVehicleDataList.add(new CreateRandomVehicleData(sensorHelperList, new VehicleInformation(vehicleID, gpsActivated, VehicleTypeEnum.TRUCK, 
					VehicleModelEnum.TRUCK_RANDOM, null, "" +vehicleID)));
		}
		
		/* create motorcycle: */
		for(int i = 0; i < randomDynamicSensorBundle.getRandomMotorcycleAmount(); i++) {
			boolean gpsActivated = false;
			UUID vehicleID = new UUID(random.nextLong(), random.nextLong());
			while(usedVehicleCopy.contains(vehicleID)) {
				vehicleID = new UUID(random.nextLong(), random.nextLong());
			}
			usedVehicleCopy.add(vehicleID);

			List<SensorHelper> sensorHelperList = new LinkedList<>();
			if(i < randomDynamicSensorBundle.getRandomMotorcycleAmount() * randomDynamicSensorBundle.getGpsMotorcycleRatio()) {
				gpsActivated = true;
				int sensorID = random.nextInt(Integer.MAX_VALUE);
				while(usedSensorIDsCopy.contains(sensorID)) {
					sensorID = Math.abs(random.nextInt(Integer.MAX_VALUE));
				}
				usedSensorIDsCopy.add(sensorID);

				sensorHelperList.add(new SensorHelper(sensorID, new Coordinate(), SensorType.GPS_MOTORCYCLE, 
						this.sensorInterfererService.getSensorInterferes(SensorType.GPS_MOTORCYCLE, withSensorInterferer), ""));
			}

			createRandomVehicleDataList.add(new CreateRandomVehicleData(sensorHelperList, new VehicleInformation(vehicleID, gpsActivated, VehicleTypeEnum.MOTORCYCLE, 
					VehicleModelEnum.MOTORCYCLE_RANDOM, null, "" +vehicleID)));
		}


		return new CreateRandomVehiclesEvent(UUID.randomUUID(), createRandomVehicleDataList);
	}
}
