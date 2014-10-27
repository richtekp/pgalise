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
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 * Remote view of the TrafficController.<br/><br/>
 * The TrafficController is responsible for everything that has to do with the
 * traffic aspect of the simulation. For instance it processes incoming traffic
 * events and generates traffic sensor data.
 *
 * @author Mustafa
 * @version 1.0 (Oct 23, 2012)
 */
public interface TrafficController<
	F extends TrafficEvent> extends
  SensorManagerController<F, TrafficStartParameter, TrafficInitParameter, TrafficSensor> {

}
