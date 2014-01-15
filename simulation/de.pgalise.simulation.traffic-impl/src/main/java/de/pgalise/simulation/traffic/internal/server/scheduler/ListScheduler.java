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
 
package de.pgalise.simulation.traffic.internal.server.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.Administration;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleModus;

/**
 * Implements a scheduler for the traffic. Uses a list to schedule the items.
 * 
 * @author Mustafa
 * @version 1.0 (Nov 8, 2012)
 */
public class ListScheduler extends BaseScheduler {
	/**
	 * Items of the scheduler
	 */
	private List<ScheduleItem> scheduledItems;

	/**
	 * List of vehicles
	 */
	private List<ScheduleItem> vehicles;

	/**
	 * List of handler
	 */
	private List<ScheduleHandler> handlerList;

	/**
	 * Default constructor
	 */
	private ListScheduler() {
		this.scheduledItems = new ArrayList<>();
		this.vehicles = new ArrayList<>();
		handlerList = new LinkedList<>();
	}

	/**
	 * Creates an Administration with this scheduler
	 * 
	 * @return Administration
	 */
	public static Administration createInstance() {
		return new Administration(new ListScheduler());
	}

	@Override
	public void onScheduleItem(ScheduleItem item) {
		int index = 0;
		for (Iterator<ScheduleItem> it = this.scheduledItems.iterator(); it.hasNext();) {
			ScheduleItem a = it.next();
			if (item.getDepartureTime() >= a.getDepartureTime()) {
				index = this.scheduledItems.indexOf(a) + 1;
			}
		}
		// log.debug("Scheduling item with ListScheduler (name=" + item.getVehicle().getName() + ", time="
		// + item.getDepartureTime() + "). Position in list: " + index);

		this.scheduledItems.add(index, item);
		onSchedule(item);
	}

	@Override
	public List<ScheduleItem> getExpiredItems(long currentTime) {
		List<ScheduleItem> list = new ArrayList<>();
		for (ScheduleItem item : this.scheduledItems) {
			// item has been expired
			if (currentTime >= item.getDepartureTime()) {
				if (item.getVehicle().getVehicleState() != VehicleStateEnum.DRIVING) {
					item.setScheduleTime(currentTime);
					// ListScheduler.log
					// .debug("Item (\"" + item.getVehicle().getName()
					// + "\") has been expired");
					// if (item.reversePath()) {
					// List<Node> path = item.getVehicle().getPath().getNodePath();
					// Collections.reverse(path);
					// List<Edge> edges = item.getVehicle().getPath().getEdgePath();
					// Collections.reverse(edges);
					// item.getVehicle().setPath(item.getVehicle().getPath());
					// }
					list.add(item);
				} else {
					// kann nicht zurückfahren, da noch gar nicht angekommen
					// abfahrzeit wird pauschal um 1 stunde erhöht
					long time = item.getDepartureTime();
					time += 1000 * 60 * 60;
					item.setDepartureTime(time);
				}
			}
		}
		this.scheduledItems.removeAll(list);
		this.vehicles.addAll(list);

		return new ArrayList<>(this.vehicles);
	}

	@Override
	public List<ScheduleItem> getScheduledItems() {
		return new ArrayList<>(this.scheduledItems);
	}

	@Override
	public void onRemoveScheduledItems(List<Vehicle<?>> vehicles) {
		for (Iterator<ScheduleItem> i = scheduledItems.iterator(); i.hasNext();) {
			ScheduleItem item = i.next();
			Vehicle<? extends VehicleData> a = item.getVehicle();
			for (Vehicle<? extends VehicleData> b : vehicles) {
				if (a.getId().equals(b.getId())) {
					i.remove();
					onRemove(item);
					break;
				}
			}
		}
	}

	@Override
	public void onClearScheduledItems() {
		for (Iterator<ScheduleItem> i = this.scheduledItems.iterator(); i.hasNext();) {
			ScheduleItem item = i.next();
			i.remove();
			onRemove(item);
		}
	}

	@Override
	public void onClearExpiredItems() {
		for (Iterator<ScheduleItem> i = this.vehicles.iterator(); i.hasNext();) {
			ScheduleItem item = i.next();
			i.remove();
			onRemove(item);
		}
	}

	@Override
	public void onAddScheduleHandler(ScheduleHandler handler) {
		handlerList.add(handler);
	}

	@Override
	public void onRemoveScheduleHandler(ScheduleHandler handler) {
		handlerList.remove(handler);
	}

	private void onRemove(ScheduleItem v) {
		for (ScheduleHandler s : handlerList) {
			s.onRemove(v);
		}
	}

	private void onSchedule(ScheduleItem v) {
		for (ScheduleHandler s : handlerList) {
			s.onSchedule(v);
		}
	}

	@Override
	public void changeModus(ScheduleModus modus) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void scheduleItem(ScheduleItem item) throws IllegalStateException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onRemoveAllHandler() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onRemoveExpiredItems(
		List<Vehicle<?>> vehicles) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
