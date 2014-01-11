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
import de.pgalise.simulation.staticsensor.StaticSensor;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import java.util.Set;

/**
 * Remote view of the traffic server.<br/><br/>
 * In order to exploit the benefits of a distributed architecture it is
 * necessary to divide the city in distinct areas when it comes to the traffic
 * aspect of the simulation. A TrafficServer takes care of one of these areas.
 * The TrafficServers are usually managed by the TrafficController.
 *
 * @param <E>
 * @see de.pgalise.simulation.traffic.TrafficController
 * @author mustafa
 */
public interface TrafficServer<E extends TrafficEvent> extends
	SensorManagerController<E, TrafficStartParameter, TrafficInitParameter, StaticSensor<?,?>> {

	/**
	 * Sets the city zone this server is responsible for.
	 *
	 * @param cityZone city zone to be set
	 */
	public void setCityZone(Geometry cityZone);

	/**
	 * @return the city zone this server is responsible for
	 */
	public Geometry getCityZone();

	/**
	 * Let this server take care of the passed vehicle which came from area of
	 * another server.
	 *
	 * @param vehicle
	 * @param startNodeId
	 * @param origin the server the vehicle came from or <code>null</code> if the
	 * vehicle is passed to a server for the first time
	 * @param targetNodeId
	 */
	public void takeVehicle(Vehicle<?> vehicle,
		TrafficNode startNodeId,
		TrafficNode targetNodeId,
		TrafficServerLocal<E> origin);

	/**
	 * Let this server process the previously received vehicles from other
	 * servers.
	 */
	public void processMovedVehicles();

	public Set<Vehicle<?>> getManagedVehicles();

	void setTrafficServers(Set<TrafficServerLocal<VehicleEvent>> trafficServer);
}
