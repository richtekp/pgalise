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
 
package de.pgalise.simulation.energy.profile;

import java.util.Map;

/**
 * Represents the energy profile
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 27, 2012)
 */
public class EnergyProfile {

	/**
	 * Interval of the timestamp values
	 */
	public static final int DATA_INTERVAL = 900000; // 15 min

	/**
	 * Map with timestamp and data
	 */
	private Map<Long, Double> data;

	/**
	 * Timestamp of the end
	 */
	private long end;

	/**
	 * Timestamp of the start
	 */
	private long start;

	/**
	 * Constructor
	 * 
	 * @param start
	 *            Timestamp of the start
	 * @param end
	 *            Timestamp of the end
	 * @param map
	 *            Data of the energy profile
	 */
	public EnergyProfile(long start, long end, Map<Long, Double> map) {
		this.start = start;
		this.end = end;
		this.data = map;
	}

	public Map<Long, Double> getData() {
		return this.data;
	}

	public long getEnd() {
		return this.end;
	}

	public long getStart() {
		return this.start;
	}

	public void setData(Map<Long, Double> data) {
		this.data = data;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public void setStart(long start) {
		this.start = start;
	}
}
