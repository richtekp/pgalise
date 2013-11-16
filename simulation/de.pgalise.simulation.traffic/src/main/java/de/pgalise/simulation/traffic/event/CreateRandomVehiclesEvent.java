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
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import java.util.List;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;

/**
 * The create random vehicles event creates the given {@link List} of
 * {@link CreateRandomVehicleData}. The created vehicles do not need to have
 * a start or target point.
 * @param <D> 
 * @param <N> 
 * @param <E> 
 * @author Timo
 * @author Lena
 */
public class CreateRandomVehiclesEvent<D extends VehicleData> extends AbstractVehicleEvent<D> {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -1592062299723834878L;

	/**
	 * List with CreateRandomVehicleData objects
	 */
	private List<CreateRandomVehicleData> createRandomVehicleDataList;

	public CreateRandomVehiclesEvent(
		TrafficServerLocal<VehicleEvent> responsibleServer, long timestamp, long elapsedTime,
		List<CreateRandomVehicleData> createRandomVehicleDataList) {
		super(responsibleServer, timestamp, elapsedTime, null);
		this.createRandomVehicleDataList = createRandomVehicleDataList;
	}

	public List<CreateRandomVehicleData> getCreateRandomVehicleDataList() {
		return createRandomVehicleDataList;
	}

	public void setCreateRandomVehicleDataList(List<CreateRandomVehicleData> createRandomVehicleDataList) {
		this.createRandomVehicleDataList = createRandomVehicleDataList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((createRandomVehicleDataList == null) ? 0 : createRandomVehicleDataList.hashCode());
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
		CreateRandomVehiclesEvent<?> other = (CreateRandomVehiclesEvent) obj;
		if (createRandomVehicleDataList == null) {
			if (other.createRandomVehicleDataList != null) {
				return false;
			}
		} else if (!createRandomVehicleDataList.equals(other.createRandomVehicleDataList)) {
			return false;
		}
		return true;
	}

	@Override
	public EventType getType() {
		return TrafficEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT;
	}
}
