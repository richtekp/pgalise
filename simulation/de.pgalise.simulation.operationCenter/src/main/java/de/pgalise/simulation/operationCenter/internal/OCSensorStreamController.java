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


/**
 * Interface for sensor stream controller.
 * Observes IBM InfoSphere streams or another DSMS and updates the @OCSimulationController
 * @author Timo
 */
public interface OCSensorStreamController {
	
	/**
	 * Listen to the stream.
	 * @param ocSimulationController
	 * 			the ocSimulationController must be updated
	 * 			via {@link OCSimulationController#update(long, de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData).
	 * @param socketStaticSensorIP
	 * 			the InfoSphere IP for static sensors
	 * @param socketStaticSensorPort
	 * 			the InfoSphere port for static sensors
	 * @param socketDynamicSensorIP
	 * 			the InfoSphere IP for dynamic (gps) sensors
	 * @param socketDynamicSensorPort
	 * 			the InfoSphere port for dynamic (gps) sensors
	 * @param socketTopoRadarIP
	 * 			the InfoSphere IP for topo radar sensors
	 * @param socketTopoRadarPort
	 * 			the InfoSphere port for topo radar sensors
	 * @param socketTrafficLightIP
	 * 			the InfoSphere IP for traffic light sensors
	 * @param socketTrafficLightPort
	 * 			the InfoSphere port for traffic light sensors
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void listenStream(OCSimulationController ocSimulationController, 
			String socketStaticSensorIP, int socketStaticSensorPort, String socketDynamicSensorIP, int socketDynamicSensorPort,
			String socketTopoRadarIP, int socketTopoRadarPort, String socketTrafficLightIP, int socketTrafficLightPort) throws UnknownHostException, IOException;

	/**
	 * Unlisten
	 */
	public void unlistenStream();
}
