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
 
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleData;
import de.pgalise.simulation.traffic.model.vehicle.TruckData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;

/**
 * Creates random vehicles with random trips.
 * 
 * @param <E> 
 * @author Mustafa
 * @version 1.0
 */
public class CreateRandomVehicleEventHandler<D extends VehicleData, E extends CreateRandomVehiclesEvent<D>> extends AbstractVehicleEventHandler<D,E> {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CreateRandomVehicleEventHandler.class);

	/**
	 * Simulation event type
	 */
	private static final EventType type = TrafficEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT;

	/**
	 * Constructor
	 */
	public CreateRandomVehicleEventHandler() {
	}

	@Override
	public EventType getTargetEventType() {
		return CreateRandomVehicleEventHandler.type;
	}

	@Override
	public void handleEvent(CreateRandomVehiclesEvent event) {
		if (!event.getResponsibleServer().equals(this.getResponsibleServer())) {
			return;
		}

		log.info("Processing CREATE_RANDOM_VEHICLES_EVENT: Vehicles=" + event.getCreateRandomVehicleDataList().size());

		final List<CreateRandomVehicleData> vehicleList = event.getCreateRandomVehicleDataList();

		final int cores = 1;// Runtime.getRuntime().availableProcessors() / 1;

		final int listPartSize = vehicleList.size() / cores;

		final CreateThread[] createThreads = new CreateThread[cores];
		for (int i = 0; i < createThreads.length - 1; i++) {
			createThreads[i] = new CreateThread(this, vehicleList, i * listPartSize, i * listPartSize + listPartSize);
			createThreads[i].setPriority(Thread.MAX_PRIORITY);
		}
		createThreads[createThreads.length - 1] = new CreateThread(this, vehicleList,
				(createThreads.length - 1) * listPartSize, vehicleList.size());
		createThreads[createThreads.length - 1].setPriority(Thread.MAX_PRIORITY);

		for (final CreateThread createThread : createThreads) {
			createThread.start();
		}

		for (final CreateThread createThread : createThreads) {
			try {
				log.debug("Waiting for" + createThread);
				createThread.join();
				System.out.println("Finished" + createThread);
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Threads for creating routes
	 * 
	 * @author marcus
	 */
	private static class CreateThread<X extends VehicleData, Y extends CreateRandomVehiclesEvent<X>> extends Thread {

		private final CreateRandomVehicleEventHandler<X,Y> eHandler;
		private final List<CreateRandomVehicleData> vehicleList;
		private final int startIndex;
		private final int endIndex;

		private CreateThread(final CreateRandomVehicleEventHandler<X,Y> eHandler,
				List<CreateRandomVehicleData> vehicleList, int startIndex, int endIndex) {
			this.eHandler = eHandler;
			this.vehicleList = vehicleList;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {

			long currentTime = this.eHandler.getResponsibleServer().getCurrentTime();
			// int amountOfServer = CreateRandomVehicleEventHandler.this.getServer().getServerListSize();
			// int myServer = this.getServer().;

			int amountBikes = 0;
			int amountCars = 0;
			int amountMotorcycles = 0;
			int amountTrucks = 0;

			double limitBikes = this.eHandler.getResponsibleServer().getVehicleFuzzyManager().getPercentageValueBicycles();
			double limitCars = this.eHandler.getResponsibleServer().getVehicleFuzzyManager().getPercentageValueCars();
			// log.debug("CAR PERCENTAGE:"+limitCars);
			double limitMotorcycles = this.eHandler.getResponsibleServer().getVehicleFuzzyManager()
					.getPercentageValueMotorcycles();
			double limitTrucks = this.eHandler.getResponsibleServer().getVehicleFuzzyManager().getPercentageValueTrucks();

			// temporär, solange der governor noch nicht richtig funktioniert und immer 0% liefert
			// limitBikes = 0.5;
			// limitCars = 0.4;
			// limitMotorcycles = 0.3;
			// limitTrucks = 0.2;

			int counterBikes = 0;
			int counterCars = 0;
			int counterMotorcycles = 0;
			int counterTrucks = 0;

			for (int i = this.startIndex; i < this.endIndex; i++) {
				final CreateRandomVehicleData data = this.vehicleList.get(i);
				switch (data.getVehicleInformation().getVehicleType()) {
					case BIKE:
						amountBikes++;
						break;
					case CAR:
						amountCars++;
						break;
					case MOTORCYCLE:
						amountMotorcycles++;
						break;
					case TRUCK:
						amountTrucks++;
						break;
					case BUS:
						break;
					default:
						break;
				}
			}

			// log.debug("Vehicles created (#bikes: " + amountBikes + ", #cars: " + amountCars + ", #motos: " +
			// amountMotorcycles + ", #trucks: " + amountTrucks+")");

			limitBikes = Math.floor(limitBikes * amountBikes);
			limitCars = Math.floor(limitCars * amountCars);
			limitMotorcycles = Math.floor(limitMotorcycles * amountMotorcycles);
			limitTrucks = Math.floor(limitTrucks * amountTrucks);

			int countScheduledBike = 0;
			int countDelayedBike = 0;
			int countSpareBike = 0;
			int countScheduledCar = 0;
			int countDelayedCar = 0;
			int countSpareCar = 0;
			int countScheduledMoto = 0;
			int countDelayedMoto = 0;
			int countSpareMoto = 0;
			int countScheduledTruck = 0;
			int countDelayedTruck = 0;
			int countSpareTruck = 0;
			int countBikesWithoutPath = 0;
			int countCarsWithoutPath = 0;
			int countMotosWithoutPath = 0;
			int countTrucksWithoutPath = 0;

			for (int i = this.startIndex; i < this.endIndex; i++) {
				final CreateRandomVehicleData data = this.vehicleList.get(i);
				TrafficTrip trip = this.eHandler.getResponsibleServer().createTrip(this.eHandler.getResponsibleServer().getCityZone(),
						data.getVehicleInformation().getVehicleType());

				// Create vehicle
				final Vehicle<?> v = this.eHandler.createVehicle(data, trip);

				if (v == null) {
					switch (data.getVehicleInformation().getVehicleType()) {
						case BIKE:
							countBikesWithoutPath++;
							break;
						case CAR:
							countCarsWithoutPath++;
							break;
						case MOTORCYCLE:
							countMotosWithoutPath++;
							break;
						case TRUCK:
							countTrucksWithoutPath++;
							break;
						default:
							break;
					}

					continue;
				}

				// check if a vehicle should be scheduled immediately or will be stored in the fuzzy manager
				if (v.getData().getClass().equals(BicycleData.class)) {
					this.eHandler.getResponsibleServer().getVehicleFuzzyManager().increaseMaxBikes();
					counterBikes++;
					if (counterBikes <= limitBikes && trip.getStartTime() >= currentTime) {
						this.eHandler.scheduleVehicle(v, trip.getStartTime());
						countScheduledBike++;
					} else {
						this.eHandler.getResponsibleServer().getVehicleFuzzyManager().addSpareBicycle(v);
						if (counterBikes > limitBikes) {
							countSpareBike++;
						}
						else {
							countDelayedBike++;
						}
					}
				} else if (v.getData().getClass().equals(TruckData.class)) { // must be proved before CarDate (because
																				// of inheritance)
					this.eHandler.getResponsibleServer().getVehicleFuzzyManager().increaseMaxTrucks();
					counterTrucks++;
					if (counterTrucks <= limitTrucks && trip.getStartTime() >= currentTime) {
						this.eHandler.scheduleVehicle(v, trip.getStartTime());
						countScheduledTruck++;
					} else {
						this.eHandler.getResponsibleServer().getVehicleFuzzyManager().addSpareTruck(v);
						if (counterTrucks > limitTrucks) {
							countSpareTruck++;
						}
						else {
							countDelayedTruck++;
						}
					}
				} else if (v.getData().getClass().equals(CarData.class)) {
					this.eHandler.getResponsibleServer().getVehicleFuzzyManager().increaseMaxCars();
					counterCars++;
					if (counterCars <= limitCars && trip.getStartTime() >= currentTime) {
						this.eHandler.scheduleVehicle(v, trip.getStartTime());
						countScheduledCar++;
					} else {
						this.eHandler.getResponsibleServer().getVehicleFuzzyManager().addSpareCar(v);
						if (counterCars > limitCars) {
							countSpareCar++;
						}
						else {
							countDelayedCar++;
						}
					}
				} else if (v.getData().getClass().equals(MotorcycleData.class)) {
					this.eHandler.getResponsibleServer().getVehicleFuzzyManager().increaseMaxMotorcycles();
					counterMotorcycles++;
					if (counterMotorcycles <= limitMotorcycles && trip.getStartTime() >= currentTime) {
						this.eHandler.scheduleVehicle(v, trip.getStartTime());
						countScheduledMoto++;
					} else {
						this.eHandler.getResponsibleServer().getVehicleFuzzyManager().addSpareMotorcycle(v);
						if (counterMotorcycles > limitMotorcycles) {
							countSpareMoto++;
						}
						else {
							countDelayedMoto++;
						}
					}
				}
			}

			log.info("Scheduled bikes: " + countScheduledBike + ", cars: " + countScheduledCar + ", motos: "
					+ countScheduledMoto + ", trucks: " + countScheduledTruck);
			log.info("Delayed bikes: " + countDelayedBike + ", cars: " + countDelayedCar + ", motos: "
					+ countDelayedMoto + ", trucks: " + countDelayedTruck);
			log.info("Spare bikes: " + countSpareBike + ", cars: " + countSpareCar + ", motos: " + countSpareMoto
					+ ", trucks: " + countSpareTruck);
			log.info("Without path bikes: " + countBikesWithoutPath + ", cars: " + countCarsWithoutPath + ", motos: "
					+ countMotosWithoutPath + ", trucks: " + countTrucksWithoutPath);

			log.info("Total fuzzy bikes: " + (countScheduledBike + countDelayedBike) + ", cars: "
					+ (countScheduledCar + countDelayedCar) + ", motos: " + (countScheduledMoto + countDelayedMoto)
					+ ", trucks: " + (countScheduledTruck + countDelayedTruck));
		}
	}
}
