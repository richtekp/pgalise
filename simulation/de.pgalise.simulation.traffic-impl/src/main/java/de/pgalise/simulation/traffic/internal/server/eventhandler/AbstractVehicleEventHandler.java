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
import java.util.Random;
import java.util.UUID;

import org.graphstream.graph.Path;

import de.pgalise.simulation.shared.event.traffic.AttractionTrafficEvent;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehicleData;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.shared.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.model.vehicle.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleData;
import de.pgalise.simulation.traffic.model.vehicle.TruckData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.server.scheduler.Item;

/**
 * This vehicle event handler creates a number of vehicle with random start nodes and one given target node. After a
 * certain time a new vehicle with the same ID and properties are generated that has the the given target node as start
 * node and a random target node for the {@link TrafficTrip}. The class are used by the {@link AttractionTrafficEvent}.
 * 
 * @author Andreas
 * @version 1.0
 */
public abstract class AbstractVehicleEventHandler implements TrafficEventHandler {
	/**
	 * Traffic server
	 */
	private TrafficServerLocal server;

	/**
	 * Random generator
	 */
	private Random random;

	/**
	 * Constructor
	 */
	public AbstractVehicleEventHandler() {

	}

	/**
	 * Creates an vehicle
	 * 
	 * @param data
	 *            Vehicle informations
	 * @param trip
	 *            Traffic trip
	 * @return Vehicle<? extends VehicleData>
	 */
	public Vehicle<? extends VehicleData> createVehicle(CreateRandomVehicleData data, TrafficTrip trip) {
		switch (data.getVehicleInformation().getVehicleType()) {
			case TRUCK:
				return this.createTruck(trip, data.getVehicleInformation().getVehicleID(), data.getVehicleInformation()
						.getName(), 0.0, data.getSensorHelpers(), data.getVehicleInformation().isGpsActivated());
			case BIKE:
				return this.createBike(trip, data.getVehicleInformation().getVehicleID(), data.getVehicleInformation()
						.getName(), 0.0, data.getSensorHelpers(), data.getVehicleInformation().isGpsActivated());
			case MOTORCYCLE:
				return this.createMotorcycle(trip, data.getVehicleInformation().getVehicleID(), data
						.getVehicleInformation().getName(), 0.0, data.getSensorHelpers(), data.getVehicleInformation()
						.isGpsActivated());
			case CAR:
			default:
				return this.createCar(trip, data.getVehicleInformation().getVehicleID(), data.getVehicleInformation()
						.getName(), 0.0, data.getSensorHelpers(), data.getVehicleInformation().isGpsActivated());
		}
	}

	public TrafficServerLocal getServer() {
		return this.server;
	}

	@Override
	public void init(TrafficServerLocal server) {
		this.server = server;
		random = new Random(server.getServiceDictionary().getRandomSeedService()
				.getSeed(AbstractVehicleEventHandler.class.getName()));
	}

