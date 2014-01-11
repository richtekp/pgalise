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

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.RandomSeedService;
import java.util.List;
import java.util.Random;

import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.event.CreateRandomBicycleData;
import de.pgalise.simulation.traffic.event.CreateRandomCarData;
import de.pgalise.simulation.traffic.event.CreateRandomMotorcycleData;
import de.pgalise.simulation.traffic.event.CreateRandomTruckData;
import de.pgalise.simulation.traffic.internal.server.scheduler.DefaultScheduleItem;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleData;
import de.pgalise.simulation.traffic.model.vehicle.TruckData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import javax.ejb.EJB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This vehicle event handler creates a number of vehicle with random start
 * nodes and one given target node. After a certain time a new vehicle with the
 * same ID and properties are generated that has the the given target node as
 * start node and a random target node for the {@link TrafficTrip}. The class
 * are used by the {@link AttractionTrafficEvent}.
 *
 * @param <E>
 * @author Andreas
 * @version 1.0
 */
public abstract class AbstractVehicleEventHandler<D extends VehicleData, E extends VehicleEvent>
	extends AbstractTrafficEventHandler<D, E> implements VehicleEventHandler<E> {

	private final static Logger LOGGER = LoggerFactory.getLogger(
		AbstractVehicleEventHandler.class);

	/**
	 * Random generator
	 */
	private Random random;
	@EJB
	private RandomSeedService randomSeedService;
	
	/**
	 * Constructor
	 */
	public AbstractVehicleEventHandler() {
	}

	/**
	 * Creates an vehicle
	 *
	 * @param data Vehicle informations
	 * @param trip Traffic trip
	 * @return Vehicle<? extends VehicleData>
	 */
	public Vehicle<?> createVehicle(CreateRandomVehicleData data,
		TrafficTrip trip) {
		if(data instanceof CreateRandomTruckData) {
			return this.createTruck(trip,
					data.getVehicleInformation()
					.getName(),
					0.0,
					((CreateRandomTruckData)data).getGpsSensor(),
				data.getVehicleInformation().isGpsActivated());
		}else if(data instanceof CreateRandomBicycleData) {
			return this.createBike(trip,
					data.getVehicleInformation()
					.getName(),
					0.0,
					((CreateRandomBicycleData)data).getGpsSensor(),
				data.getVehicleInformation().isGpsActivated());
		}else if(data instanceof CreateRandomMotorcycleData) {
			return this.createMotorcycle(trip,
					data.getVehicleInformation()
					.getName(),
					0.0,
					((CreateRandomMotorcycleData)data).getGpsSensor(),
				data.getVehicleInformation().isGpsActivated());
		}else if (data instanceof CreateRandomCarData) {
			return this.createMotorcycle(trip,
					data.getVehicleInformation()
					.getName(),
					0.0,
					((CreateRandomMotorcycleData)data).getGpsSensor(),
				data.getVehicleInformation().isGpsActivated());
		}
		else {
			throw new IllegalArgumentException(String.format("unsupported subtype of %s", CreateRandomVehicleData.class));
		}
	}

	@Override
	public void init(TrafficServerLocal server) {
		setResponsibleServer(server);
		random = new Random(randomSeedService
			.getSeed(AbstractVehicleEventHandler.class.getName()));
	}

	/**
	 * Schedules an vehicle with the given start time
	 *
	 * @param vehicle Vehicle
	 * @param startTime start time of the vehicle
	 */
	public void scheduleVehicle(Vehicle<? extends VehicleData> vehicle,
		long startTime) {
		if (vehicle != null) {
			ScheduleItem item = new DefaultScheduleItem(vehicle,
				startTime,
				this.getResponsibleServer().getUpdateIntervall());
			// item.setLastUpdate(startTime - this.getServer().getUpdateIntervall());
			this.getResponsibleServer().getScheduler().scheduleItem(item);
		}
	}

	/**
	 * Returns the GPS SensorHelper or <code>null</code> if <tt>sensors</tt>
	 * doesn't contain a GPS sensor.
	 *
	 * @param sensors List with SensorHelpers
	 * @return SensorHelper for GPS Sensor
	 */
	private GpsSensor getGPSSensor(List<Sensor<?,?>> sensors) {
		for (Sensor<?,?> sensorHelper : sensors) {
			if (sensorHelper instanceof GpsSensor) {
				return (GpsSensor) sensorHelper;
			}
		}

		return null;
	}

	/**
	 * Create bike
	 *
	 * @param trip Trip
	 * @param vehicleID ID
	 * @param name Name
	 * @param velocity Velocity
	 * @param sensorHelpers List with sensors
	 * @param gpsActivated True if the GPS sensor should be activated
	 * @return bicycle
	 */
	protected Vehicle<BicycleData> createBike(TrafficTrip trip,
		String name,
		double velocity,
		GpsSensor gpsSensor,
		boolean gpsActivated) {
		Vehicle<BicycleData> bike = null;
		TrafficTrip tmpTrip = trip;

		List<TrafficEdge> path = this.getResponsibleServer().getShortestPath(
			tmpTrip.getStartNode(),
			tmpTrip.getTargetNode());

		// check if path could not be computed between the nodes
		if (path != null) {
			bike = this.getResponsibleServer().getBikeFactory().createRandomBicycle(
				getOutput());
			if (name != null) {
				bike.setName(name);
			}

			if (velocity > 0) {
				bike.setVelocity(velocity);
			} else {
				double randomVelocity = (((random.nextInt(7) + 15) * 1000) / (3600));
				bike.setVelocity(randomVelocity);
			}
			bike.setPath(path);
			if(gpsSensor != null) {
				bike.setGpsSensor(gpsSensor);
			}
			if (gpsActivated && gpsSensor != null) {
				bike.getGpsSensor().setActivated(true);
			}
		}
		return bike;
	}

	/**
	 * Create car
	 *
	 * @param trip Trip
	 * @param vehicleID ID
	 * @param name Name
	 * @param velocity Velocity
	 * @param sensorHelpers List with sensors
	 * @param gpsActivated True if the GPS sensor should be activated
	 * @return Car
	 */
	protected Vehicle<CarData> createCar(final TrafficTrip trip,
		final String name,
		final double velocity,
		final List<Sensor<?,?>> sensorHelpers,
		final boolean gpsActivated) {
		Vehicle<CarData> car = null;
		TrafficTrip tmpTrip = trip;

		// log.debug("Calculating route "+trip);
		List<TrafficEdge> path = this.getResponsibleServer().getShortestPath(
			tmpTrip.getStartNode(),
			tmpTrip.getTargetNode());

		// check if path could not be computed between the nodes
		if (path != null) {
			GpsSensor gpsSensorHelper = this.getGPSSensor(sensorHelpers);
			car = this.getResponsibleServer().getCarFactory().createRandomCar(
				getOutput());

			if (name != null) {
				car.setName(name);
			}

			car.setVelocity(velocity);
			car.setPath(path);

			if(gpsActivated && gpsSensorHelper != null) {
				car.getGpsSensor().setActivated(true);
			}
		}
		return car;
	}

	/**
	 * Create motorcycle
	 *
	 * @param trip Trip
	 * @param name Name
	 * @param gpsSensor
	 * @param velocity Velocity
	 * @param gpsActivated True if the GPS sensor should be activated
	 * @return Motorcycle
	 */
	protected Vehicle<MotorcycleData> createMotorcycle(TrafficTrip trip,
		String name,
		double velocity,
		GpsSensor gpsSensor,
		boolean gpsActivated) {
		Vehicle<MotorcycleData> motorcycle = null;
		TrafficTrip tmpTrip = trip;

		List<TrafficEdge> path = this.getResponsibleServer().getShortestPath(
			tmpTrip.getStartNode(),
			tmpTrip.getTargetNode()
		);

		// check if path could not be computed between the nodes
		if (path != null) {
			motorcycle = this.getResponsibleServer().getMotorcycleFactory().
				createRandomMotorcycle(getOutput());
			if (name != null) {
				motorcycle.setName(name);
			}

			motorcycle.setVelocity(velocity);
			motorcycle.setPath(path);
			if(gpsSensor != null) {
				motorcycle.setGpsSensor(gpsSensor);
			}

			if(gpsActivated && gpsSensor != null) {
				motorcycle.getGpsSensor().setActivated(true);
			}
		}
		return motorcycle;
	}

	/**
	 * Create truck
	 *
	 * @param trip Trip
	 * @param name Name
	 * @param gpsSensor
	 * @param velocity Velocity
	 * @param gpsActivated True if the GPS sensor should be activated
	 * @return Truck
	 */
	protected Vehicle<TruckData> createTruck(TrafficTrip trip,
		String name,
		double velocity,
		GpsSensor gpsSensor,
		boolean gpsActivated) {
		Vehicle<TruckData> truck = null;
		TrafficTrip tmpTrip = trip;

		List<TrafficEdge> path = this.getResponsibleServer().getShortestPath(
			tmpTrip.getStartNode(),
			tmpTrip.getTargetNode());

		// check if path could not be computed between the nodes
		if (path != null) {
			truck = this.getResponsibleServer().getTruckFactory().createRandomTruck(
				getOutput());
			if (name != null) {
				truck.setName(name);
			}
			truck.setVelocity(velocity);
			truck.setPath(path);
			if(gpsSensor != null) {
				truck.setGpsSensor(gpsSensor);
			}
			if(gpsActivated && gpsSensor != null) {
				truck.getGpsSensor().setActivated(true);
			}
		}
		return truck;
	}
}
