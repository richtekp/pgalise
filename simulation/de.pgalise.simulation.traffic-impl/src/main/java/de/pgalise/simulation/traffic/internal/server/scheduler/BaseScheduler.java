/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.scheduler;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.scheduler.AbstractScheduler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleModus;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author richter
 */
public abstract class BaseScheduler extends AbstractScheduler implements Scheduler {
	
	private Modus modus;

	@Override
	public void changeModus(ScheduleModus modus) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void scheduleItem(ScheduleItem item) throws IllegalStateException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public static enum Modus {
		READ, WRITE;
		public static EnumSet<Modus> READ_OR_WRITE = EnumSet.of(READ, WRITE);
	}

	public BaseScheduler() {
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
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void addScheduleHandler(ScheduleHandler handler) throws IllegalStateException {
		if (this.modus == Modus.WRITE) {
			onAddScheduleHandler(handler);
		} else {
			throw new IllegalStateException("Can't add schedule handler, write access needed (current: " + modus + ")");
		}
	}

	protected abstract void onAddScheduleHandler(ScheduleHandler handler);

	/**
	 * Removes a schedule handler to this scheduler.
	 * 
	 * @see ScheduleHandler
	 * @param handler
	 *            to remove
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeScheduleHandler(ScheduleHandler handler) {
		if (this.modus == Modus.WRITE) {
			onRemoveScheduleHandler(handler);
		} else {
			throw new IllegalStateException("Can't remove schedule handler, write access needed (current: " + modus
					+ ")");
		}
	}

	protected abstract void onRemoveScheduleHandler(ScheduleHandler handler);

	/**
	 * Removes all schedule handler from this scheduler.
	 * 
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 * @see ScheduleHandler
	 */
	public void removeAllHandler() throws IllegalStateException {
		if (this.modus == Modus.WRITE) {
			onRemoveAllHandler();
		} else {
			throw new IllegalStateException("Can't remove all schedule handler, write access needed (current: "
					+ modus + ")");
		}
	}

	protected abstract void onRemoveAllHandler();

	/**
	 * Adds an item to the proper position in the schedule.
	 * 
	 * @param item
	 *            to add to the schedule
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void scheduleItem(DefaultScheduleItem item) throws IllegalStateException {
		if (this.modus == Modus.WRITE) {
			onScheduleItem(item);
		} else {
			throw new IllegalStateException("Can't schedule item, write access needed (current: " + modus + ")");
		}
	}

	protected abstract void onScheduleItem(ScheduleItem item);

	/**
	 * Returns a shallow copy of vehicles should be driving already. Important: Does not remove vehicles that arrived at
	 * their target.
	 * 
	 * @see #removeExpiredItems(List)
	 * @return a shallow copy of vehicles should be driving already
	 */
	@Override
	public abstract List<ScheduleItem> getExpiredItems(long currentTime);

	/**
	 * @see #removeScheduledItems(List)
	 * @return a shallow copy of scheduled items
	 */
	@Override
	public abstract List<ScheduleItem> getScheduledItems();

	/**
	 * Searches for the passed vehicles in the expired items list and removes them.
	 * 
	 * @param vehicles
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeScheduledItems(List<Vehicle<?>> vehicles) throws IllegalStateException {
		if (this.modus == Modus.WRITE) {
			onRemoveScheduledItems(vehicles);
		} else {
			throw new IllegalStateException("Can't remove scheduled items, write access needed (current: " + modus
					+ ")");
		}
	}

	protected abstract void onRemoveScheduledItems(List<Vehicle<?>> vehicles);

	/**
	 * Searches for the passed vehicles in the expired items list and removes them.
	 * 
	 * @param vehicles
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void removeExpiredItems(List<Vehicle<?>> vehicles) throws IllegalStateException {
		if (this.modus == Modus.WRITE) {
			onRemoveExpiredItems(vehicles);
		} else {
			throw new IllegalStateException("Can't remove expired items, write access needed (current: " + modus + ")");
		}
	}

	protected abstract void onRemoveExpiredItems(List<Vehicle<?>> vehicles);

	/**
	 * Clears scheduled items.
	 * 
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void clearScheduledItems() throws IllegalStateException {
		if (this.modus == Modus.WRITE) {
			onClearScheduledItems();
		} else {
			throw new IllegalStateException("Can't clear scheduled items list, write access needed (current: " + modus
					+ ")");
		}
	}

	protected abstract void onClearScheduledItems();

	/**
	 * Clears expired items.
	 * 
	 * @throws IllegalStateException
	 *             when access right is not set to {@link Modus#WRITE}
	 */
	public void clearExpiredItems() throws IllegalStateException {
		if (this.modus == Modus.WRITE) {
			onClearExpiredItems();
		} else {
			throw new IllegalStateException("Can't clear expired items list, write access needed (current: " + modus
					+ ")");
		}
	}

	@Override
	protected abstract void onClearExpiredItems();
}
