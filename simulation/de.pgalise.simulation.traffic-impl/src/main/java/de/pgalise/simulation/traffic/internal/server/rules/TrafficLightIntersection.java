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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.rules.TrafficRuleCallback;

/**
 * A TrafficlightSetof is a gadjet that controlls a crossing street. It is also a sensor, because it measure its state
 * and sends data.
 * 
 * @author Marcus
 */
public class TrafficLightIntersection extends TrafficRule {

	private final TrafficGraphExtensions trafficGraphExtensions;

	/**
	 * the id of this TrafficLightExtension
	 */
	private final int id;

	/**
	 * determines whether this {@link TrafficLightIntersection} is activated
	 */
	private boolean activated;

	/**
	 * delay can be used to control the TrafficLightSetof
	 */
	private long delay = 0;

	/**
	 * a hash map that holds to each edge the traffic light that is responsible for the edge
	 */
	private final HashMap<Edge, TrafficLight> edgesToTrafficLights = new HashMap<Edge, TrafficLight>();

	/**
	 * flag whether this TrafficLightSetofs needs recalibration due to activating after it was inactive
	 */
	private boolean needsRecalibration;

	/**
	 * holds the time when the recalibration happened
	 */
	private long recalibrationTime;

	/**
	 * the first traffic light that controls two edges of the considered node
	 */
	private final TrafficLight trafficLight0;

	/**
	 * the second traffic light that controls at least one edge up to two of the considered node
	 */
	private final TrafficLight trafficLight1;

	/**
	 * Waiting queue
	 */
	private final Map<Edge, Queue<TrafficRuleData>> waiting = new HashMap<>();

	/**
	 * Constructor to create an instance of an {@link TrafficLightIntersection}
	 * 
	 * @param output
	 *            the SensorOutput used by the {@link TrafficLightIntersection} to transmit data
	 * @param sensorId
	 *            the unique integer id of the sensor
	 * @param node
	 *            the considered node this TrafficLightSetof is responsible for
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 * @throws RuntimeException
	 *             if argument 'node' doesn't have 3 or 4 edges or if at least one the other nodes' position is 'null'
	 */
	public TrafficLightIntersection(final int id, final Node node, final TrafficGraphExtensions trafficGraphExtensions)
			throws IllegalArgumentException, RuntimeException {
		this(id, node, trafficGraphExtensions, 1);
	}

	/**
	 * Constructor to create an instance of an TrafficLightSetof
	 * 
	 * @param output
	 *            the SensorOutput used by the TrafficLightSetof to transmit data
	 * @param sensorId
	 *            the unique integer id of the sensor
	 * @param node
	 *            the considered node this TrafficLightSetof is responsible for
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 * @throws RuntimeException
	 *             if argument 'node' doesn't have 3 or 4 edges or if at least one the other nodes' position is 'null'
	 */
	public TrafficLightIntersection(final int id, final Node node, final TrafficGraphExtensions trafficGraphExtensions,
			int updateLimit) throws IllegalArgumentException, RuntimeException {
		super(node);

		if (trafficGraphExtensions == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("trafficGraphExtensions"));
		}
		this.trafficGraphExtensions = trafficGraphExtensions;

