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
 
package de.pgalise.simulation.visualizationcontroller;

import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.Event;

/**
 * The interface for the operation center. It receives all information from the simulation
 * and sends it e.g. to the OperationCenter for visualization web application via HTTP.
 * 
 * @author Timo
 */
public interface OperationCenterController extends VisualizationController, SensorManagerController<Event, StartParameter, InitParameter> {

}
