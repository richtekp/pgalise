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

import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;


/**
 * The ScheduleHandler can be used to inform components about scheduling event
 * (e.g. scheduling of a new item or removal of an item)
 * @param <D> 
 * @param <N> 
 * @param <E> 
 * @param <V> 
 * @param <I> 
 * @author mustafa
 *
 */
public interface ScheduleHandler {
	public void onRemove(ScheduleItem v);
	public void onSchedule(ScheduleItem v);
}
