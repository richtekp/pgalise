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
 
package de.pgalise.simulation.traffic.internal.server.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleCallback;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleData;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * Class for roundabouts
 * 
 * @author Marcus
 * @version 1.0 (Oct 28, 2012)
 */
public class Roundabout extends TrafficRule {

	/**
	 * 
	 */
	private final TrafficGraphExtensions trafficGraphExtensions;

	/**
	 * a {@link Random} instance which seed is returned by this {@link Roundabout}'s {@link RandomSeedService}
	 */
	private final Random random;

	/**
	 * milliseconds a {@link Vehicle} would need for a complete circumnavigation
	 */
	private final int millisPerRound;

	/**
	 * the maximal number of {@link Vehicle}s that can be at the same time in the {@link Roundabout}
	 */
	private final int maxNumberOfVehicles;

	/**
	 * maps all the milliseconds for passing the roundabout from each {@link Edge} to each {@link Edge}
	 */
	private final Map<NavigationEdge<?,?>, Map<NavigationEdge<?,?>, Integer>> edgeToEdgeTimes = new HashMap<>();

	/**
	 * collects {@link Edge}s where {@link Vehicle}s that have to wait.
	 */
	private final Map<NavigationEdge<?,?>, Queue<TrafficRuleData>> vehiclesWaiting = new HashMap<>();

	/**
	 * collects {@link Vehicle}s that are currently in the {@link Roundabout}
	 */
	private final Map<TrafficRuleData, Integer> vehiclesInRoundabout = new HashMap<>();

