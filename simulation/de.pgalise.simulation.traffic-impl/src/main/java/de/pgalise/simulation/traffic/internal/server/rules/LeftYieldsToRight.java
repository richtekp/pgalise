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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleCallback;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * Class for left yields to right
 * 
 * @author Marcus
 */
public class LeftYieldsToRight extends TrafficRule {

	/**
	 * maps the right {@link Edge} for each {@link Edge}
	 */
	private final Map<Edge, Edge> rights = new HashMap<>();

	/**
	 * maps the straight {@link Edge} for each {@link Edge}
	 */
	private final Map<Edge, Edge> straights = new HashMap<>();

	/**
	 * maps the left {@link Edge} for each {@link Edge}
	 */
	private final Map<Edge, Edge> lefts = new HashMap<>();

	/**
	 * maps to each {@link Edge} a {@link Queue} with waiting {@link Vehicle}s
	 */
	private final Map<Edge, Queue<TrafficRuleData>> waiting = new HashMap<>();

	/**
	 * a {@link Random} instance which seed is returned by this {@link LeftYieldsToRight}'s {@link RandomSeedService}
	 */
	private final Random random;

	/**
	 * 
	 */
	private final TrafficGraphExtensions trafficGraphExtensions;

	/**
	 * Creates a {@link LeftYieldsToRight} with the passed arguments.
	 * 
	 * @param node
	 *            the node on which this {@link LeftYieldsToRight} is applied
	 * @throws IllegalArgumentException
	 *             if argument 'node' or argument 'randomSeedService' is null
	 * @throws IllegalStateException
	 *             if the passed {@link Node} doesn't have 3 or 4 {@link Edge}s
	 */
	public LeftYieldsToRight(final Node node, final RandomSeedService randomSeedService,
			final TrafficGraphExtensions trafficGraphExtensions) throws IllegalArgumentException, IllegalStateException {
		super(node);
		if (randomSeedService == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("randomSeedService"));
		}
		if (trafficGraphExtensions == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("trafficGraphExtensions"));
		}
		this.random = new Random(randomSeedService.getSeed(LeftYieldsToRight.class.getName()));
		this.trafficGraphExtensions = trafficGraphExtensions;
		this.setUp();
	}

	@Override
	protected void checkNode(final Node node) throws IllegalStateException {
		final int edgeSize = node.getEdgeSet().size();
		if ((edgeSize < 3) || (edgeSize > 4)) {
			throw new IllegalStateException(
					ExceptionMessages.getMessageForMustBetween("node.getEdgeSet().size()", 3, 5));
		}
	}

	/**
	 * Sets up the instance members
	 */
	private void setUp() {
		if (this.getNode().getEdgeSet().size() == 3) {
			double maxAngle = Double.MIN_VALUE;
			Edge straight1 = null;
			Edge straight2 = null;
			for (final Edge from : this.getNode()) {
				for (final Edge to : this.getNode()) {
					final double angle = this.calculateAngle(from, to);
					if (angle > maxAngle) {
						maxAngle = angle;
						straight1 = from;
						straight2 = to;
					}
				}
			}
			Edge nonStraight = null;
			for (final Edge edge : this.getNode()) {
				if ((edge != straight1) && (edge != straight2)) {
					nonStraight = edge;
					break;
				}
			}
			this.straights.put(straight1, straight2);
			this.straights.put(straight2, straight1);

			if (this.calculateLeftAngle(nonStraight, straight1) <= 180) {
				this.rights.put(nonStraight, straight1);
				this.rights.put(straight2, nonStraight);
				this.lefts.put(straight1, nonStraight);
				this.lefts.put(nonStraight, straight2);
			} else {
				this.rights.put(straight1, nonStraight);
				this.rights.put(nonStraight, straight2);
				this.lefts.put(nonStraight, straight1);
				this.lefts.put(straight2, nonStraight);
			}

		} else {
			for (final Edge from : this.getNode()) {
				final Map<Double, Edge> angles = new HashMap<>();
				for (final Edge to : this.getNode()) {
					final double angle = this.calculateLeftAngle(from, to);
					angles.put(angle, to);
				}
				final List<Double> sortedAngles = new ArrayList<>(angles.keySet());
				Collections.sort(sortedAngles);
				this.rights.put(from, angles.get(sortedAngles.get(0)));
				this.straights.put(from, angles.get(sortedAngles.get(1)));
				this.lefts.put(from, angles.get(sortedAngles.get(2)));
			}
		}
		for (final Edge edge : this.getNode()) {
			this.waiting.put(edge, new LinkedList<TrafficRuleData>());
		}
	}

	/**
	 * Calculates the angle between the to passed {@link Edge}s.
	 * 
	 * @param from
	 *            the first {@link Edge}
	 * @param to
	 *            the second {@link Edge}
	 * @return the angle between 'from' and 'to' always smaller than 180
	 */
	private double calculateAngle(final Edge from, final Edge to) {
		final Node nodeFrom = from.getNode0() != this.getNode() ? from.getNode0() : from.getNode1();
		final Node nodeTo = to.getNode0() != this.getNode() ? to.getNode0() : to.getNode1();

		return (this.trafficGraphExtensions.getVectorBetween(this.getNode(), nodeFrom).angle(
				this.trafficGraphExtensions.getVectorBetween(this.getNode(), nodeTo)) * 180D)
				/ Math.PI;
	}

	/**
	 * Calculates the counter-clockwise angle between the to passed {@link Edge} s.
	 * 
	 * @param from
	 *            the first {@link Edge}
	 * @param to
	 *            the second {@link Edge}
	 * @return the counter-clockwise angle between 'from' and 'to'
	 */
	private double calculateLeftAngle(final Edge from, final Edge to) {
		final Node nodeFrom = from.getNode0() != this.getNode() ? from.getNode0() : from.getNode1();
		final Node nodeTo = to.getNode0() != this.getNode() ? to.getNode0() : to.getNode1();

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
		return angle;
	}

	/**
	 * Registers a {@link Vehicle} at this {@link LeftYieldsToRight}. <br>
	 * 
	 * @param vehicle
	 *            the vehicle to register at this {@link LeftYieldsToRight}
	 * @param from
	 *            the {@link Edge} the {@link Vehicle} is coming from
	 * @param to
	 *            the {@link Edge} the {@link Vehicle} wants to follow
	 * @param callback
	 *            the {@link TrafficRuleCallback} that methods are executed on certain events
	 * @throws IllegalArgumentException
	 *             if at least one of the passed arguments is 'null'
	 * @throws UnsupportedOperationException
	 *             if {@link Edge} 'from' or {@link Edge} 'to' aren't linked with this {@link Roundabout}'s {@link Node}
	 *             or if both {@link Edge}s are the same object
	 */
	@Override
	public void register(final Vehicle<? extends VehicleData> vehicle, final Edge from, final Edge to,
			final TrafficRuleCallback callback) throws IllegalArgumentException, UnsupportedOperationException {
		if (!this.waiting.containsKey(from)) {
			throw new UnsupportedOperationException("Edge 'from' isn't linked with this LeftYieldsToRight's node.");
		}
		if (!this.waiting.containsKey(to)) {
			throw new UnsupportedOperationException("Edge 'to' isn't linked with this LeftYieldsToRight's node.");
		}
		if (from == to) {
			throw new UnsupportedOperationException("Edge 'from' and Edge 'to' must not be the same object.");
		}
		final TrafficRuleData trafficRuleData = new TrafficRuleData(vehicle, from, to, callback);
		this.waiting.get(trafficRuleData.getFrom()).offer(trafficRuleData);
	}

	/**
	 * Updates the {@link LeftYieldsToRight} by letting {@link Vehicle}s through that are allowed to pass <br>
	 * 
	 * @param simulationEventList
	 *            the {@link SimulationEventList}
	 * @throws UpdateException
	 *             not thrown in here
	 */
	@Override
	public void update(final EventList simulationEventList) {
		final Set<Edge> movedTrafficRuleDatas = new HashSet<>();
		for (final Edge edge : this.waiting.keySet()) {
			final TrafficRuleData trafficRuleData = this.waiting.get(edge).peek();
			if (trafficRuleData != null) {
				if (this.rights.get(trafficRuleData.getFrom()) == trafficRuleData.getTo()) {
					// Vehicle wants to turn right
					movedTrafficRuleDatas.add(edge);
				} else if (this.straights.get(trafficRuleData.getFrom()) == trafficRuleData.getTo()) {
					// Vehicle wants to go straight
					final Queue<TrafficRuleData> rightEdgeWaiting = this.waiting.get(this.rights.get(trafficRuleData
							.getFrom()));
					TrafficRuleData rightTrafficRuleData = null;
					if (rightEdgeWaiting != null) {
						rightTrafficRuleData = rightEdgeWaiting.peek();
					}
					if (rightTrafficRuleData == null) {
						movedTrafficRuleDatas.add(edge);
					}
				} else if (this.lefts.get(trafficRuleData.getFrom()) == trafficRuleData.getTo()) {
					// Vehicle wants to turn right
					final Queue<TrafficRuleData> rightEdgeWaiting = this.waiting.get(this.rights.get(trafficRuleData
							.getFrom()));
					TrafficRuleData rightTrafficRuleData = null;
					if (rightEdgeWaiting != null) {
						rightTrafficRuleData = rightEdgeWaiting.peek();
					}
					final Queue<TrafficRuleData> straightEdgeWaiting = this.waiting.get(this.straights
							.get(trafficRuleData.getFrom()));
					TrafficRuleData straightTrafficRuleData = null;
					if (straightEdgeWaiting != null) {
						straightTrafficRuleData = straightEdgeWaiting.peek();
					}

					if (((rightTrafficRuleData == null) || (this.rights.get(rightTrafficRuleData.getFrom()) == rightTrafficRuleData
							.getTo()))
							&& ((straightTrafficRuleData == null) || (this.lefts.get(straightTrafficRuleData.getFrom()) == straightTrafficRuleData
									.getTo()))) {

						movedTrafficRuleDatas.add(edge);
					}
				}
			}
		}
		if (movedTrafficRuleDatas.isEmpty() && (this.getNumberOfWaitingVehicles() > 0)) {
			// choose one randomly
			final List<Queue<TrafficRuleData>> allQueues = new ArrayList<>(this.waiting.values());
			Collections.sort(allQueues, new Comparator<Queue<TrafficRuleData>>() {
				@Override
				public int compare(Queue<TrafficRuleData> o1, Queue<TrafficRuleData> o2) {
					return o1.size() - o2.size();
				}
			});
			int to = allQueues.size();
			for (int i = 0; i < allQueues.size(); i++) {
				if (allQueues.get(i).size() == 0) {
					to = i;
					break;
				}
			}
			final Queue<TrafficRuleData> queue = allQueues.get(this.random.nextInt(to));
			final TrafficRuleData trafficRuleData = queue.peek();
			if (trafficRuleData.getCallback().onEnter()) {
				trafficRuleData.getCallback().onExit();
				queue.poll();
			}
		}
		for (final Edge edge : movedTrafficRuleDatas) {
			final Queue<TrafficRuleData> queue = this.waiting.get(edge);
			final TrafficRuleData trafficRuleData = queue.peek();
			if (trafficRuleData.getCallback().onEnter()) {
				trafficRuleData.getCallback().onExit();
				queue.poll();
			}
		}
	}

	/**
	 * Returns the number of {@link Vehicle}s waiting to get through.
	 * 
	 * @return the number of {@link Vehicle}s waiting to get through
	 */
	public int getNumberOfWaitingVehicles() {
		int result = 0;
		for (final Queue<TrafficRuleData> queue : this.waiting.values()) {
			result += queue.size();
		}
		return result;
	}

}
