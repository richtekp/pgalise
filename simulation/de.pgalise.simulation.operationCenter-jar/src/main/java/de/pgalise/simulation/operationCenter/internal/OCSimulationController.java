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
 
package de.pgalise.simulation.operationCenter.internal;

import java.io.IOException;
import java.net.UnknownHostException;

import de.pgalise.simulation.operationCenter.internal.OCWebSocketService.NewUserEventListener;
import de.pgalise.simulation.operationCenter.internal.message.GateMessage;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.visualizationcontroller.VisualizationController;

/**
 * The OC simulation controller receives simulation information from
 * the {@link OCSimulationServlet} and has to provide it to the users
 * by using {@link OCWebSocketService}.
 * 
 * @author Timo
 */
public interface OCSimulationController extends SensorManagerController<Event,InfrastructureStartParameter, TrafficInitParameter,Sensor<?,?>>, NewUserEventListener, VisualizationController {

	/**
	 * Updates the clients.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @param sensorData
	 *            Sensor helper
	 * @throws IllegalStateException
	 */
	public void update(long timestamp, Sensor<?,?> sensorData) throws IllegalStateException;

	/**
	 * Handles a gate message.
	 * 
	 * @param gateMessage
	 *            Message to define how many sensors should be ignored by the data stream management system
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public void handleGateMessage(GateMessage gateMessage) throws UnknownHostException, IOException;
}
