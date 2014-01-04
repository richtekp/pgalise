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
 
package de.pgalise.simulation.traffic.internal.server.jam;

import java.util.Random;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.jam.SurroundingCarsFinder;
import de.pgalise.simulation.traffic.server.jam.TrafficJamModel;
import de.pgalise.simulation.shared.city.Vector2d;

/**
 * Implementation of the Nagel-Schreckenberg model Follows the Nagel-Schreckenberg model to update the given list of
 * vehicles. On each update call the following four rules will be applied:<br/>
 * <br/>
 * 1. Acceleration: All cars not at the maximum velocity have their velocity increased by one unit. For example, if the
 * velocity is 4 it is increased to 5.<br/>
 * <br/>
 * 2. Slowing down: All cars are checked to see if the distance between it and the car in front (in units of cells) is
 * smaller than its current velocity (which has units of cells per time step). If the distance is smaller than the
 * velocity, the velocity is reduced to the number of empty cells in front of the car - to avoid a collision. For
 * example, if the velocity of a car is now 5, but there are only 3 free cells in front of it, with the fourth cell
 * occupied by another car, the car velocity is reduced to 3.<br/>
 * <br/>
 * 3. Randomization: The speed of all cars that have a velocity of at least 1, is now reduced by one unit with a
 * probability of p. For example, if p = 0.5, then if the velocity is 4, it is reduced to 3 50% of the time.<br/>
 * <br/>
 * 4. Car motion: Finally, all cars are moved forward the number of cells equal to their velocity. For example, if the
 * velocity is 3, the car is moved forward 3 cells.
 * 
 * @param vehicles
 *            List of the driving cars
 * @param time
 *            Current simulation time
 * @param maxVelocity
 *            Maximal velocity
 * @return New position of the updated car
 * @author Marina, Mustafa
 */
public class DefaultNaSchModel implements TrafficJamModel {
	private SurroundingCarsFinder finder;
	private Random random;
	private RandomSeedService seedService;
	private double bias;

	/**
	 * Constructor
	 * 
	 * @param mapper
	 *            GPS-Mapper
	 * @param seed
	 *            Random seed service
	 */
	public DefaultNaSchModel(RandomSeedService seed) {
		this.seedService = seed;
		this.random = new Random(this.seedService.getSeed(DefaultNaSchModel.class.getName()));
		this.bias = 0.5 ;
		this.finder = new AdvancedCarFinder();
	}

	@Override
	public void setSurroundingCarsFinder(SurroundingCarsFinder finder) {
		this.finder = finder;

	}

	@Override
	public void update(Vehicle<?> v, long time, TrafficGraph graph, double probability) {
		// log.debug("\n\n--- Calculating update behavior of vehicle " + v.getName() + " ---");

		// car length in mm
		double carLength = v.getData().getVehicleLength();
		if (carLength != 0) {
			carLength /= 1000;
			this.bias += (carLength / 2) ;
			// log.debug(v.getData().getType() + " length: " + carLength);
		}

		TrafficNode node = v.getCurrentNode();
		TrafficNode nextNode = v.getNextNode();
		TrafficEdge edge = null;
		if (nextNode != null) {
			edge = graph.getEdge(node, nextNode);
		}
		double maxSpeed = 0;
		if (edge != null) {
			maxSpeed = v.getTrafficGraphExtensions().getMaxSpeed(edge);
		}

		if (maxSpeed <= 0) {
			maxSpeed = 50;
		}

		// velocity of 1 = 27/kmh = 0.075vu/s
		double vel1 = 0.075;
		if (v.getVelocity() < maxSpeed) {
			v.setVelocity(((int) ((v.getVelocity() + vel1) * 1000)) / 1000.0d);
			// log.debug("Vehicle " + v.getName() + " accelerated to " + v.getVelocity());
		}

		Vehicle<? extends VehicleData> carAhead = this.finder.findNearestCar(v, time);
		if (carAhead != null) {
			// log.debug("Nearest vehicle to " + v.getName() + ": " + carAhead.getName());
			Vector2d carAheadPos = new Vector2d(carAhead.getPosition().getX(), carAhead.getPosition().getY());
			Vector2d vVector = new Vector2d(v.getPosition().getX(), v.getPosition().getY());
			carAheadPos.sub(vVector);

			double distance = ((int) (carAheadPos.length() * 1000) / 1000.0d);

			// log.debug("Distance to vehicle ahead of " + v.getName() + ": " + distance);
			// log.debug("Current velocity of " + v.getName() + ": " + v.getVelocity());
			double length = v.getVelocity() * (time / 1000d);
			if (length >= (distance)) {
				v.setVelocity(((distance - this.bias) < 0) ? 0 : ((distance - this.bias) / (time / 1000d)));
				// log.debug("Distance to the vehicle ahead is too small. Adjusted velocity of "
				// + v.getName() + " to: " + v.getVelocity());
			}
		}
		// else {
		// log.debug("No vehicle near " + v.getName());
		// }

		if (v.getVelocity() >= vel1) {
			if (this.random.nextDouble() <= probability) {
				v.setVelocity(v.getVelocity() - vel1);
				if (v.getVelocity() < 0) {
					v.setVelocity(0);
				}
				// log.debug("Vehicle " + v.getName()
				// + " hit the probability to reduce his velocity to " + v.getVelocity());
			}
		}
		v.update(time);
	}
}
