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

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.util.Collection;

/**
 * This message will be send, if new vehicles are created.
 * Notice: Vehicles can not be instantiated with {@link NewSensorsMessage}.
 * @author Timo
 */
public class NewVehiclesMessage extends OCWebSocketMessage<Collection<Vehicle<?>>> {

	/**
	 * Constructor
	 * @param content
	 * 			the vehicles, which shall be created
	 */
	public NewVehiclesMessage(Collection<Vehicle<?>> content) {
		super(OCWebSocketMessage.MessageType.NEW_VEHICLES_MESSAGE, content);
	}
}