	/**
	 * Schedules an vehicle with the given start time
	 * 
	 * @param vehicle
	 *            Vehicle
	 * @param startTime
	 *            start time of the vehicle
	 */
	public void scheduleVehicle(Vehicle<? extends VehicleData> vehicle, long startTime) {
		if (vehicle != null) {
			try {
				Item item = new Item(vehicle, startTime, this.getServer().getUpdateIntervall());
				// item.setLastUpdate(startTime - this.getServer().getUpdateIntervall());
				this.getServer().getScheduler().scheduleItem(item);
				// server.getScheduler().scheduleItem(new Item(v, trip.getStartTimeWayBack(), true));
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Returns the GPS SensorHelper
	 * 
	 * @param sensors
	 *            List with SensorHelpers
	 * @return SensorHelper for GPS Sensor
	 */
	private SensorHelper getGPSSensor(List<SensorHelper> sensors) {
		for (SensorHelper sensorHelper : sensors) {
			SensorType type = sensorHelper.getSensorType();
			if (SensorType.GPS.contains(type)) {
				return sensorHelper;
			}
		}

		return null;
	}

	/**
	 * Create bike
	 * 
	 * @param trip
	 *            Trip
	 * @param vehicleID
	 *            ID
	 * @param name
	 *            Name
	 * @param velocity
	 *            Velocity
	 * @param sensorHelpers
	 *            List with sensors
	 * @param gpsActivated
	 *            True if the GPS sensor should be activated
	 * @return bicycle
	 */
	protected Vehicle<BicycleData> createBike(TrafficTrip trip, UUID vehicleID, String name, double velocity,
			List<SensorHelper> sensorHelpers, boolean gpsActivated) {
		Vehicle<BicycleData> bike = null;
		TrafficTrip tmpTrip = trip;

		Path path = this.server.getShortestPath(this.server.getGraph().getNode(tmpTrip.getStartNode()), this.server
				.getGraph().getNode(tmpTrip.getTargetNode()));

		// check if path could not be computed between the nodes
		if (path.getNodeCount() > 1) {
			SensorHelper gpsSensorHelper = this.getGPSSensor(sensorHelpers);
			bike = this.server.getBikeFactory().createRandomBicycle(vehicleID, gpsSensorHelper);
			if (name != null) {
				bike.setName(name);
			}

			if (velocity > 0)
				bike.setVelocity(velocity);
			else {
				double randomVelocity = (((random.nextInt(7) + 15) * 1000) / (3600))
						;
				bike.setVelocity(randomVelocity);
			}
			bike.setPath(path);

			bike.setHasGPS(gpsActivated && (gpsSensorHelper != null));
		}
		return bike;
	}

	/**
	 * Create car
	 * 
	 * @param trip
	 *            Trip
	 * @param vehicleID
	 *            ID
	 * @param name
	 *            Name
	 * @param velocity
	 *            Velocity
	 * @param sensorHelpers
	 *            List with sensors
	 * @param gpsActivated
	 *            True if the GPS sensor should be activated
	 * @return Car
	 */
	protected Vehicle<CarData> createCar(final TrafficTrip trip, final UUID vehicleID, final String name,
			final double velocity, final List<SensorHelper> sensorHelpers, final boolean gpsActivated) {
		Vehicle<CarData> car = null;
		TrafficTrip tmpTrip = trip;

		// log.debug("Calculating route "+trip);
		Path path = this.server.getShortestPath(this.server.getGraph().getNode(tmpTrip.getStartNode()), this.server
				.getGraph().getNode(tmpTrip.getTargetNode()));

		// check if path could not be computed between the nodes
		if (path.getNodeCount() > 1) {
			SensorHelper gpsSensorHelper = this.getGPSSensor(sensorHelpers);
			car = this.server.getCarFactory().createRandomCar(vehicleID, gpsSensorHelper);

			if (name != null) {
				car.setName(name);
			}

			car.setVelocity(velocity);
			car.setPath(path);

			car.setHasGPS(gpsActivated && (gpsSensorHelper != null));
		}
		return car;
	}

	/**
	 * Create motorcycle
	 * 
	 * @param trip
	 *            Trip
	 * @param vehicleID
	 *            ID
	 * @param name
	 *            Name
	 * @param velocity
	 *            Velocity
	 * @param sensorHelpers
	 *            List with sensors
	 * @param gpsActivated
	 *            True if the GPS sensor should be activated
	 * @return Motorcycle
	 */
	protected Vehicle<MotorcycleData> createMotorcycle(TrafficTrip trip, UUID vehicleID, String name, double velocity,
			List<SensorHelper> sensorHelpers, boolean gpsActivated) {
		Vehicle<MotorcycleData> motorcycle = null;
		TrafficTrip tmpTrip = trip;

		Path path = this.server.getShortestPath(this.server.getGraph().getNode(tmpTrip.getStartNode()), this.server
				.getGraph().getNode(tmpTrip.getTargetNode()));

		// check if path could not be computed between the nodes
		if (path.getNodeCount() > 1) {
			SensorHelper gpsSensorHelper = this.getGPSSensor(sensorHelpers);
			motorcycle = this.server.getMotorcycleFactory().createRandomMotorcycle(vehicleID, gpsSensorHelper);
			if (name != null) {
				motorcycle.setName(name);
			}

			motorcycle.setVelocity(velocity);
			motorcycle.setPath(path);

			motorcycle.setHasGPS(gpsActivated && (gpsSensorHelper != null));
		}
		return motorcycle;
	}

	/**
	 * Create truck
	 * 
	 * @param trip
	 *            Trip
	 * @param vehicleID
	 *            ID
	 * @param name
	 *            Name
	 * @param velocity
	 *            Velocity
	 * @param sensorHelpers
	 *            List with sensors
	 * @param gpsActivated
	 *            True if the GPS sensor should be activated
	 * @return Truck
	 */
	protected Vehicle<TruckData> createTruck(TrafficTrip trip, UUID vehicleID, String name, double velocity,
			List<SensorHelper> sensorHelpers, boolean gpsActivated) {
		Vehicle<TruckData> truck = null;
		TrafficTrip tmpTrip = trip;

		Path path = this.server.getShortestPath(this.server.getGraph().getNode(tmpTrip.getStartNode()), this.server
				.getGraph().getNode(tmpTrip.getTargetNode()));

		// check if path could not be computed between the nodes
		if (path.getNodeCount() > 1) {
			SensorHelper gpsSensorHelper = this.getGPSSensor(sensorHelpers);
			truck = this.server.getTruckFactory().createRandomTruck(vehicleID, gpsSensorHelper);
			if (name != null) {
				truck.setName(name);
			}

			truck.setVelocity(velocity);
			truck.setPath(path);

			truck.setHasGPS(gpsActivated && (gpsSensorHelper != null));
		}
		return truck;
	}
}