	/**
	 * Creates a {@link Roundabout} for the passed {@link Node}.
	 * 
	 * @param node
	 *            the {@link Node} on which the {@link Roundabout} will be applied
	 * @param randomSeedService
	 *            the {@link RandomSeedService} for generating replicable random numbers for letting waiting
	 *            {@link Vehicle}s in
	 * @param millisPerRound
	 *            the milliseconds a {@link Vehicle} needs for a whole round in the {@link Roundabout}
	 * @param maxNumberOfVehicles
	 *            the maximal number of {@link Vehicle}s that can be at the same time in the {@link Roundabout}
	 * @param simulationTime
	 *            the current time of the simulation
	 * @throws IllegalArgumentException
	 *             if argument 'node' is null or has no edge <br>
	 *             if argument 'simulationTime' is a negative number
	 */
	public Roundabout(final NavigationNode node, TrafficGraph<?> graph, final RandomSeedService randomSeedService,
			final TrafficGraphExtensions trafficGraphExtensions, final int millisPerRound,
			final int maxNumberOfVehicles, final long simulationTime) throws IllegalArgumentException {
		super(node, graph);
		if (randomSeedService == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("randomSeedService"));
		}
		if (trafficGraphExtensions == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("trafficGraphExtensions"));
		}
		if (millisPerRound <= 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("millisPerRound", false));
		}
		if (maxNumberOfVehicles <= 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("numberOfMaxVehicles", false));
		}
		if (simulationTime < 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("simulationStartTime", true));
		}
		this.millisPerRound = millisPerRound;
		this.maxNumberOfVehicles = maxNumberOfVehicles;
		this.previousTime = simulationTime;

		for (final NavigationEdge<?,?> edge : getGraph().edgesOf(this.getNode())) {
			this.vehiclesWaiting.put(edge, new LinkedList<TrafficRuleData>());
		}

		this.random = new Random(randomSeedService.getSeed(Roundabout.class.getName()));
		this.trafficGraphExtensions = trafficGraphExtensions;

		this.calculateTimes();
	}

	/**
	 * Creates a {@link Roundabout} for the passed {@link Node}. 'getMillisPerRound' will return 4000 and
	 * 'getNumberOfMaxVehicles' will return 5.
	 * 
	 * @param node
	 *            the {@link Node} on which the {@link Roundabout} will be applied
	 * @param randomSeedService
	 *            the {@link RandomSeedService} for generating replicable random numbers
	 * @param simulationTime
	 *            the current time of the simulation
	 * @throws IllegalArgumentException
	 *             if argument 'node' is null or has no edge <br>
	 *             if argument 'simulationTime' is a negative number
	 */
	public Roundabout(final NavigationNode node, TrafficGraph<?> graph, final RandomSeedService randomSeedService,
			final TrafficGraphExtensions trafficGraphExtensions, final long simulationTime)
			throws IllegalArgumentException {
		this(node, graph, randomSeedService, trafficGraphExtensions, 40000, 5, simulationTime);
	}

	@Override
	protected void checkNode(final NavigationNode node) throws IllegalArgumentException {
		if (getGraph().edgesOf(node).isEmpty()) {
			throw new IllegalArgumentException("Argument 'node' must have at least one edge.");
		}
	}

	/**
	 * Calculates the times in millisecond for each {@link Edge}e needed for getting from it to another. <br>
	 * Calculated values will be written to 'edgeToEdgeTimes' map.
	 */
	private void calculateTimes() {
		for (final NavigationEdge<?,?> from : getGraph().edgesOf(this.getNode())) {
			final HashMap<NavigationEdge<?,?>, Integer> map = new HashMap<>();
			for (final NavigationEdge<?,?> to : this.getNode()) {
				map.put(to, this.calculateTime(from, to));
			}
			this.edgeToEdgeTimes.put(from, map);
		}
	}

	/**
	 * Calculates the time in milliseconds for getting from one {@link Edge} to the other.
	 * 
	 * @param from
	 *            the starting {@link Edge}
	 * @param to
	 *            the target {@link Edge}
	 * @return the time in milliseconds for getting from {@link Edge} 'from' to {@link Edge} 'to'
	 */
	private int calculateTime(final NavigationEdge<?,?> from, final NavigationEdge<?,?> to) {
		final NavigationNode nodeFrom = from.getSource()!= this.getNode() ? from.getSource(): from.getTarget();
		final NavigationNode nodeTo = to.getSource() != this.getNode() ? to.getSource() : to.getTarget();

		double angle = (this.trafficGraphExtensions.getVectorBetween(this.getNode(), nodeFrom).angle(
				this.trafficGraphExtensions.getVectorBetween(this.getNode(), nodeTo)) * 180D)
				/ Math.PI;

		final Vector2d posNodeFrom = this.trafficGraphExtensions.getVectorBetween(this.getNode(), nodeFrom);
		final Vector2d posNodeTo = this.trafficGraphExtensions.getVectorBetween(this.getNode(), nodeTo);

		final Vector3d vector3dCross = new Vector3d(posNodeFrom.x, posNodeFrom.y, 0);
		vector3dCross.cross(vector3dCross,
				new Vector3d(posNodeTo.x, posNodeTo.y, 0));

		if (vector3dCross.z >= 0) {
			angle = 360 - angle;
		}
		return (int) ((angle / 360D) * this.getMillisPerRound());
	}

	/**
	 * Returns the time in milliseconds for getting from one {@link Edge} to the other.
	 * 
	 * @param from
	 *            the starting {@link Edge}
	 * @param to
	 *            the target {@link Edge}
	 * @return the time in milliseconds for getting from 'from' to 'to'
	 * @throws IllegalArgumentException
	 *             if argument 'from' or argument 'node' is null.
	 * @throws IllegalStateException
	 *             if argument 'from' or argument 'to' isn't linked with this roundabout's node
	 */
	public int getMillis(final NavigationEdge<?,?> from, final NavigationEdge<?,?> to) throws IllegalArgumentException, IllegalStateException {
		if (from == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("from"));
		}
		if (to == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("to"));
		}
		final Map<NavigationEdge<?,?>, Integer> map = this.edgeToEdgeTimes.get(from);
		if (map == null) {
			throw new IllegalStateException("Argument 'from' isn't linked with this roundabout's node");
		}
		final Integer result = map.get(to);
		if (result == null) {
			throw new IllegalStateException("Argument 'to' isn't linked with this roundabout's node");
		}
		return result;
	}

	/**
	 * Registers a {@link Vehicle} at this {@link Roundabout}. <br>
	 * If there are less {@link Vehicle}s in the {@link Roundabout} than 'getNumberOfMaxVehicles' <br>
	 * the {@link Vehicle} to register will be put in a waiting queue.
	 * 
	 * @param vehicle
	 *            the vehicle to register at this {@link Roundabout}
	 * @param from
	 *            the {@link Edge} the {@link Vehicle} is coming from
	 * @param to
	 *            the {@link Edge} the {@link Vehicle} wants to follow
	 * @param callback
	 *            the {@link TrafficRuleCallback} that methods are executed on certain events
	 * @throws IllegalArgumentException
	 *             if at least one of the passed arguments is 'null'
	 * @throws IllegalStateException
	 *             if {@link Edge} 'from' or {@link Edge} 'to' aren't linked with this {@link Roundabout}'s {@link Node}
	 */
	@Override
	public void register(final Vehicle<?> vehicle, final NavigationEdge<?,?> from, final NavigationEdge<?,?> to,
			final TrafficRuleCallback callback) throws IllegalArgumentException {
		final TrafficRuleData trafficRuleData = new TrafficRuleData(vehicle, from, to, callback);
		if (!this.vehiclesWaiting.containsKey(trafficRuleData.getFrom())) {
			throw new IllegalStateException("Edge 'from' isn't linked with this Roundabout's node.");
		}
		if (!this.vehiclesWaiting.containsKey(trafficRuleData.getTo())) {
			throw new IllegalStateException("Edge 'to' isn't linked with this Roundabout's node.");
		}
		if (this.vehiclesInRoundabout.size() < this.getMaxNumberOfVehicles()) {
			// vehicle can enter the roundabout
			if (trafficRuleData.getCallback().onEnter()) {
				this.vehiclesInRoundabout.put(trafficRuleData,
						this.getMillis(trafficRuleData.getFrom(), trafficRuleData.getTo()));
			} else {
				// vehicle has to wait
				if (!this.vehiclesWaiting.get(trafficRuleData.getFrom()).offer(trafficRuleData)) {
					throw new RuntimeException("Vehicle couldn't be offered to waiting queue.");
				}
			}
		} else {
			// vehicle has to wait
			if (!this.vehiclesWaiting.get(trafficRuleData.getFrom()).offer(trafficRuleData)) {
				throw new RuntimeException("Vehicle couldn't be offered to waiting queue.");
			}
		}
	}

	/**
	 * Saves the the last time the simulation was updated
	 */
	private long previousTime;

	/**
	 * Updates the {@link Roundabout} by first throwing {@link Vehicle}s out <br>
	 * that time has exceeded and then let waiting {@link Vehicle}s in.
	 * 
	 * @param simulationEventList
	 *            the {@link SimulationEventList}
	 * @throws UpdateException
	 *             not thrown in here
	 */
	@Override
	public void update(final EventList<Event> simulationEventList) {
		final int timeDif = (int) (simulationEventList.getTimestamp() - this.previousTime);
		this.previousTime = simulationEventList.getTimestamp();

		// Throw out vehicles
		for (final TrafficRuleData trafficRuleData : new HashSet<>(this.vehiclesInRoundabout.keySet())) {
			this.vehiclesInRoundabout.put(trafficRuleData, this.vehiclesInRoundabout.get(trafficRuleData) - timeDif);

			if (this.vehiclesInRoundabout.get(trafficRuleData) <= 0) {
				if (trafficRuleData.getCallback().onExit()) {
					this.vehiclesInRoundabout.remove(trafficRuleData);
				}
			}
		}

		// Let vehicles randomly in
		final ArrayList<Queue<TrafficRuleData>> queues = new ArrayList<>();
		while ((this.vehiclesInRoundabout.size() < this.getMaxNumberOfVehicles()) && this.fillQueueList(queues)) {
			// Dequeue waiting cars
			final Queue<TrafficRuleData> queue = queues.get(this.random.nextInt(queues.size()));
			final TrafficRuleData trafficRuleData = queue.peek();
			if (trafficRuleData.getCallback().onEnter()) {
				this.vehiclesInRoundabout.put(queue.poll(),
						this.getMillis(trafficRuleData.getFrom(), trafficRuleData.getTo()));
			} else {
				// vehicle has to wait
				if (!this.vehiclesWaiting.get(trafficRuleData.getFrom()).offer(trafficRuleData)) {
					throw new RuntimeException("Vehicle couldn't be offered to waiting queue.");
				}
			}
		}
	}

	/**
	 * Fills the passed {@link List} with {@link Queue}s of the waiting {@link Vehicle}s. <br>
	 * Returns true if at least one {@link Queue} has been added to the passed {@link List}. <br>
	 * The passed {@link List} is initially cleared before adding the {@link Queue}s.
	 * 
	 * @param queues
	 * @return
	 */
	private boolean fillQueueList(final List<Queue<TrafficRuleData>> queues) {
		queues.clear();
		boolean result = false;
		for (final Queue<TrafficRuleData> value : this.vehiclesWaiting.values()) {
			if (value.size() > 0) {
				queues.add(value);
				result = true;
			}
		}
		return result;
	}

	/**
	 * Returns the number of all {@link Vehicle}s that are currently waiting for getting in the {@link Roundabout}.
	 * 
	 * @return the number of all {@link Vehicle}s that are currently waiting for getting in the {@link Roundabout}
	 */
	public int getNumberOfVehiclesWaiting() {
		int result = 0;
		for (final NavigationEdge<?,?> key : this.vehiclesWaiting.keySet()) {
			result += this.getNumberOfVehiclesWaiting(key);
		}
		return result;
	}

	/**
	 * Returns the number of {@link Vehicle}s that are currently waiting at the passed {@link Edge} for getting in the
	 * {@link Roundabout}.
	 * 
	 * @param from
	 *            the {@link Edge} where to get the number of waiting {@link Vehicle}s.
	 * @return the number of {@link Vehicle}s that are currently waiting at the passed {@link Edge} for getting in the
	 *         {@link Roundabout}
	 * @throws IllegalArgumentException
	 *             if argument 'edge' is 'null'
	 * @throws IllegalStateException
	 *             if the passed {@link Edge} isn't linked with this {@link Roundabout}'s {@link Node}
	 */
	public int getNumberOfVehiclesWaiting(final NavigationEdge<?,?> from) throws IllegalArgumentException, IllegalStateException {
		if (from == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("edge"));
		}
		final Queue<TrafficRuleData> queue = this.vehiclesWaiting.get(from);
		if (queue == null) {
			throw new IllegalStateException("Argument 'edge' isn't linked with this Roundabout's node.");
		}
		return queue.size();
	}

	/**
	 * Returns the time in milliseconds a {@link Vehicle} needs for one round in the {@link Roundabout}.
	 * 
	 * @return the time in milliseconds a {@link Vehicle} needs for one round in the {@link Roundabout}
	 */
	public int getMillisPerRound() {
		return this.millisPerRound;
	}

	/**
	 * Returns the maximal number of {@link Vehicle}s that can be in the {@link Roundabout} at the same time.
	 * 
	 * @return the maximal number of {@link Vehicle}s that can be in the {@link Roundabout} at the same time
	 */
	public int getMaxNumberOfVehicles() {
		return this.maxNumberOfVehicles;
	}

	/**
	 * Returns the number of {@link Vehicle}s that are currently in the {@link Roundabout}.
	 * 
	 * @return the number of {@link Vehicle}s that are currently in the {@link Roundabout}
	 */
	public int getNumbersOfVehiclesInRoundabout() {
		return this.vehiclesInRoundabout.size();
	}
}
