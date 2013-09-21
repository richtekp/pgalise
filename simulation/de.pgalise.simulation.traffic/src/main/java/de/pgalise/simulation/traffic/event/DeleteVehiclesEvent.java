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
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import java.util.List;
import java.util.UUID;

import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventTypeEnum;

/**
 * Removes the vehicles with the given {@link UUID}.
 * @author Timo
 */
public class DeleteVehiclesEvent extends AbstractVehicleEvent {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1533612793042541169L;

	/**
	 * List with IDs of vehicles to delete
	 */
	private List<Vehicle<?>> deleteVehicles;

	public DeleteVehiclesEvent(TrafficServerLocal server,
		long simulationTime,
		long elapsedTime,
		List<Vehicle<?>> deleteVehicles) {
		super(server,
			simulationTime,
			elapsedTime);
		this.deleteVehicles = deleteVehicles;
	}

	public void setDeleteVehicles(
		List<Vehicle<?>> deleteVehicles) {
		this.deleteVehicles = deleteVehicles;
	}

	public List<Vehicle<?>> getDeleteVehicles() {
		return deleteVehicles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((deleteVehicles == null) ? 0 : deleteVehicles.hashCode());
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
		if (deleteVehicles == null) {
			if (other.deleteVehicles != null) {
				return false;
			}
		} else if (!deleteVehicles.equals(other.deleteVehicles)) {
			return false;
		}
		return true;
	}

	@Override
	public EventType getType() {
		return TrafficEventTypeEnum.DELETE_VEHICLES_EVENT;
	}
}
