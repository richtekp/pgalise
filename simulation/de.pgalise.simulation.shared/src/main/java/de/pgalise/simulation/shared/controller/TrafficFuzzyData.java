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
 
package de.pgalise.simulation.shared.controller;

import java.io.Serializable;
/**
 * Settings for the fuzzy rules in traffic.
 * The updatesteps, tolerance and timeBuffer can be set here.
 * @author Mischa
 */
public class TrafficFuzzyData implements Serializable {
	private static final long serialVersionUID = 7976429010181411827L;
	private int updateSteps;
	private double tolerance;
	private int timeBuffer;

	/**
	 * Constructor
	 * @param updateSteps
	 * 			how often to ask the fuzzy rules
	 * @param tolerance
	 * 			what is the tolerance
	 * @param buffer
	 * 			how big is the timeBuffer
	 */
	public TrafficFuzzyData(int updateSteps, double tolerance, int buffer) {
		this.updateSteps = updateSteps;
		this.tolerance = tolerance;
		this.timeBuffer = buffer;
	}

	public int getUpdateSteps() {
		return updateSteps;
	}

	public void setUpdateSteps(int updateSteps) {
		this.updateSteps = updateSteps;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public int getTimeBuffer() {
		return timeBuffer;
	}

	public void setTimeBuffer(int timeBuffer) {
		this.timeBuffer = timeBuffer;
	}
}
