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
 
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.traffic.BusTrip;
import java.util.List;
import java.util.UUID;

/**
 * Contains bus stops for ohne busline and the stop times.
 * @author Lena
 *
 */
public class BusLineInformation {
	private UUID busID;
	private List<BusTrip> weekdayTripWayThere;
	private List<BusTrip> weekdayTripWayBack;
	private List<BusTrip> weekendTripWayThere;
	private List<BusTrip> weekendTripWayBack;

	/**
	 * @return the busID
	 */
	public UUID getBusID() {
		return busID;
	}

	/**
	 * @param busID the busID to set
	 */
	public void setBusID(UUID busID) {
		this.busID = busID;
	}

	/**
	 * @return the weekdayTripWayThere
	 */
	public List<BusTrip> getWeekdayTripWayThere() {
		return weekdayTripWayThere;
	}

	/**
	 * @param weekdayTripWayThere the weekdayTripWayThere to set
	 */
	public void setWeekdayTripWayThere(List<BusTrip> weekdayTripWayThere) {
		this.weekdayTripWayThere = weekdayTripWayThere;
	}

	/**
	 * @return the weekdayTripWayBack
	 */
	public List<BusTrip> getWeekdayTripWayBack() {
		return weekdayTripWayBack;
	}

	/**
	 * @param weekdayTripWayBack the weekdayTripWayBack to set
	 */
	public void setWeekdayTripWayBack(List<BusTrip> weekdayTripWayBack) {
		this.weekdayTripWayBack = weekdayTripWayBack;
	}

	/**
	 * @return the weekendTripWayThere
	 */
	public List<BusTrip> getWeekendTripWayThere() {
		return weekendTripWayThere;
	}

	/**
	 * @param weekendTripWayThere the weekendTripWayThere to set
	 */
	public void setWeekendTripWayThere(List<BusTrip> weekendTripWayThere) {
		this.weekendTripWayThere = weekendTripWayThere;
	}

	/**
	 * @return the weekendTripWayBack
	 */
	public List<BusTrip> getWeekendTripWayBack() {
		return weekendTripWayBack;
	}

	/**
	 * @param weekendTripWayBack the weekendTripWayBack to set
	 */
	public void setWeekendTripWayBack(List<BusTrip> weekendTripWayBack) {
		this.weekendTripWayBack = weekendTripWayBack;
	}
}
