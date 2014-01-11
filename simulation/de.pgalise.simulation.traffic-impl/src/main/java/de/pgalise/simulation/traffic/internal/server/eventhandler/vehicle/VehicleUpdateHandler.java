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
 
package de.pgalise.simulation.traffic.internal.server.eventhandler.vehicle;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.internal.server.eventhandler.AbstractVehicleEventHandler;
import de.pgalise.simulation.traffic.internal.server.jam.DefaultNaSchModel;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.jam.TrafficJamModel;
import javax.ejb.EJB;

/**
 * ...
 * 
 * @author Mustafa
 */
public class VehicleUpdateHandler extends AbstractVehicleEventHandler<VehicleData,VehicleEvent> {
	@EJB
	private RandomSeedService randomSeedService;

	/**
	 * Traffic jam model
	 */
	private TrafficJamModel jamModel;

	/**
	 * Default constructor
	 */
	public VehicleUpdateHandler() {
	}

	@Override
	public VehicleEventTypeEnum getTargetEventType() {
		return VehicleEventTypeEnum.VEHICLE_UPDATE;
	}

	@Override
	public void handleEvent(VehicleEvent event) {
		// // ansonsten gibt es komische werte bei der berechnung der neuen position des autos
		// if(event.getElapsedTime()<=0) {
		// log.warn("No update processed on vehicle "+event.getVehicle().getName()+", elapsedTime=0");
		// }

		if (jamModel == null) {
			jamModel = new DefaultNaSchModel(randomSeedService);
		}
		if(VehicleTypeEnum.MOTORIZED_VEHICLES.contains(event.getVehicle().getData().getType())) {			
			jamModel.update(event.getVehicle(), event.getElapsedTime(), event.getGraph(), 0.01);
		}
		else {
			event.getVehicle().update(event.getElapsedTime());
		}
	}
}
