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
 
package de.pgalise.util.GTFS.service;

import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.BusTrip;
import java.util.List;


/**
 * 
 * @author Lena
 *
 */
public interface BusService {
	/**
	 * Returns the bustrips of a specific busline
	 * 
	 * @param busLine
	 * @param time
	 * @return
	 */
	public List<BusTrip> getBusLineData(BusRoute busLine, long time);

	/**
	 * Returns the total number of bus trips for the given buslines an the actual day
	 * 
	 * @param busLines
	 * @param time
	 * @return
	 */
	public int getTotalNumberOfBusTrips(List<BusRoute> busLines, long time);
	
	/**
	 * Returns all existing busroutes of the gtfs file
	 * @return a list with BusRoutes
	 */
	public List<BusRoute> getAllBusRoutes() ;
	
}
