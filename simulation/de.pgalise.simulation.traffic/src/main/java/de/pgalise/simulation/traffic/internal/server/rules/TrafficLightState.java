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


/**
 * Abstract super class for TrafficLightStates
 * 
 * @author Marcus
 */
abstract class TrafficLightState {

	/**
	 * Constructor that creates a TrafficLightState
	 * 
	 * @param trafficLight
	 *            the considered TrafficLight
	 */
	protected TrafficLightState() {
	}

	/**
	 * Returns the state as an enum constant.
	 * 
	 * @return the state as an enum constant
	 */
	abstract TrafficLightStateEnum getState();
}
