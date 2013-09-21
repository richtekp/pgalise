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
 
package de.pgalise.simulation.operationCenter.internal.message;

import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.util.Collection;

/**
 * Message to remove vehicles.
 * @author Timo
 */
public class RemoveVehiclesMessage extends OCWebSocketMessage<Collection<VehicleData>>{

	/**
	 * Constructor
	 * @param content
	 * 			all vehicles, which shall be removed
	 */
	public RemoveVehiclesMessage(Collection<VehicleData> content) {
		super(OCWebSocketMessage.MessageType.REMOVE_VEHICLES_MESSAGE, content);
	}
}
