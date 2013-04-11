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
 
package de.pgalise.simulation.shared.traffic;

/**
 * Wraps a stop name and its first stop time in millis.
 * @author Lena
 */
public class BusStopInformation {
	private String stopName;
	private long firstStopTimeInMillis;
	
	public BusStopInformation(String name, long time) {
		this.stopName = name;
		this.firstStopTimeInMillis = time;
	}

	/**
	 * @return the stopName
	 */
	public String getStopName() {
		return stopName;
	}

	/**
	 * @param stopName the stopName to set
	 */
	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	/**
	 * @return the stopTimeInMillis
	 */
	public long getFirstStopTimeInMillis() {
		return firstStopTimeInMillis;
	}

	/**
	 * @param stopTimeInMillis the stopTimeInMillis to set
	 */
	public void setFirstStopTimeInMillis(long stopTimeInMillis) {
		this.firstStopTimeInMillis = stopTimeInMillis;
	}
}
