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

import java.util.HashMap;
import java.util.Map;

/**
 * Enum of the traffic light states
 * 
 * @author Marcus
 * @version 1.0 (Oct 28, 2012)
 */
public enum TrafficLightStateEnum {

	BLINKING(0), GREEN(1), RED(2), RED_YELLOW(3), YELLOW(4);

	/**
	 * maps state ids integers to TrafficLightStateEnums
	 */
	private static Map<Integer, TrafficLightStateEnum> STATE_IDS;

	/**
	 * Returns SENSOR_TYPE_IDS.
	 * 
	 * @return SENSOR_TYPE_IDS
	 */
	private static Map<Integer, TrafficLightStateEnum> getStateIds() {
		if (TrafficLightStateEnum.STATE_IDS == null) {
			TrafficLightStateEnum.STATE_IDS = new HashMap<>();
		}
		return TrafficLightStateEnum.STATE_IDS;
	}

	/**
	 * Returns the {@link TrafficLightStateEnum} for the passed state id integer.
	 * 
	 * @param stateId
	 *            the state id of the wanted {@link TrafficLightStateEnum}
	 * @return the {@link TrafficLightStateEnum} for the passed state id integer
	 */
	public static TrafficLightStateEnum getForStateId(final int stateId) {
		return TrafficLightStateEnum.STATE_IDS.get(stateId);
	}

	/**
	 * the state id of this {@link TrafficLightStateEnum}
	 */
	private final int stateId;

	/**
	 * Creates a {@link TrafficLightStateEnum}.
	 * 
	 * @param stateId
	 *            the integer state id belonging to the literal
	 * @throws IllegalStateException
	 *             if argument 'stateId' is already mapped to a {@link TrafficLightStateEnum}
	 */
	private TrafficLightStateEnum(final int stateId) throws IllegalStateException {
		if (TrafficLightStateEnum.getStateIds().containsKey(stateId)) {
			throw new IllegalStateException("Argument 'sensorTypeId' = " + stateId + " is already registered");
		}
		this.stateId = stateId;
		TrafficLightStateEnum.getStateIds().put(this.stateId, this);
	}

	/**
	 * Returns the state is of this {@link TrafficLightStateEnum}.
	 * 
	 * @return the state is of this {@link TrafficLightStateEnum}
	 */
	public int getStateId() {
		return this.stateId;
	}
}
