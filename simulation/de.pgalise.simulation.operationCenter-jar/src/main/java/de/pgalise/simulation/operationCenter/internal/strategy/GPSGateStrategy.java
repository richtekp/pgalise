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
 
package de.pgalise.simulation.operationCenter.internal.strategy;

import de.pgalise.simulation.sensorFramework.Sensor;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;

/**
 * Interface to handle gate messages.
 * @author Timo
 */
public interface GPSGateStrategy extends Controller<Event, InfrastructureStartParameter, TrafficInitParameter>, SensorManagerController<Event, InfrastructureStartParameter, TrafficInitParameter,Sensor<?,?>> {
	/**
	 * Handles a gate message.
	 * @param gateInformationMap <Integer = sensor type, Double = percentage> e.g. 1, 0.8 means 80% of sensors from type 1.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void handleGateMessage(Map<Integer, Double> gateInformationMap) throws UnknownHostException, IOException;
}
