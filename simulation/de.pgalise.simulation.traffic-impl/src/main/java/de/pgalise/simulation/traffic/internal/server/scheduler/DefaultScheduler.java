/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.scheduler;

import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author richter
 */
public class DefaultScheduler {
	
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
	public void scheduleItem(DefaultScheduleItem item) throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onScheduleItem(item);
		} else {
			throw new IllegalAccessException("Can't schedule item, write access needed (current: " + modus + ")");
		}
	}

	protected abstract void onScheduleItem(DefaultScheduleItem item);

	/**
	 * Returns a shallow copy of vehicles should be driving already. Important: Does not remove vehicles that arrived at
	 * their target.
	 * 
	 * @see #removeExpiredItems(List)
	 * @return a shallow copy of vehicles should be driving already
	 */
	public abstract List<DefaultScheduleItem> getExpiredItems(long currentTime);

	/**
	 * @see #removeScheduledItems(List)
	 * @return a shallow copy of scheduled items
	 */
	public abstract List<DefaultScheduleItem> getScheduledItems();

	/**
	 * Searches for the passed vehicles in the expired items list and removes them.
	 * 
	 * @param vehicles
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeScheduledItems(List<V> vehicles) throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onRemoveScheduledItems(vehicles);
		} else {
			throw new IllegalAccessException("Can't remove scheduled items, write access needed (current: " + modus
					+ ")");
		}
	}

	protected abstract void onRemoveScheduledItems(List<V> vehicles);

	/**
	 * Searches for the passed vehicles in the expired items list and removes them.
	 * 
	 * @param vehicles
	 * @throws IllegalAccessException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeExpiredItems(List<V> vehicles) throws IllegalAccessException {
		if (this.modus == Modus.WRITE) {
			onRemoveExpiredItems(vehicles);
		} else {
			throw new IllegalAccessException("Can't remove expired items, write access needed (current: " + modus + ")");
		}
	}

	protected abstract void onRemoveExpiredItems(List<V> vehicles);

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
