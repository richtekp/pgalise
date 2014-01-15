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
package de.pgalise.simulation.traffic.internal.server.route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.entity.TrafficTrip;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.server.route.RandomVehicleTripGenerator;

/**
 *
 *
 * @author Lena
 * @author Timo
 * @author Mischa
 */
public class DefaultRandomVehicleTripGenerator implements
	RandomVehicleTripGenerator {

	/**
	 * Date
	 */
	private Date date;

	/**
	 * Only nodes from one server
	 */
	private List<TrafficNode> startHomeNodes = new ArrayList<>();

	/**
	 * Only nodes from one server
	 */
	private List<TrafficNode> startWorkNodes = new ArrayList<>();

	/**
	 * All nodes from all servers
	 */
	private List<TrafficNode> homeNodes = new ArrayList<>();

	/**
	 * Random generator
	 */
	private Random random;

	/**
	 * Random seed service
	 */
	private RandomSeedService randomSeedService;

	/**
	 * Start node of the traffic trip
	 */
	private TrafficNode startNode;

	/**
	 * Start timestamp to start the drive
	 */
	private long startTimeWayThere;

	/**
	 * Target node of the traffic trip
	 */
	private TrafficNode targetNode;

	/**
	 * All work nodes from all servers
	 */
	private List<TrafficNode> workNodes = new ArrayList<>();

	/**
	 * Constructor
	 *
	 * @param timeInMs ...
	 * @param hn All nodes from all servers
	 * @param wn All work nodes from all servers
	 * @param seedService Random seed service
	 */
	public DefaultRandomVehicleTripGenerator(long timeInMs,
		List<TrafficNode> hn,
		List<TrafficNode> wn,
		RandomSeedService seedService) {
		this.randomSeedService = seedService;
		this.date = new Date(timeInMs);

		this.homeNodes = hn;
		this.workNodes = wn;
		// this.carModelList =
		// VehicleModelEnum.getVehicleModelsForTypeAsUnmodifiable(VehicleTypeEnum.CAR);
		// this.truckModelList =
		// VehicleModelEnum.getVehicleModelsForTypeAsUnmodifiable(VehicleTypeEnum.TRUCK);
		this.init();
	}

	/**
	 * Creates a trip for the given vehicle type.
	 *
	 * @param vehicleType Vehicle type
	 * @return TrafficTrip
	 */
	@Override
	public TrafficTrip createVehicleTrip(List<TrafficNode> startHomeNodes,
		List<TrafficNode> startWorkNodes,
		VehicleTypeEnum vehicleType,
		Date date,
		int buffer) {
		this.startHomeNodes = startHomeNodes;
		this.startWorkNodes = startWorkNodes;
		// log.debug("Anzahl startHomeNodes vs allHomeNodes: " +
		// startHomeNodes.size() + " : " + homeNodes.size());
		switch (vehicleType) {
			case TRUCK:
				return this.createTruckTrip(date,
					buffer);
			case BIKE:
				return this.createBikeTrip(date,
					buffer);
			default:
				return this.createCarTrip(date,
					buffer);
		}
	}

	/**
	 * Create bike trips
	 *
	 * @param date Date
	 * @param buffer Buffer
	 * @return TrafficTrip
	 */
	private TrafficTrip createBikeTrip(Date date,
		int buffer) {
		return this.createCarTrip(date,
			buffer);
	}

	/**
	 * Creates a random trip for a car.
	 *
	 * @param date Date
	 * @param buffer Buffer
	 * @return TrafficTrip
	 */
	private TrafficTrip createCarTrip(Date date,
		int buffer) {
		String weekday = new SimpleDateFormat("E").format(this.date);

		if (weekday.equals("Sa") || weekday.equals("So")) {
			this.createFreeTimeTrip(date,
				buffer);
		} else {
			int decisionMaker = this.random.nextInt(101);
			if (decisionMaker <= 60) {
				this.createWorkTrip(date,
					buffer);
			} else {
				this.createFreeTimeTrip(date,
					buffer);
			}
		}

		// log.debug(String.format("Created Trip (%s, %s)",
		// startNode.getAttribute("position"),
		// targetNode.getAttribute("position")));
		return new TrafficTrip(this.startNode,
			this.targetNode,
			this.startTimeWayThere);// ,this.startTimeWayBack);
	}

	/**
	 * Creates a random trip for a truck.
	 *
	 * @param date Date
	 * @param buffer Buffer
	 * @return TrafficTrip
	 */
	private TrafficTrip createTruckTrip(Date date,
		int buffer) {
		// Amount of trucks just depends on fuzzy. Every trip is a work trip
		this.createTruckWorkTrip(date,
			buffer);

		return new TrafficTrip(this.startNode,
			this.targetNode,
			this.startTimeWayThere);// ,this.startTimeWayBack);
	}

	public List<TrafficNode> getHomeNodes() {
		return this.homeNodes;
	}

	public RandomSeedService getRandomSeedService() {
		return this.randomSeedService;
	}

	public List<TrafficNode> getWorkNodes() {
		return this.workNodes;
	}

	@Override
	public void init() {
		this.random = new Random(
			this.randomSeedService.getSeed(DefaultRandomVehicleTripGenerator.class
				.getName()));
	}

	public void setHomeNodes(List<TrafficNode> homeNodes) {
		this.homeNodes = homeNodes;
	}

	public void setRandomSeedService(RandomSeedService randomSeedService) {
		this.randomSeedService = randomSeedService;
	}

	public void setWorkNodes(List<TrafficNode> workNodes) {
		this.workNodes = workNodes;
	}

	/**
	 * ...
	 *
	 * @param date
	 * @param buffer
	 */
	@SuppressWarnings({"deprecation"})
	private void createFreeTimeTrip(Date date,
		int buffer) {
		this.startNode = this.startHomeNodes.get(this.random
			.nextInt(this.startHomeNodes.size()));
		this.targetNode = this.homeNodes.get(this.random.nextInt(this.homeNodes
			.size()));
		if (date == null) {
			int tempHour = this.random.nextInt(24);
			int minute = this.random.nextInt(60);

			this.date.setHours(tempHour);
			this.date.setMinutes(minute);
			this.date.setSeconds(0);

			this.startTimeWayThere = this.date.getTime();
		} else {
			int variance = this.random.nextInt(buffer + 1) + date.getMinutes();
			int tmpHour = (int) Math.ceil(variance / 60);

			this.date.setHours(date.getHours() + tmpHour);
			this.date.setMinutes(variance % 60);
			this.date.setSeconds(0);

			this.startTimeWayThere = this.date.getTime();
		}
	}

	/**
	 * ...
	 *
	 * @param date
	 * @param buffer
	 */
	@SuppressWarnings("deprecation")
	private void createWorkTrip(Date date,
		int buffer) {
		// this.startNode =
		// this.startHomeNodes.get(this.random.nextInt(this.startHomeNodes.size()));
		// this.targetNode =
		// this.workNodes.get(this.random.nextInt(this.workNodes.size()));
		if (date == null) {
			int decisionMaker = this.random.nextInt(101);
			if (decisionMaker <= 50) {
				int variance = this.random.nextInt(121 + 120) - 120;

				int tmpHour = (int) (Math.ceil(variance / 60.0));

				this.date.setHours(8 + tmpHour);
				this.date.setMinutes(variance % 60);
				this.date.setSeconds(0);

				this.startTimeWayThere = this.date.getTime();

				this.startNode = this.startHomeNodes.get(this.random
					.nextInt(this.startHomeNodes.size()));
				this.targetNode = this.workNodes.get(this.random
					.nextInt(this.workNodes.size()));
			} else {
				int variance = this.random.nextInt(121 + 120) - 120;

				int tmpHour = (int) (Math.ceil(variance / 60.0));

				this.date.setHours(16 + tmpHour);
				this.date.setMinutes(variance % 60);
				this.date.setSeconds(0);

				this.startTimeWayThere = this.date.getTime();

				this.startNode = this.startWorkNodes.get(this.random
					.nextInt(this.startWorkNodes.size()));
				this.targetNode = this.homeNodes.get(this.random
					.nextInt(this.homeNodes.size()));

			}
		} else {
			int variance = this.random.nextInt(buffer + 1) + date.getMinutes();

			int tmpHour = (int) (Math.ceil(variance / 60.0));

			this.date.setHours(date.getHours() + tmpHour);
			this.date.setMinutes(variance % 60);
			this.date.setSeconds(0);

			this.startTimeWayThere = this.date.getTime();
		}
	}

	/**
	 * ...
	 *
	 * @param date
	 * @param buffer
	 */
	private void createTruckWorkTrip(Date date,
		int buffer) {
		createFreeTimeTrip(date,
			buffer); // Trucks are driving from 0-24 Uhr
	}

	// // log.debug("WorkTrip am " + cal.get(Calendar.DATE) + "." +
	// // (cal.get(Calendar.MONTH) + 1) + "." +
	// // cal.get(Calendar.YEAR));
	//
	// this.startNode =
	// this.workNodes.get(this.random.nextInt(this.workNodes.size()));
	// this.targetNode =
	// this.workNodes.get(this.random.nextInt(this.workNodes.size()));
	// if ((date == null) && (buffer == -1)) {
	// this.cal.set(Calendar.HOUR_OF_DAY, 8);
	// this.cal.set(Calendar.MINUTE, 0);
	// this.cal.set(Calendar.SECOND, 0);
	// this.cal.set(Calendar.MILLISECOND, 0);
	//
	// int variance = this.random.nextInt(121 + 120) - 120;
	// this.cal.add(Calendar.MINUTE, variance);
	// this.startTimeWayThere = this.cal.getTimeInMillis();
	//
	// // log.debug("Startzeit " + cal.get(Calendar.HOUR) + ":"
	// // + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) +
	// // ", Startknoten: " + startNode);
	//
	// this.cal.set(Calendar.HOUR_OF_DAY, 16);
	// this.cal.set(Calendar.MINUTE, 0);
	// this.cal.set(Calendar.SECOND, 0);
	// this.cal.set(Calendar.MILLISECOND, 0);
	//
	// variance = this.random.nextInt(121 + 120) - 120;
	// this.cal.add(Calendar.MINUTE, variance);
	// this.startTimeWayBack = this.cal.getTimeInMillis();
	// } else {
	// // ggf. nicht +1
	// this.cal.set(Calendar.HOUR_OF_DAY, date.getHours() + 1);
	// this.cal.set(Calendar.MINUTE, 0);
	// this.cal.set(Calendar.SECOND, 0);
	// this.cal.set(Calendar.MILLISECOND, 0);
	// int minute = this.random.nextInt(buffer) + date.getMinutes();
	// this.cal.add(Calendar.MINUTE, minute);
	// this.startTimeWayThere = this.cal.getTimeInMillis();
	//
	// this.cal.set(Calendar.HOUR_OF_DAY, date.getHours() + 9);
	// this.cal.set(Calendar.MINUTE, 0);
	// this.cal.set(Calendar.SECOND, 0);
	// this.cal.set(Calendar.MILLISECOND, 0);
	// minute = this.random.nextInt(buffer);
	// this.cal.add(Calendar.MINUTE, minute);
	// this.startTimeWayBack = this.cal.getTimeInMillis();
	// }
	// // log.debug("Endzeit " + cal.get(Calendar.HOUR_OF_DAY) + ":"
	// // + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) +
	// // ", Zielknoten: " + targetNode);
	// }
	/**
	 * Create a traffic trip with given target node ID and time
	 *
	 * @param startHomeNodes List with start home nodes
	 * @param targetNode Target node ID
	 * @param startTimestamp startTimeWayThere
	 * @return TrafficTrip
	 */
	@Override
	public TrafficTrip createVehicleTrip(List<TrafficNode> startHomeNodes,
		TrafficNode targetNode,
		long startTimestamp) {
		this.startHomeNodes = startHomeNodes;

		// Calculate start node
		this.startNode = this.startHomeNodes.get(this.random
			.nextInt(this.startHomeNodes.size()));

		// Set target node
		return new TrafficTrip(this.startNode,
			targetNode,
			startTimestamp);
	}

	/**
	 * Create a traffic trip with given start node ID and time
	 *
	 * @param homeNodes List with target home nodes
	 * @param startNode Start node ID
	 * @param startTimestamp startTimeWayThere
	 * @return TrafficTrip
	 */
	@Override
	public TrafficTrip createVehicleTrip(TrafficNode startNode,
		List<TrafficNode> homeNodes,
		long startTimestamp) {
		this.homeNodes = homeNodes;

		// Calculate target node
		this.targetNode = this.homeNodes.get(this.random.nextInt(this.homeNodes
			.size()));

		// Set target node
		return new TrafficTrip(startNode,
			this.targetNode,
			startTimestamp);
	}
}
