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

import java.util.List;
import java.util.UUID;

import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;

/**
 * The create random vehicles event creates the given {@link List} of
 * {@link CreateRandomVehicleData}. The created vehicles do not need to have
 * a start or target point.
 * @author Timo
 * @author Lena
 */
public class CreateRandomVehiclesEvent extends TrafficEvent {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -1592062299723834878L;

	/**
	 * List with CreateRandomVehicleData objects
	 */
	private List<CreateRandomVehicleData> createRandomVehicleDataList;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the event
	 * @param createRandomVehicleDataList
	 *            List with CreateRandomVehicleData
	 */
	public CreateRandomVehiclesEvent(List<CreateRandomVehicleData> createRandomVehicleDataList) {
		super(SimulationEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT);
		this.createRandomVehicleDataList = createRandomVehicleDataList;
	}

	public CreateRandomVehiclesEvent(Long id,
		List<CreateRandomVehicleData> createRandomVehicleDataList	) {
		super(id,
			SimulationEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT);
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
		CreateRandomVehiclesEvent other = (CreateRandomVehiclesEvent) obj;
		if (createRandomVehicleDataList == null) {
			if (other.createRandomVehicleDataList != null) {
				return false;
			}
		} else if (!createRandomVehicleDataList.equals(other.createRandomVehicleDataList)) {
			return false;
		}
		return true;
	}
}
