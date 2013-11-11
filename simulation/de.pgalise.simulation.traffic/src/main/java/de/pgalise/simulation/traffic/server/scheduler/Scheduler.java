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
import java.util.EnumSet;
import java.util.List;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * The Scheduler used by the TrafficServer to schedule vehicles whose departure times lie in the future.
 * A schedule plan consists of two lists storing expired Items and not expired Items.
 * On every update step the TrafficServer iterates through the expired items to update
 * the vehicles that are already on the road.
 * 
 * @param <D> 
 * @param <N> 
 * @param <E> 
 * @param <V> 
 * @param <I> 
 * @see Item
 * @see Administration
 * @author mustafa
 */
public interface Scheduler {

	/**
	 * Changes the access rights to this scheduler
	 * 
	 * @param modus
	 *            new access mode to set
	 */
	void changeModus(ScheduleModus modus);

	/**
	 * Adds a schedule handler to this scheduler.
	 * 
	 * @see ScheduleHandler
	 * @param handler
	 *            to add
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void addScheduleHandler(ScheduleHandler handler) throws IllegalStateException ;

	/**
	 * Removes a schedule handler to this scheduler.
	 * 
	 * @see ScheduleHandler
	 * @param handler
	 *            to remove
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeScheduleHandler(ScheduleHandler handler) ;

	/**
	 * Removes all schedule handler from this scheduler.
	 * 
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 * @see ScheduleHandler
	 */
	public void removeAllHandler() throws IllegalStateException;

	/**
	 * Adds an item to the proper position in the schedule.
	 * 
	 * @param item
	 *            to add to the schedule
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void scheduleItem(ScheduleItem item) throws IllegalStateException ;

	/**
	 * Returns a shallow copy of vehicles should be driving already. Important: Does not remove vehicles that arrived at
	 * their target.
	 * 
	 * @param currentTime 
	 * @see #removeExpiredItems(List)
	 * @return a shallow copy of vehicles should be driving already
	 */
	List<ScheduleItem> getExpiredItems(long currentTime);

	/**
	 * @see #removeScheduledItems(List)
	 * @return a shallow copy of scheduled items
	 */
	List<ScheduleItem> getScheduledItems();

	/**
	 * Searches for the passed vehicles in the expired items list and removes them.
	 * 
	 * @param vehicles
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeScheduledItems(List<Vehicle<?>> vehicles) throws IllegalStateException ;

	/**
	 * Searches for the passed vehicles in the expired items list and removes them.
	 * 
	 * @param vehicles
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeExpiredItems(List<Vehicle<?>> vehicles) throws IllegalStateException;

	/**
	 * Clears scheduled items.
	 * 
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void clearScheduledItems() throws IllegalStateException ;

	/**
	 * Clears expired items.
	 * 
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void clearExpiredItems() throws IllegalStateException ;
}
