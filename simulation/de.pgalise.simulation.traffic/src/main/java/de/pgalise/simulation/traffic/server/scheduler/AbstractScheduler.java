/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.server.scheduler;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import java.util.List;

/**
 *
 * @author richter
 */
public abstract class AbstractScheduler implements Scheduler {
	
  private static final long serialVersionUID = 1L;

	protected abstract void onAddScheduleHandler(ScheduleHandler handler);

	protected abstract void onRemoveScheduleHandler(ScheduleHandler handler);

	protected abstract void onRemoveAllHandler();

	protected abstract void onScheduleItem(ScheduleItem item);

	protected abstract void onRemoveScheduledItems(List<Vehicle<?>> vehicles);

	protected abstract void onClearScheduledItems();

	protected abstract void onClearExpiredItems();

	protected abstract void onRemoveExpiredItems(List<Vehicle<?>> vehicles);
}
