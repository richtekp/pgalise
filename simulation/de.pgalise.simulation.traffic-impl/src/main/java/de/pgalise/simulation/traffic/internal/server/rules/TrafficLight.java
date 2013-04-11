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

import org.graphstream.graph.Edge;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;


/**
 * Class that represents a single traffic light. This class can assume several states.
 * 
 * @author Marcus
 */
public class TrafficLight {

	/**
	 * Current state
	 */
	private final static TrafficLightState TRAFFIC_LIGHT_BLINKING_STATE = new TrafficLightBlinkingState();
	private final static TrafficLightState TRAFFIC_LIGHT_GREEN_STATE = new TrafficLightGreenState();
	private final static TrafficLightState TRAFFIC_LIGHT_RED_STATE = new TrafficLightRedState();
	private final static TrafficLightState TRAFFIC_LIGHT_RED_YELLOW_STATE = new TrafficLightRedYellowState();
	private final static TrafficLightState TRAFFIC_LIGHT_YELLOW_STATE = new TrafficLightYellowState();
		
	private final Edge edge1;
	private final Edge edge2;
	
	private final double angle1;
	private final double angle2;
	
	private final short intersectionID;

	private TrafficLightState currentState = TrafficLight.TRAFFIC_LIGHT_BLINKING_STATE;

	public TrafficLight(final Edge edge1, final Edge edge2, double angle1, double angle2, 
			final TrafficGraphExtensions trafficGraphExtensions, short intersectionID) {
		if(edge1 == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("edge1"));
		}
		this.edge1 = edge1;
		this.edge2 = edge2;
			
		this.angle1 = angle1;
		this.angle2 = angle2;
		
		this.intersectionID = intersectionID;
	}

	Edge getEdge1() {
		return this.edge1;
	}

	Edge getEdge2() {
		return this.edge2;
	}

	public double getAngle1() {
		return this.angle1;
	}

	public double getAngle2() {
		return this.angle2;
	}

	/**
	 * Returns the current state of the TrafficLight as an enum constant.
	 * 
	 * @return the current state of the TrafficLight as an enum constant
	 */
	public TrafficLightStateEnum getState() {
		return this.currentState.getState();
	}

	/**
	 * Returns the green state which a TrafficLight can assume.
	 * 
	 * @return the green state which a TrafficLight can assume
	 */
	TrafficLightState getTrafficLightBlinkingState() {
		return TrafficLight.TRAFFIC_LIGHT_BLINKING_STATE;
	}

	/**
	 * Return the instance of the current state this TrafficLight has.
	 * 
	 * @return the instance of the current state this TrafficLight has
	 */
	TrafficLightState getCurrentState() {
		return this.currentState;
	}

	/**
	 * Returns the green state which a TrafficLight can assume.
	 * 
	 * @return the green state which a TrafficLight can assume
	 */
	TrafficLightState getTrafficLightGreenState() {
		return TrafficLight.TRAFFIC_LIGHT_GREEN_STATE;
	}

	/**
	 * Returns the green state which a TrafficLight can assume.
	 * 
	 * @return the green state which a TrafficLight can assume
	 */
	TrafficLightState getTrafficLightRedState() {
		return TrafficLight.TRAFFIC_LIGHT_RED_STATE;
	}

	/**
	 * Returns the green state which a TrafficLight can assume.
	 * 
	 * @return the green state which a TrafficLight can assume
	 */
	TrafficLightState getTrafficLightRedYellowState() {
		return TrafficLight.TRAFFIC_LIGHT_RED_YELLOW_STATE;
	}

	/**
	 * Returns the green state which a TrafficLight can assume.
	 * 
	 * @return the green state which a TrafficLight can assume
	 */
	TrafficLightState getTrafficLightYellowState() {
		return TrafficLight.TRAFFIC_LIGHT_YELLOW_STATE;
	}

	/**
	 * Sets the current state of the TrafficLight. To use it pass a state as a parameter by using one of the "getState"
	 * methods (i.e. t.setCurrentState(t.getTrafficLightRedState)).
	 * 
	 * @param currentState
	 *            the state which the TrafficLight is supposed to assume.
	 */
	void setCurrentState(final TrafficLightState currentState) {
		if (currentState == null) {
			throw new IllegalArgumentException("state must not be NULL.");
		}
		this.currentState = currentState;
	}

	public short getIntersectionID() {
		return intersectionID;
	}
}
