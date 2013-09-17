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
 * Removes the vehicles with the given {@link UUID}.
 * @author Timo
 */
public class DeleteVehiclesEvent extends TrafficEvent {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1533612793042541169L;

	/**
	 * List with IDs of vehicles to delete
	 */
	private List<UUID> deleteVehicleIDList;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the event
	 * @param vehicleIDList
	 *            List with IDs of vehicles to delete
	 */
	public DeleteVehiclesEvent(UUID id, List<UUID> vehicleIDList) {
		super(id, SimulationEventTypeEnum.DELETE_VEHICLES_EVENT);
		this.deleteVehicleIDList = vehicleIDList;
	}

	public List<UUID> getDeleteVehicleIDList() {
		return this.deleteVehicleIDList;
	}

	public void setDeleteVehicleIDList(List<UUID> deleteVehilceIDList) {
		this.deleteVehicleIDList = deleteVehilceIDList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((deleteVehicleIDList == null) ? 0 : deleteVehicleIDList.hashCode());
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
		DeleteVehiclesEvent other = (DeleteVehiclesEvent) obj;
		if (deleteVehicleIDList == null) {
			if (other.deleteVehicleIDList != null) {
				return false;
			}
		} else if (!deleteVehicleIDList.equals(other.deleteVehicleIDList)) {
			return false;
		}
		return true;
	}
}
