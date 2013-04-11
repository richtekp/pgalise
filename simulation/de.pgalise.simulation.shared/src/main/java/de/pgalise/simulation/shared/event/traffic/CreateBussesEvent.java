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
 
package de.pgalise.simulation.shared.event.traffic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;
import de.pgalise.simulation.shared.traffic.BusRoute;

/**
 * The create busses event will create the given vehicles as busses
 * for the given routes.
 * @author Lena
 */
public class CreateBussesEvent extends TrafficEvent {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1693162235868339949L;

	/**
	 * Timestamp
	 */
	private long time;

	/**
	 * List with bus routes
	 */
	private List<String> routeIds;

	/**
	 * List with busses
	 */
	private List<CreateRandomVehicleData> createRandomVehicleDataList;

	/**
	 * Constructor
	 * 
	 * @param createRandomVehicleDataList
	 *            List with busses
	 * @param id
	 *            ID of the event
	 * @param time
	 *            Timestamp
	 * @param busRoutes
	 *            List with bus routes
	 */
	public CreateBussesEvent(UUID id, List<CreateRandomVehicleData> createRandomVehicleDataList, long time,
			List<BusRoute> busRoutes) {
		super(id, SimulationEventTypeEnum.CREATE_BUSSES_EVENT);
		this.time = time;
		// this.busRoutes = busRoutes;
		this.createRandomVehicleDataList = createRandomVehicleDataList;
		this.routeIds = new ArrayList<>();
		for (BusRoute r : busRoutes)
			this.routeIds.add(r.getRouteId());
	}

	/**
	 * @return the createRandomVehicleDataList
	 */
	public List<CreateRandomVehicleData> getCreateRandomVehicleDataList() {
		return createRandomVehicleDataList;
	}

	/**
	 * @param createRandomVehicleDataList
	 *            the createRandomVehicleDataList to set
	 */
	public void setCreateRandomVehicleDataList(List<CreateRandomVehicleData> createRandomVehicleDataList) {
		this.createRandomVehicleDataList = createRandomVehicleDataList;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return the busLines
	 */
	public List<String> getRouteIds() {
		return routeIds;
	}

	/**
	 * @param busLines
	 *            the busLines to set
	 */
	public void setRouteIds(List<String> busLines) {
		this.routeIds = busLines;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((routeIds == null) ? 0 : routeIds.hashCode());
		result = prime * result + ((createRandomVehicleDataList == null) ? 0 : createRandomVehicleDataList.hashCode());
		result = prime * result + (int) (time ^ (time >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CreateBussesEvent other = (CreateBussesEvent) obj;
		if (routeIds == null) {
			if (other.routeIds != null)
				return false;
		} else if (!routeIds.equals(other.routeIds))
			return false;
		if (createRandomVehicleDataList == null) {
			if (other.createRandomVehicleDataList != null)
				return false;
		} else if (!createRandomVehicleDataList.equals(other.createRandomVehicleDataList))
			return false;
		if (time != other.time)
			return false;
		return true;
	}
}
