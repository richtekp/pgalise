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
 * @see Item
 * @see Administration
 * @author mustafa
 */
public abstract class Scheduler {
	private Modus modus;

	public static enum Modus {
		READ, WRITE;
		public static EnumSet<Modus> READ_OR_WRITE = EnumSet.of(READ, WRITE);
	}

	public Scheduler() {
		this.modus = Modus.READ;
	}

	/**
	 * Changes the access rights to this scheduler
	 * 
	 * @param modus
	 *            new access mode to set
	 */
	void changeModus(Modus modus) {
		this.modus = modus;
	}

	/**
	 * Adds a schedule handler to this scheduler.
	 * 
	 * @see ScheduleHandler
	 * @param handler
	 *            to add
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void addScheduleHandler(ScheduleHandler handler) throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onAddScheduleHandler(handler);
		} else {
			throw new IllegalAccessException("Can't add schedule handler, write access needed (current: " + modus + ")");
		}
	}

	protected abstract void onAddScheduleHandler(ScheduleHandler handler);

	/**
	 * Removes a schedule handler to this scheduler.
	 * 
	 * @see ScheduleHandler
	 * @param handler
	 *            to remove
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeScheduleHandler(ScheduleHandler handler) throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onRemoveScheduleHandler(handler);
		} else {
			throw new IllegalAccessException("Can't remove schedule handler, write access needed (current: " + modus
					+ ")");
		}
	}

	protected abstract void onRemoveScheduleHandler(ScheduleHandler handler);

	/**
	 * Removes all schedule handler from this scheduler.
	 * 
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 * @see ScheduleHandler
	 */
	public void removeAllHandler() throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onRemoveAllHandler();
		} else {
			throw new IllegalAccessException("Can't remove all schedule handler, write access needed (current: "
					+ modus + ")");
		}
	}

	protected abstract void onRemoveAllHandler();

	/**
	 * Adds an item to the proper position in the schedule.
	 * 
	 * @param item
	 *            to add to the schedule
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void scheduleItem(Item item) throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onScheduleItem(item);
		} else {
			throw new IllegalAccessException("Can't schedule item, write access needed (current: " + modus + ")");
		}
	}

	protected abstract void onScheduleItem(Item item);

	/**
	 * Returns a shallow copy of vehicles should be driving already. Important: Does not remove vehicles that arrived at
	 * their target.
	 * 
	 * @see #removeExpiredItems(List)
	 * @return a shallow copy of vehicles should be driving already
	 */
	public abstract List<Item> getExpiredItems(long currentTime);

	/**
	 * @see #removeScheduledItems(List)
	 * @return a shallow copy of scheduled items
	 */
	public abstract List<Item> getScheduledItems();

	/**
	 * Searches for the passed vehicles in the expired items list and removes them.
	 * 
	 * @param vehicles
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeScheduledItems(List<Vehicle<? extends VehicleData>> vehicles) throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onRemoveScheduledItems(vehicles);
		} else {
			throw new IllegalAccessException("Can't remove scheduled items, write access needed (current: " + modus
					+ ")");
		}
	}

	protected abstract void onRemoveScheduledItems(List<Vehicle<? extends VehicleData>> vehicles);

	/**
	 * Searches for the passed vehicles in the expired items list and removes them.
	 * 
	 * @param vehicles
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeExpiredItems(List<Vehicle<? extends VehicleData>> vehicles) throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onRemoveExpiredItems(vehicles);
		} else {
			throw new IllegalAccessException("Can't remove expired items, write access needed (current: " + modus + ")");
		}
	}

	protected abstract void onRemoveExpiredItems(List<Vehicle<? extends VehicleData>> vehicles);

	/**
	 * Clears scheduled items.
	 * 
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void clearScheduledItems() throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onClearScheduledItems();
		} else {
			throw new IllegalAccessException("Can't clear scheduled items list, write access needed (current: " + modus
					+ ")");
		}
	}

	protected abstract void onClearScheduledItems();

	/**
	 * Clears expired items.
	 * 
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void clearExpiredItems() throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onClearExpiredItems();
		} else {
			throw new IllegalAccessException("Can't clear expired items list, write access needed (current: " + modus
					+ ")");
		}
	}

	protected abstract void onClearExpiredItems();
}
