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
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.Administration;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;

/**
 * Implements a scheduler for the traffic. Uses a list to schedule the items.
 * 
 * @author Mustafa
 * @version 1.0 (Nov 8, 2012)
 */
public class ListScheduler extends Scheduler {
	/**
	 * Items of the scheduler
	 */
	private List<DefaultScheduleItem> scheduledItems;

	/**
	 * List of vehicles
	 */
	private List<DefaultScheduleItem> vehicles;

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
	protected void onScheduleItem(DefaultScheduleItem item) {
		int index = 0;
		for (Iterator<DefaultScheduleItem> it = this.scheduledItems.iterator(); it.hasNext();) {
			DefaultScheduleItem a = it.next();
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
	public List<DefaultScheduleItem> getExpiredItems(long currentTime) {
		List<DefaultScheduleItem> list = new ArrayList<>();
		for (DefaultScheduleItem item : this.scheduledItems) {
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
	public List<DefaultScheduleItem> getScheduledItems() {
		return new ArrayList<>(this.scheduledItems);
	}

	@Override
	protected void onRemoveScheduledItems(List<Vehicle<? extends VehicleData,N,E>> vehicles) {
		for (Iterator<DefaultScheduleItem> i = scheduledItems.iterator(); i.hasNext();) {
			DefaultScheduleItem item = i.next();
			Vehicle<? extends VehicleData,N,E> a = item.getVehicle();
			for (Vehicle<? extends VehicleData,N,E> b : vehicles) {
				if (a.getId().equals(b.getId())) {
					i.remove();
					onRemove(item);
					break;
				}
			}
		}
	}

	@Override
	protected void onRemoveExpiredItems(List<Vehicle<? extends VehicleData,N,E>> list) {
		for (Iterator<DefaultScheduleItem> i = vehicles.iterator(); i.hasNext();) {
			DefaultScheduleItem item = i.next();
			Vehicle<? extends VehicleData,N,E> a = item.getVehicle();
			for (Vehicle<? extends VehicleData,N,E> b : list) {
				if (a.getId().equals(b.getId())) {
					i.remove();
					onRemove(item);
					break;
				}
			}
		}
	}

	@Override
	protected void onClearScheduledItems() {
		for (Iterator<DefaultScheduleItem> i = this.scheduledItems.iterator(); i.hasNext();) {
			DefaultScheduleItem item = i.next();
			i.remove();
			onRemove(item);
		}
	}

	@Override
	protected void onClearExpiredItems() {
		for (Iterator<DefaultScheduleItem> i = this.vehicles.iterator(); i.hasNext();) {
			DefaultScheduleItem item = i.next();
			i.remove();
			onRemove(item);
		}
	}

	@Override
	protected void onAddScheduleHandler(ScheduleHandler handler) {
		handlerList.add(handler);
	}

	@Override
	protected void onRemoveScheduleHandler(ScheduleHandler handler) {
		handlerList.remove(handler);
	}

	private void onRemove(DefaultScheduleItem v) {
		for (ScheduleHandler s : handlerList) {
			s.onRemove(v);
		}
	}

	private void onSchedule(DefaultScheduleItem v) {
		for (ScheduleHandler s : handlerList) {
			s.onSchedule(v);
		}
	}

	@Override
	protected void onRemoveAllHandler() {
		for (Iterator<ScheduleHandler> i = this.handlerList.iterator(); i.hasNext();) {
			i.remove();
		}
	}

}
