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
 
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server;

import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.city.InfrastructureInitParameter;
import de.pgalise.simulation.shared.city.InfrastructureStartParameter;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 * Interface of the TrafficSensorController
 * 
 * @author Mischa
 * @author Lena
 * @version 1.0 (Oct 23, 2012)
 */
public interface TrafficSensorController<N extends TrafficNode<N,E,D,V>, E extends TrafficEdge<N,E,D,V>, D extends VehicleData, V extends Vehicle<D,N,E,V>, F extends TrafficEvent<N,E,D,V,F>> extends SensorManagerController<F, InfrastructureStartParameter, InfrastructureInitParameter> {
	
	public void onSchedule(V v);

	public void onUpdate(V vehicle, EventList<F> eventList);

	public void onRemove(V vehicle);
}
