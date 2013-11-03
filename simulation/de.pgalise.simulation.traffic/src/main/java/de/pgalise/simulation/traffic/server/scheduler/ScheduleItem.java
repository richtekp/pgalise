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
 
package de.pgalise.simulation.traffic.server.scheduler;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * An item represents a vehicle and its departure time.
 * 
 * @author Mustafa
 * @author Andreas Rehfeldt
 * @version 1.0
 */
public interface ScheduleItem<
	D extends VehicleData,
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	V extends Vehicle<D,N,E,V>> {


	/**
	 * The time the vehicle should be departured.
	 * 
	 * @return
	 */
	public long getDepartureTime() ;

	public void setDepartureTime(long time) ;

	public V getVehicle();

	/**
	 * @return the time the vehicle of this items has been updated
	 */
	public long getLastUpdate();

	public void setLastUpdate(long lastUpdate) ;

	/**
	 * First time the vehicle is driving.
	 * 
	 * @return
	 */
	public long getScheduleTime() ;

	public void setScheduleTime(long time) ;
}
