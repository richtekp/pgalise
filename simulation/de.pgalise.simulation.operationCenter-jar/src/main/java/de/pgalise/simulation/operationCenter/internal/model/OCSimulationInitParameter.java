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
package de.pgalise.simulation.operationCenter.internal.model;

import com.vividsolutions.jts.geom.Envelope;

/**
 * This model contains all that is needed to initialize the operation center.
 *
 * @author Timo
 */
public class OCSimulationInitParameter {

	private long startTimestamp, endTimestamp, interval, clockGeneratorInterval;
	private Envelope cityBoundary;

	/**
	 * Constructor
	 *
	 * @param startTimestamp the start time of the simulation
	 * @param endTimestamp the end time of the simulation
	 * @param interval the interval of the simulation
	 * @param clockGeneratorInterval the interval for pauses between every
	 * simulation step
	 * @param cityBoundary the boundaries of the city to set a fitting map
	 */
	public OCSimulationInitParameter(long startTimestamp,
		long endTimestamp,
		long interval,
		long clockGeneratorInterval,
		Envelope cityBoundary) {
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.interval = interval;
		this.clockGeneratorInterval = clockGeneratorInterval;
		this.cityBoundary = cityBoundary;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public long getClockGeneratorInterval() {
		return clockGeneratorInterval;
	}

	public void setClockGeneratorInterval(long clockGeneratorInterval) {
		this.clockGeneratorInterval = clockGeneratorInterval;
	}

	public Envelope getCityBoundary() {
		return cityBoundary;
	}

	public void setCityBoundary(Envelope cityBoundary) {
		this.cityBoundary = cityBoundary;
	}
}
