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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.Administration;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;

/**
 * Implements a scheduler for the traffic. Uses a sorted list to schedule the items.
 * 
 * @author Andreas
 * @version 1.0 (Nov 8, 2012)
 */
public class TreeSetScheduler extends Scheduler {

	/**
	 * Items of the scheduler
	 */
	private TreeSet<DefaultScheduleItem> scheduledItems;

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
	private TreeSetScheduler() {
		this.scheduledItems = new TreeSet<>();
		this.vehicles = new ArrayList<>();
		handlerList = new LinkedList<>();
	}

	/**
	 * Creates an Administration with this scheduler
	 * 
	 * @return Administration
	 */
	public static Administration createInstance() {
		return new Administration(new TreeSetScheduler());
	}

	@Override
	protected void onScheduleItem(DefaultScheduleItem item) {
		this.scheduledItems.add(item);
		onSchedule(item);
	}

	@Override
	public List<DefaultScheduleItem> getExpiredItems(long currentTime) {
		List<DefaultScheduleItem> list = new ArrayList<>();
		for (DefaultScheduleItem item : this.scheduledItems) {
			// item has been expired
			if (currentTime >= item.getDepartureTime()) {
				if (item.getVehicle().getVehicleState() != VehicleStateEnum.DRIVING) {
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
				if (a == b) {
					i.remove();
					onRemove(item);
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
				if (a == b) {
					i.remove();
					onRemove(item);
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

	/**
	 * Sorted list for {@link Item}
	 * 
	 * @author Andreas Rehfeldt
	 * @version 1.0 (Feb 16, 2013)
	 */
	class SortedList<E> extends ArrayList<E> {

		/**
		 * Serial
		 */
		private static final long serialVersionUID = -3129995612402480528L;

		/**
		 * Internal list
		 */
		private ArrayList<E> internalList = new ArrayList<>();

		@Override
		public void add(int position, E e) {
			internalList.add(e);
			Collections.sort(internalList, null);
		}

		@Override
		public E get(int i) {
			return internalList.get(i);
		}

		@Override
		public int size() {
			return internalList.size();
		}

	}

}
