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
import java.util.List;
import java.util.UUID;

import de.pgalise.simulation.shared.event.EventTypeEnum;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventTypeEnum;

/**
 * Creates vehicles with given path.
 * 
 * @author Timo
 * @author Andreas Rehfeldt
 */
public class CreateVehiclesEvent extends AbstractVehicleEvent {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -5015354611010459052L;

	/**
	 * List with vehicle informations
	 */
	private List<CreateRandomVehicleData> vehicles;

	public CreateVehiclesEvent(
		List<CreateRandomVehicleData> vehicles,
		TrafficServerLocal server,
		long simulationTime,
		long elapsedTime) {
		super(
			server,
			simulationTime,
			elapsedTime);
		this.vehicles = vehicles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((vehicles == null) ? 0 : vehicles.hashCode());
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
		CreateVehiclesEvent other = (CreateVehiclesEvent) obj;
		if (vehicles == null) {
			if (other.vehicles != null) {
				return false;
			}
		} else if (!vehicles.equals(other.vehicles)) {
			return false;
		}
		return true;
	}

	public List<CreateRandomVehicleData> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<CreateRandomVehicleData> vehicles) {
		this.vehicles = vehicles;
	}

	@Override
	public EventType getType() {
		return TrafficEventTypeEnum.CREATE_VEHICLES_EVENT;
	}
}