		// if(uuid == null) {
		// throw new
		// IllegalArgumentException(ExceptionMessages.getMessageForNotNull("node"));
		// }
		if (updateLimit <= 0) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNegative("updateLimit", false));
		}

		this.id = id;

		// Check how many Edges the node has
		if (node.getEdgeSet().size() == 3) {
			// largest angle wins
			int index0 = -1;
			int index1 = -1;
			double maxAngle = Double.MIN_VALUE;
			for (int i = 0; i < (node.getEdgeSet().size() - 1); i++) {
				final Edge edge0 = node.getEdge(i);
				for (int j = i + 1; j < node.getEdgeSet().size(); j++) {
					final Edge edge1 = node.getEdge(j);

					final Node otherNode0 = edge0.getOpposite(node);
					final Node otherNode1 = edge1.getOpposite(node);

					final double a = this.trafficGraphExtensions.getLength(edge0);
					final double b = Math.sqrt(Math.pow(this.trafficGraphExtensions.getPosition(otherNode0).getX()
							- this.trafficGraphExtensions.getPosition(otherNode1).getX(), 2)
							+ Math.pow(this.trafficGraphExtensions.getPosition(otherNode0).getY()
									- this.trafficGraphExtensions.getPosition(otherNode1).getY(), 2));
					final double c = this.trafficGraphExtensions.getLength(edge1);

					// Save cosBeta
					final double angle = (Math.acos(((Math.pow(a, 2) + Math.pow(c, 2)) - Math.pow(b, 2)) / (2 * a * c)) * 180)
							/ Math.PI;
					if (angle > maxAngle) {
						maxAngle = angle;
						index0 = i;
						index1 = j;
					}
				}
			}

			int index2 = -1;
			if (((index0 == 0) && (index1 == 1)) || ((index0 == 1) && (index1 == 0))) {
				index2 = 2;
			} else if (((index0 == 1) && (index1 == 2)) || ((index0 == 2) && (index1 == 1))) {
				index2 = 0;
			} else {
				index2 = 1;
			}

			this.trafficLight0 = new TrafficLight(node.getEdge(index0), node.getEdge(index1), 0, 0,
					trafficGraphExtensions, (short) id);
			this.trafficLight1 = new TrafficLight(node.getEdge(index2), null, 0, -1, trafficGraphExtensions, (short) id);

			this.edgesToTrafficLights.put(node.getEdge(index0), this.trafficLight0);
			this.edgesToTrafficLights.put(node.getEdge(index1), this.trafficLight0);
			this.edgesToTrafficLights.put(node.getEdge(index2), this.trafficLight1);

		} else {
			final HashMap<Double, Edge> anglesToEdges = new HashMap<>();
			final HashMap<Edge, Double> edgesToAngles = new HashMap<>();

			// compute entrance angle for each edge
			for (final Edge edge : node.getEachEdge()) {
				// calculate angle
				final Node otherNode = edge.getOpposite(node);

				final double a = 1;
				final double b = Math.sqrt(Math.pow(this.trafficGraphExtensions.getPosition(otherNode).getX()
						- this.trafficGraphExtensions.getPosition(node).getX(), 2)
						+ Math.pow(this.trafficGraphExtensions.getPosition(otherNode).getY()
								- this.trafficGraphExtensions.getPosition(node).getY() - 1, 2));
				final double c = this.trafficGraphExtensions.getLength(edge);

				// Save cosBeta
				double angle = (Math.acos(((Math.pow(a, 2) + Math.pow(c, 2)) - Math.pow(b, 2)) / (2 * a * c)) * 180)
						/ Math.PI;
				if (this.trafficGraphExtensions.getPosition(otherNode).getX() < this.trafficGraphExtensions
						.getPosition(node).getX()) {
					angle = 360 - angle;
				}
				anglesToEdges.put(angle, edge);
				edgesToAngles.put(edge, angle);
			}

			// Sort list
			final ArrayList<Double> sortedAngles = new ArrayList<Double>(anglesToEdges.keySet());
			Collections.sort(sortedAngles);

			this.trafficLight0 = new TrafficLight(anglesToEdges.get(sortedAngles.get(0)),
					anglesToEdges.get(sortedAngles.get(2)), edgesToAngles.get(anglesToEdges.get(sortedAngles.get(0))),
					edgesToAngles.get(anglesToEdges.get(sortedAngles.get(2))), trafficGraphExtensions, (short) id);
			this.trafficLight1 = new TrafficLight(anglesToEdges.get(sortedAngles.get(1)),
					anglesToEdges.get(sortedAngles.get(3)), edgesToAngles.get(anglesToEdges.get(sortedAngles.get(1))),
					edgesToAngles.get(anglesToEdges.get(sortedAngles.get(3))), trafficGraphExtensions, (short) id);

			// Save to hash map
			this.edgesToTrafficLights.put(anglesToEdges.get(sortedAngles.get(0)), this.trafficLight0);
			this.edgesToTrafficLights.put(anglesToEdges.get(sortedAngles.get(2)), this.trafficLight0);
			this.edgesToTrafficLights.put(anglesToEdges.get(sortedAngles.get(1)), this.trafficLight1);
			this.edgesToTrafficLights.put(anglesToEdges.get(sortedAngles.get(3)), this.trafficLight1);
		}
		for (final Edge edge : this.getNode()) {
			this.waiting.put(edge, new LinkedList<TrafficRuleData>());
		}

		this.switchLights(0);
	}

	/**
	 * Checks whether the passed node is legal. <br>
	 * An exception is thrown if the node is null or has the wrong number of edges.
	 * 
	 * @param node
	 *            the node to validate
	 * @throws IllegalArgumentException
	 *             if argument 'node' is 'null'
	 */
	@Override
	protected void checkNode(final Node node) throws IllegalArgumentException {
		if (node == null) {
			throw new IllegalArgumentException("Argument \"node\" must not be \"null\"");
		}
		final int edgeSetSize = node.getEdgeSet().size();
		if (!((edgeSetSize == 3) || (edgeSetSize == 4))) {
			throw new IllegalStateException("Only crossroads with 3 or 4 edges are supported.");
		}
		// for (final Edge edge : node.getEachEdge()) {
		// if (!this.trafficGraphExtensions.hasPosition(edge.getNode0())
		// || !this.trafficGraphExtensions.hasPosition(edge.getNode1())) {
		// throw new IllegalStateException("At least one involved node has no attached position property.");
		// }
		// }
	}

	/**
	 * Returns the delay. The delay is used to control this {@link TrafficLightIntersection}.
	 * 
	 * @return the delay of this {@link TrafficLightIntersection}
	 */
	public long getDelay() {
		return this.delay;
	}

	/**
	 * Returns the number of streets that crosses the considered node.
	 * 
	 * @return the number of streets that crosses the considered node.
	 */
	public int getNumberOfStreets() {
		return this.getNode().getEdgeSet().size();
	}

	/**
	 * Returns the first TrafficLight of the TrafficLightSetof.
	 * 
	 * @return the first TrafficLight of the TrafficLightSetof.
	 */
	public TrafficLight getTrafficLight0() {
		return this.trafficLight0;
	}

	/**
	 * Returns the first TrafficLight of the TrafficLightSetof.
	 * 
	 * @return the first TrafficLight of the TrafficLightSetof.
	 */
	public TrafficLight getTrafficLight1() {
		return this.trafficLight1;
	}

	/**
	 * Returns the responsible TrafficLight for the passed edge.
	 * 
	 * @param edge
	 *            the edge to which the responsible TrafficLight shall be found
	 * @return the responsible TrafficLight for the passed edge
	 */
	public TrafficLight getTrafficLightForEdge(final Edge edge) {
		return this.edgesToTrafficLights.get(edge);
	}

	/**
	 * Determines whether this {@link TrafficLightIntersection} is activated.
	 * 
	 * @return activated
	 */
	public boolean isActivated() {
		return this.activated;
	}

	/**
	 * Sets this {@link TrafficLightIntersection} activated. <br>
	 * if it really was deactivated before then it flags this {@link TrafficLightIntersection} to be recalibrated. <br>
	 * Recalibration happens automatically so the client doesn't need to care.
	 * 
	 * @param activated
	 *            determines whether this {@link TrafficLightIntersection} is activated
	 */
	public void setActivated(boolean activated) {
		final boolean before = this.isActivated();
		this.activated = activated;
		if (!before && activated) {
			this.needsRecalibration = true;
		}
	}

	/**
	 * Sets the delay in milliseconds. The delay is used to control the {@link TrafficLightIntersection}. <br>
	 * The passed value automatically is divided and remaindered.
	 * 
	 * @param delay
	 *            delay in milliseconds that can adopt any positive long value or zero
	 * @exception IllegalArgumentException
	 *                if argument 'delay' is negative
	 */
	public void setDelay(long delay) throws IllegalArgumentException {
		if (delay < 0) {
			throw new IllegalArgumentException("Argument 'delay' must be positive or zero.");
		}
		this.delay = delay;
	}

	/**
	 * This method overrides its superclass method. Besides transmitting data by its output it first changes its state
	 * by switching the traffic lights.
	 */
	// @Override
	@Override
	public void update(SimulationEventList eventList) {
		if (eventList == null) {
			throw new IllegalArgumentException("\"eventList\" must not be \"null\"");
		}
		this.switchLights(eventList.getTimestamp());
		for (final Edge edge : this.waiting.keySet()) {
			final Queue<TrafficRuleData> queue = this.waiting.get(edge);
			final TrafficRuleData trafficRuleData = queue.peek();
			if (trafficRuleData != null) {
				if (this.edgesToTrafficLights.get(trafficRuleData.getFrom()).getState() == TrafficLightStateEnum.GREEN) {
					if (trafficRuleData.getCallback().onEnter()) {
						trafficRuleData.getCallback().onExit();
						queue.poll();
					}
				}
			}
		}
	}

	@Override
	public void register(final Vehicle vehicle, final Edge from, final Edge to, final TrafficRuleCallback callback)
			throws UnsupportedOperationException {
		final TrafficRuleData trafficRuleData = new TrafficRuleData(vehicle, from, to, callback);
		if (this.edgesToTrafficLights.get(trafficRuleData.getFrom()).getState() == TrafficLightStateEnum.GREEN) {
			if (trafficRuleData.getCallback().onEnter()) {
				trafficRuleData.getCallback().onExit();
			} else {
				this.waiting.get(trafficRuleData.getFrom()).offer(trafficRuleData);
			}
		} else {
			this.waiting.get(trafficRuleData.getFrom()).offer(trafficRuleData);
		}
	}

	/**
	 * This method switches both traffic lights due to an internal logic.
	 * 
	 * @param simulationTime
	 */
	private void switchLights(final long simulationTime) {

		// if(false && !this.isActivated()) {
		// this.trafficLight1.setCurrentState(this.trafficLight1.getTrafficLightBlinkingState());
		// this.trafficLight0.setCurrentState(this.trafficLight0.getTrafficLightBlinkingState());
		// return;
		// }

		if (this.needsRecalibration) {
			this.recalibrationTime = simulationTime;
			this.needsRecalibration = false;
		}

		/*
		 * get the seconds of the current minutes
		 */
		final long roundTime = (((simulationTime + this.delay) - this.recalibrationTime) / 1000) % 60;

		// System.out.println(roundTime);
		// vertical green, horizontal red (24)
		// vertical yellow, horizontal red (25)
		// vertical red, horizontal red (29)
		// vertical red, horizontal yellow/red (30)
		// vertical red, horizontal green (54)
		// vertical red, horizontal yellow (55)
		// vertical red, horizontal red (59)
		// vertical yellow/red, horizontal red (60)

		// Now it's time to switch
		if (roundTime < 24) {
			this.trafficLight1.setCurrentState(this.trafficLight1.getTrafficLightGreenState());
			this.trafficLight0.setCurrentState(this.trafficLight0.getTrafficLightRedState());
		} else if (roundTime < 25) {
			this.trafficLight1.setCurrentState(this.trafficLight1.getTrafficLightYellowState());
			this.trafficLight0.setCurrentState(this.trafficLight0.getTrafficLightRedState());
		} else if (roundTime < 29) {
			this.trafficLight1.setCurrentState(this.trafficLight1.getTrafficLightRedState());
			this.trafficLight0.setCurrentState(this.trafficLight0.getTrafficLightRedState());
		} else if (roundTime < 30) {
			this.trafficLight1.setCurrentState(this.trafficLight1.getTrafficLightRedState());
			this.trafficLight0.setCurrentState(this.trafficLight0.getTrafficLightRedYellowState());
		} else if (roundTime < 54) {
			this.trafficLight1.setCurrentState(this.trafficLight1.getTrafficLightRedState());
			this.trafficLight0.setCurrentState(this.trafficLight0.getTrafficLightGreenState());
		} else if (roundTime < 55) {
			this.trafficLight1.setCurrentState(this.trafficLight1.getTrafficLightRedState());
			this.trafficLight0.setCurrentState(this.trafficLight0.getTrafficLightYellowState());
		} else if (roundTime < 59) {
			this.trafficLight1.setCurrentState(this.trafficLight1.getTrafficLightRedState());
			this.trafficLight0.setCurrentState(this.trafficLight0.getTrafficLightRedState());
		} else if (roundTime < 60) {
			this.trafficLight1.setCurrentState(this.trafficLight1.getTrafficLightRedYellowState());
			this.trafficLight0.setCurrentState(this.trafficLight0.getTrafficLightRedState());
		}
	}
	
	/**
	 * Returns the id of this TrafficLightIntersection
	 * @return
	 */
	public int getId() {
		return id;
	}
}
