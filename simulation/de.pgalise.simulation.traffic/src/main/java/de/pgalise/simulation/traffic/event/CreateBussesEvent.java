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
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.VehicleData;
import java.util.List;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The create busses event will create the given vehicles as busses
 * for the given routes.
 * @param <D>
 * @author Lena
 */
@XmlRootElement
public class CreateBussesEvent<D extends VehicleData> extends AbstractTrafficEvent<D,CreateBussesEvent<D>> {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1693162235868339949L;

	/**
	 * List with bus routes
	 */
	private List<BusRoute> busRoutes;

	/**
	 * List with busses
	 */
	private List<CreateRandomBusData> createRandomVehicleDataList;

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
	public CreateBussesEvent(TrafficServerLocal<CreateBussesEvent<D>> responsibleServer, long time, long elaspsedTime, List<CreateRandomBusData> createRandomVehicleDataList, 
			List<BusRoute> busRoutes) {
		super(responsibleServer, time,
			elaspsedTime);
		// this.busRoutes = busRoutes;
		this.createRandomVehicleDataList = createRandomVehicleDataList;
		this.busRoutes = busRoutes;
	}

	/**
	 * @return the createRandomVehicleDataList
	 */
	public List<CreateRandomBusData> getCreateRandomVehicleDataList() {
		return createRandomVehicleDataList;
	}

	/**
	 * @param createRandomVehicleDataList
	 *            the createRandomVehicleDataList to set
	 */
	protected void setCreateRandomVehicleDataList(List<CreateRandomBusData> createRandomVehicleDataList) {
		this.createRandomVehicleDataList = createRandomVehicleDataList;
	}

	/**
	 * @return the busLines
	 */
	public List<BusRoute> getBusRoutes() {
		return busRoutes;
	}

	/**
	 * @param busLines
	 *            the busLines to set
	 */
	public void setBusRoutes(List<BusRoute> busLines) {
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
