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
 
package de.pgalise.simulation.traffic.event;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.BusRoute;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.server.DefaultTrafficServer;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.util.ArrayList;
import java.util.List;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;

/**
 * The create busses event will create the given vehicles as busses
 * for the given routes.
 * @param <N> 
 * @param <E> 
 * @author Lena
 */
public class CreateBussesEvent<D extends VehicleData> extends AbstractTrafficEvent<D> {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1693162235868339949L;

	/**
	 * List with bus routes
	 */
	private List<BusRoute<?>> busRoutes;

	/**
	 * List with busses
	 */
	private List<CreateRandomVehicleData> createRandomVehicleDataList;

	/**
	 * Constructor
	 * 
	 * @param responsibleServer 
	 * @param createRandomVehicleDataList
	 *            List with busses
	 * @param elaspsedTime 
	 * @param time
	 *            Timestamp
	 * @param busRoutes
	 *            List with bus routes
	 */
	public CreateBussesEvent(DefaultTrafficServer<D> responsibleServer, long time, long elaspsedTime, List<CreateRandomVehicleData> createRandomVehicleDataList, 
			List<BusRoute<?>> busRoutes) {
		super(responsibleServer, time,
			elaspsedTime);
		// this.busRoutes = busRoutes;
		this.createRandomVehicleDataList = createRandomVehicleDataList;
		this.busRoutes = busRoutes;
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
	 * @return the busLines
	 */
	public List<BusRoute<?>> getBusRoutes() {
		return busRoutes;
	}

	/**
	 * @param busLines
	 *            the busLines to set
	 */
	public void setBusRoutes(List<BusRoute<?>> busLines) {
		this.busRoutes = busLines;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((busRoutes == null) ? 0 : busRoutes.hashCode());
		result = prime * result + ((createRandomVehicleDataList == null) ? 0 : createRandomVehicleDataList.hashCode());
		result = prime * result + (int) (getSimulationTime() ^ (getSimulationTime() >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CreateBussesEvent other = (CreateBussesEvent) obj;
		if (busRoutes == null) {
			if (other.busRoutes != null) {
				return false;
			}
		} else if (!busRoutes.equals(other.busRoutes)) {
			return false;
		}
		if (createRandomVehicleDataList == null) {
			if (other.createRandomVehicleDataList != null) {
				return false;
			}
		} else if (!createRandomVehicleDataList.equals(other.createRandomVehicleDataList)) {
			return false;
		}
		if (getSimulationTime() != other.getSimulationTime()) {
			return false;
		}
		return true;
	}

	@Override
	public EventType getType() {
		return TrafficEventTypeEnum.CREATE_BUSSES_EVENT;
	}
}
