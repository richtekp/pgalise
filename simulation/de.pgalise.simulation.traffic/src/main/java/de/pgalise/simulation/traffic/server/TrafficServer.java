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
 
package de.pgalise.simulation.traffic.server;

import de.pgalise.simulation.sensorFramework.SensorManagerController;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 * Remote view of the traffic server.<br/><br/>
 * In order to exploit the benefits of a distributed architecture it is necessary 
 * to divide the city in distinct areas when it comes to the traffic aspect of the simulation. 
 * A TrafficServer takes care of one of these areas.
 * The TrafficServers are usually managed by the TrafficController.
 *  
 * @param <E> 
 * @see de.pgalise.simulation.traffic.TrafficController
 * @author mustafa
 */
public interface TrafficServer<E extends TrafficEvent> extends SensorManagerController<E, StartParameter, InitParameter> {
	/**
	 * Sets the city zone this server is responsible for.
	 * 
	 * @param cityZone
	 *            city zone to be set
	 */
	public void setCityZone(Geometry cityZone);

	/**
	 * @return the city zone this server is responsible for
	 */
	public Geometry getCityZone();

	/**
	 * Let this server take care of the passed vehicle.
	 * 
	 * @param vehicle 
	 * @param startNodeId
	 * @param serverId 
	 * @param targetNodeId
	 */
	public void takeVehicle(Vehicle<?> vehicle, NavigationNode startNodeId, NavigationNode targetNodeId,
			TrafficServer<?> serverId);

	/**
	 * Let this server process the previously received vehicles from other servers.
	 */
	public void processMovedVehicles();
	
//	public void setServerId(int serverId);
//	
//	/**
//	 * The id of this server given by the TrafficController.
//	 * @return serverId
//	 */
//	public int getServerId();
}
