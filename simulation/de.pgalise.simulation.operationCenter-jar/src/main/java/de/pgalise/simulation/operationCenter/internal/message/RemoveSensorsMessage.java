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

import de.pgalise.simulation.sensorFramework.Sensor;
import java.util.Collection;
/**
 * If you want to remove more than one sensor at the same time.
 * @author Timo
 */
public class RemoveSensorsMessage extends OCWebSocketMessage<Collection<Sensor<?,?>>> {
	/**
	 * Constructor
	 * @param content a list with all sensor ids that will be removed.
	 */
	public RemoveSensorsMessage(Collection<Sensor<?,?>> content) {
		super(OCWebSocketMessage.MessageType.REMOVE_SENSORS, content);
	}
}
