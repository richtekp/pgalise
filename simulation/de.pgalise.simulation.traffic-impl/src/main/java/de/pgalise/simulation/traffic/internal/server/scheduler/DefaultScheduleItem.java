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
 
package de.pgalise.simulation.traffic.internal.server.scheduler;

import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;

/**
 * An item represents a vehicle and its departure time.
 * 
 * @param <D> 
 * @author Mustafa
 * @author Andreas Rehfeldt
 * @version 1.0
 */
public class DefaultScheduleItem<D extends VehicleData> implements ScheduleItem, Comparable<ScheduleItem> {

	/**
	 * Departure time
	 */
	private long time;

	/**
	 * Vehicle
	 */
	private final Vehicle<D> vehicle;

	/**
	 * Point in time at which the vehicle got scheduled and starts driving.
	 * (Needed for recognizing if a vehicle passed its startnode) 
	 */
	private long scheduledAt;

	/**
	 * the time the vehicle of this items has been updated
	 */
	private long lastUpdate;

	/**
	 * Constructor
	 * 
	 * @param vehicle
	 *            Vehicle
	 * @param time
	 *            Departure time
	 * @param reversePath
	 *            True if the vehicle's path shall be reversed before it begins its journey
	 */
	public DefaultScheduleItem(Vehicle<D> vehicle, long time, long updateIntervall) {
		this.vehicle = vehicle;
		this.time = time;
		this.lastUpdate = time - updateIntervall;
	}

	/**
	 * The time the vehicle should be departured.
	 * 
	 * @return
	 */
	public long getDepartureTime() {
		return this.time;
	}

	public void setDepartureTime(long time) {
		this.time = time;
	}

	public Vehicle<D> getVehicle() {
		return this.vehicle;
	}

	/**
	 * @return the time the vehicle of this items has been updated
	 */
	public long getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "Item [time=" + time + ", lastUpdate=" + lastUpdate + "]";
	}

	/**
	 * First time the vehicle is driving.
	 * 
	 * @return
	 */
	public long getScheduleTime() {
		return scheduledAt;
	}

	@Override
	public int compareTo(ScheduleItem o) {
		long thisTime = this.getDepartureTime();
		long anotherTime = o.getDepartureTime();

		if (thisTime < anotherTime) {
			return -1;
		}
		if (thisTime > anotherTime) {
			return 1;
		}

		// Same time
		if (vehicle == null) {
			return -1;
		}
		if (vehicle.equals(o.getVehicle())) {
			return 0;
		}

		return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (time ^ (time >>> 32));
		result = prime * result + ((vehicle == null) ? 0 : vehicle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DefaultScheduleItem other = (DefaultScheduleItem) obj;
		if (time != other.time) {
			return false;
		}
		if (vehicle == null) {
			if (other.vehicle != null) {
				return false;
			}
		} else if (!vehicle.equals(other.vehicle)) {
			return false;
		}
		return true;
	}

	public void setScheduleTime(long time) {
		this.scheduledAt = time;
	}
}
